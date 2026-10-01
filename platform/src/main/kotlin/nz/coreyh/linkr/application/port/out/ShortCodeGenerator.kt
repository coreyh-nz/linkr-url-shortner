package nz.coreyh.linkr.application.port.out

import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.TargetUrl

/** Outbound port: generates a short code for a target URL. */
interface ShortCodeGenerator {
    /**
     * @param attempt increments on each retry within one shorten call, so
     *    strategies that would otherwise be deterministic (e.g. hash-based)
     *    can vary the output.
     */
    fun generate(
        target: TargetUrl,
        attempt: Int = 0,
    ): ShortCode
}
