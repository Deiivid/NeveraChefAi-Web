package es.neverachefai.neverachefai

actual fun openExternalUrl(url: String) {
    js("window.open(url, '_blank', 'noopener,noreferrer')")
}
