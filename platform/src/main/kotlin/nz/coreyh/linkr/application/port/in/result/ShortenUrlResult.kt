package nz.coreyh.linkr.application.port.`in`.result

import nz.coreyh.linkr.domain.model.ShortLink

sealed interface ShortenUrlResult {
    data class Created(
        val link: ShortLink,
    ) : ShortenUrlResult

    data object InvalidUrl : ShortenUrlResult

    data object InvalidCode : ShortenUrlResult

    data object InvalidTtl : ShortenUrlResult

    data object CodeTaken : ShortenUrlResult

    data object CouldNotGenerateCode : ShortenUrlResult
}
