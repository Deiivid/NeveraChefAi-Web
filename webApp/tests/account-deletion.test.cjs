const { test } = require("node:test");
const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { resolve } = require("node:path");
const { runInNewContext } = require("node:vm");

const script = readFileSync(resolve(__dirname, "../src/webMain/resources/account-deletion.js"), "utf8");
const baseConfig = {
    supabaseUrl: "https://test-project.supabase.co",
    publishableKey: "sb_publishable_test",
};
const response = (status, body = {}) => ({ status, ok: status >= 200 && status < 300, json: async () => body });
const session = () => response(200, { user: { id: "test-user" }, access_token: "temporary-token" });

function setup(responses = [], config = baseConfig) {
    const elements = Object.fromEntries([
        "delete-account-form", "account-fields", "password", "delete-button", "form-status",
        "email", "configuration-notice", "retention-notice", "confirmation",
    ].map(id => [id, {
        value: "", disabled: id === "account-fields", hidden: false, textContent: "", dataset: {},
        checked: false, attributes: {},
        setAttribute(name, value) { this.attributes[name] = value; },
        removeAttribute(name) { delete this.attributes[name]; },
        focus() { this.focused = true; },
    }]));
    elements["configuration-notice"].hidden = true;
    elements.email.value = "person@example.test";
    elements.password.value = "entered-password";
    elements.confirmation.checked = true;
    elements["delete-button"].textContent = "Eliminar mi cuenta definitivamente";
    const form = elements["delete-account-form"];
    form.addEventListener = (_, handler) => { form.submit = () => handler({ preventDefault() {} }); };
    form.reportValidity = () => elements.confirmation.checked && !!elements.email.value && !!elements.password.value;
    form.reset = () => {
        elements.email.value = elements.password.value = "";
        elements.confirmation.checked = false;
    };
    const requests = [];
    const fetch = async (url, options) => {
        requests.push({ url, options });
        const next = responses.shift();
        if (next instanceof Error) throw next;
        if (typeof next === "function") return next();
        assert.ok(next, "Unexpected network request");
        return next;
    };
    runInNewContext(script, {
        window: { NEVERACHEF_ACCOUNT_CONFIG: config }, document: { getElementById: id => elements[id] },
        URL, atob, fetch, AbortSignal,
    });
    return { elements, form, requests, status: elements["form-status"] };
}

test("missing or secret configuration never enables the form", () => {
    for (const config of [{}, { ...baseConfig, publishableKey: "sb_secret_forbidden" },
        { ...baseConfig, supabaseUrl: "http://test-project.supabase.co" },
        { ...baseConfig, supabaseUrl: "https://test-project.supabase.co.evil.test" },
        { ...baseConfig, supabaseUrl: "https://user@test-project.supabase.co" }]) {
        const { elements, requests } = setup([], config);
        assert.equal(elements["account-fields"].disabled, true);
        assert.equal(elements["configuration-notice"].hidden, false);
        assert.equal(requests.length, 0);
    }
});

test("confirmation is required before sending credentials", async () => {
    const { elements, form, requests } = setup();
    elements.confirmation.checked = false;
    await form.submit();
    assert.equal(requests.length, 0);
});

test("invalid credentials cannot trigger deletion and clear the password", async () => {
    const { elements, form, requests, status } = setup([response(400)]);
    await form.submit();
    assert.equal(requests.length, 1);
    assert.equal(elements.password.value, "");
    assert.equal(status.dataset.state, "error");
    assert.match(status.textContent, /Revisa el correo/);
    assert.equal(elements["account-fields"].disabled, false);
});

test("rate limits are explained without attempting deletion", async () => {
    const { form, requests, status } = setup([response(429)]);
    await form.submit();
    assert.equal(requests.length, 1);
    assert.match(status.textContent, /Demasiados intentos/);
});

test("only a confirmed 204 hides the form and reports deletion", async () => {
    const { elements, form, requests, status } = setup([session(), response(204)]);
    await form.submit();
    assert.equal(requests.length, 2);
    assert.equal(requests[1].options.headers.Authorization, "Bearer temporary-token");
    assert.equal(requests[1].options.body, undefined);
    assert.equal(requests[1].options.credentials, "omit");
    assert.equal(requests[1].options.redirect, "error");
    assert.equal(form.hidden, true);
    assert.equal(elements.password.value, "");
    assert.equal(status.dataset.state, "success");
    assert.ok(!JSON.stringify(requests[1]).includes("entered-password"));
});

test("shared ownership errors explain the next step and revoke the web session", async () => {
    const { form, requests, status } = setup([
        session(), response(409, { error: "ownership_transfer_required" }), response(204),
    ]);
    await form.submit();
    assert.match(status.textContent, /transferir la propiedad/);
    assert.equal(status.dataset.state, "error");
    assert.equal(form.hidden, false);
    assert.match(requests[2].url, /logout\?scope=local$/);
});

test("unexpected successful responses cannot falsely claim deletion", async () => {
    const { form, status } = setup([session(), response(200, {}), response(204)]);
    await form.submit();
    assert.equal(status.dataset.state, "error");
    assert.equal(form.hidden, false);
});

test("network loss during deletion reports an unknown outcome", async () => {
    const { form, status } = setup([session(), new Error("network"), response(204)]);
    await form.submit();
    assert.match(status.textContent, /no podemos confirmar si se completó/);
    assert.equal(status.dataset.state, "error");
});

test("network loss during sign-in cannot show a success", async () => {
    const { form, requests, status } = setup([new Error("network")]);
    await form.submit();
    assert.equal(requests.length, 1);
    assert.match(status.textContent, /No se pudo conectar/);
});

test("duplicate submits cannot create concurrent deletion requests", async () => {
    let resolveLogin;
    const { form, requests } = setup([
        () => new Promise(resolve => { resolveLogin = resolve; }), response(204),
    ]);
    const pending = form.submit();
    await form.submit();
    assert.equal(requests.length, 1);
    resolveLogin(session());
    await pending;
    assert.equal(requests.length, 2);
});

test("invalid login responses cannot invoke deletion", async () => {
    const { form, requests, status } = setup([response(200, { access_token: "token" }), response(204)]);
    await form.submit();
    assert.equal(requests.filter(request => request.url.includes("/delete-account")).length, 0);
    assert.equal(status.dataset.state, "error");
});

test("retention overrides are rendered as text", () => {
    const { elements } = setup([], { ...baseConfig, retentionNotice: "<script>unsafe</script>" });
    assert.equal(elements["retention-notice"].textContent, "<script>unsafe</script>");
});
