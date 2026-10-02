package nz.coreyh.linkr.application.port.`in`

import nz.coreyh.linkr.application.port.`in`.command.ShortenUrlCommand
import nz.coreyh.linkr.application.port.`in`.result.ShortenUrlResult

/** Inbound port: creates a short link for a target URL. */
interface ShortenUrlUseCase {
    fun shorten(command: ShortenUrlCommand): ShortenUrlResult
}
