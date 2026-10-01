package nz.coreyh.linkr.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import nz.coreyh.linkr.application.port.`in`.DeleteLinkUseCase
import nz.coreyh.linkr.application.port.`in`.result.DeleteLinkResult
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.domain.model.ShortCode

private val logger = KotlinLogging.logger {}

class DeleteLinkService(
    private val linkRepository: LinkRepository,
) : DeleteLinkUseCase {
    override fun delete(rawCode: String): DeleteLinkResult {
        logger.trace { "delete called rawCode=$rawCode" }

        val code =
            ShortCode.parse(rawCode) ?: run {
                logger.debug { "Delete rejected, rawCode=$rawCode is not a valid short code" }
                return DeleteLinkResult.NotFound
            }

        val result = if (linkRepository.delete(code)) DeleteLinkResult.Deleted else DeleteLinkResult.NotFound
        logger.debug { "Delete code=$code -> $result" }
        return result
    }
}
