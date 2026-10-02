package nz.coreyh.linkr.application.port.out

import nz.coreyh.linkr.application.port.out.result.SaveLinkResult
import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.ShortLink

/** Outbound port: persistence for short links. */
interface LinkRepository {
    fun save(link: ShortLink): SaveLinkResult

    fun findByCode(code: ShortCode): ShortLink?

    fun existsByCode(code: ShortCode): Boolean

    /** @return true if a link with [code] was deleted, false if none existed. */
    fun delete(code: ShortCode): Boolean
}
