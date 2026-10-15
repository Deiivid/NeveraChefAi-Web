"use strict";

(() => {
    const config = window.NEVERACHEF_ACCOUNT_CONFIG || {};
    const form = document.getElementById("delete-account-form");
    const fields = document.getElementById("account-fields");
    const password = document.getElementById("password");
    const button = document.getElementById("delete-button");
    const status = document.getElementById("form-status");
    const defaultButtonLabel = button.textContent;
    let baseUrl;
    let busy = false;

    try {
        const url = new URL(config.supabaseUrl);
        // Credentials must only go to the configured Supabase project over HTTPS.
        if (url.protocol !== "https:" || !url.hostname.endsWith(".supabase.co") ||
            url.username || url.password || url.port || url.pathname !== "/" || url.search || url.hash) {
            throw new Error("invalid_configuration");
        }
        const key = config.publishableKey;
        let publicKey = typeof key === "string" && /^sb_publishable_[A-Za-z0-9_-]+$/.test(key);
        if (!publicKey && typeof key === "string") {
            const payload = key.split(".")[1];
            if (payload) {
                const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
                publicKey = JSON.parse(atob(normalized)).role === "anon";
            }
        }
        if (!publicKey) throw new Error("invalid_configuration");
        baseUrl = url.origin;
        fields.disabled = false;
    } catch {
        document.getElementById("configuration-notice").hidden = false;
    }

    if (typeof config.retentionNotice === "string" && config.retentionNotice.trim()) {
        document.getElementById("retention-notice").textContent = config.retentionNotice;
    }

    function showStatus(message, state) {
        status.textContent = message;
        status.dataset.state = state;
        status.focus();
    }

    async function request(path, options) {
        return fetch(`${baseUrl}${path}`, {
            ...options,
            cache: "no-store",
            credentials: "omit",
            redirect: "error",
            referrerPolicy: "no-referrer",
        });
    }

    async function jsonBody(response) {
        try { return await response.json(); } catch { return {}; }
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();
        if (busy || !baseUrl || !form.reportValidity()) return;
        // Snapshot the entered password before disabling and clearing the field.
        let enteredPassword = password.value;
        const email = document.getElementById("email").value.trim();
        let accessToken;
        let deleted = false;
        let deletionAttempted = false;
        busy = true;
        fields.disabled = true;
        form.setAttribute("aria-busy", "true");
        button.textContent = "Verificando tu cuenta…";
        status.textContent = "";
        password.value = "";

        try {
            const login = await request("/auth/v1/token?grant_type=password", {
                method: "POST",
                headers: { apikey: config.publishableKey, "Content-Type": "application/json" },
                body: JSON.stringify({ email, password: enteredPassword }),
            });
            enteredPassword = "";
            const session = await jsonBody(login);
            if (!login.ok) {
                const message = login.status === 429
                    ? "Demasiados intentos. Espera unos minutos antes de volver a intentarlo."
                    : login.status >= 500
                        ? "No se pudo verificar tu cuenta. Inténtalo más tarde o solicita ayuda por correo."
                        : "No se pudo iniciar sesión. Revisa el correo y la contraseña. Si utilizas Google o Apple, solicita la eliminación por correo.";
                showStatus(message, "error");
                return;
            }
            accessToken = session.access_token;
            if (session.user?.id && typeof accessToken === "string" && accessToken) {
                button.textContent = "Eliminando tu cuenta…";
                deletionAttempted = true;
                const result = await request("/functions/v1/delete-account", {
                    method: "POST",
                    headers: { apikey: config.publishableKey, Authorization: `Bearer ${accessToken}` },
                });
                if (result.status !== 204) {
                    const error = await jsonBody(result);
                    const message = error.error === "ownership_transfer_required"
                        ? "Antes de eliminar tu cuenta debes transferir la propiedad de tu hogar compartido a otro miembro desde la app. Si no puedes hacerlo, solicita ayuda por correo."
                        : result.status === 401 || result.status === 403
                            ? "No se ha autorizado la eliminación. Inténtalo de nuevo o solicita ayuda por correo."
                            : "No se pudo confirmar la eliminación. Puedes volver a intentarlo o solicitar ayuda por correo.";
                    showStatus(message, "error");
                    return;
                }
                deleted = true;
                form.reset();
                form.hidden = true;
                showStatus("Tu cuenta de NeveraChefAI y los datos asociados indicados en esta página se han eliminado. Recuerda borrar también los datos locales de tus dispositivos.", "success");
            } else {
                showStatus("No se pudo verificar tu sesión. Inténtalo de nuevo o solicita ayuda por correo.", "error");
            }
        } catch {
            showStatus(deletionAttempted
                ? "Se ha interrumpido la conexión y no podemos confirmar si se completó la eliminación. Contacta con neverachefai@gmail.com antes de volver a intentarlo."
                : "No se pudo conectar con el servicio. Comprueba tu conexión o solicita la eliminación por correo.", "error");
        } finally {
            enteredPassword = "";
            password.value = "";
            // Revoke only this temporary web session; never persist its tokens.
            if (accessToken && !deleted) {
                try {
                    await request("/auth/v1/logout?scope=local", {
                        method: "POST",
                        headers: { apikey: config.publishableKey, Authorization: `Bearer ${accessToken}` },
                        signal: AbortSignal.timeout(5000),
                    });
                } catch { /* The session is still discarded locally on failure. */ }
            }
            accessToken = undefined;
            busy = false;
            fields.disabled = deleted;
            form.removeAttribute("aria-busy");
            button.textContent = defaultButtonLabel;
        }
    });
})();
