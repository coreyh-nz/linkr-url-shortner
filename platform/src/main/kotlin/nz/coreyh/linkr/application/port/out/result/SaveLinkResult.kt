package nz.coreyh.linkr.application.port.out.result

sealed interface SaveLinkResult {
    data object Saved : SaveLinkResult

    data object CodeTaken : SaveLinkResult
}
