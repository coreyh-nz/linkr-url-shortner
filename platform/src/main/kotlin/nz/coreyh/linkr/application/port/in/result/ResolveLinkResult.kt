package nz.coreyh.linkr.application.port.`in`.result

import nz.coreyh.linkr.domain.model.TargetUrl

sealed interface ResolveLinkResult {
    data class Found(
        val target: TargetUrl,
    ) : ResolveLinkResult

    data object NotFound : ResolveLinkResult

    data object Expired : ResolveLinkResult
}
