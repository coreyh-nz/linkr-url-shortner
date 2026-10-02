package nz.coreyh.linkr.application.port.`in`

import nz.coreyh.linkr.application.port.`in`.result.DeleteLinkResult

/** Inbound port: deletes a short link by its code. */
interface DeleteLinkUseCase {
    fun delete(rawCode: String): DeleteLinkResult
}
