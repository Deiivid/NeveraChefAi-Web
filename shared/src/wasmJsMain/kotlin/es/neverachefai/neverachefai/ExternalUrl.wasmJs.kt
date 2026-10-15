package es.neverachefai.neverachefai

actual fun openExternalUrl(url: String) {
    // Intentionally empty until this site ships a Wasm-specific browser bridge.
}

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
actual fun openAccountDeletionPage(): Unit = js("window.location.assign('delete-account.html')")
