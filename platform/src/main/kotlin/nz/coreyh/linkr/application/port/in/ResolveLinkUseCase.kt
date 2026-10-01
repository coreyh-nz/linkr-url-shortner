package nz.coreyh.linkr.application.port.`in`

import nz.coreyh.linkr.application.port.`in`.result.ResolveLinkResult

/** Inbound port: resolves a short code to its redirect target. */
interface ResolveLinkUseCase {
    fun resolve(rawCode: String): ResolveLinkResult
}
