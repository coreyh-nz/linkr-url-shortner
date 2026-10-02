package nz.coreyh.linkr.application.port.`in`.result

sealed interface DeleteLinkResult {
    data object Deleted : DeleteLinkResult

    data object NotFound : DeleteLinkResult
}
