package es.neverachefai.neverachefai

actual fun openExternalUrl(url: String) {
    js("window.open(url, '_blank', 'noopener,noreferrer')")
}

actual fun openAccountDeletionPage() {
    js("window.location.assign('delete-account.html')")
}
