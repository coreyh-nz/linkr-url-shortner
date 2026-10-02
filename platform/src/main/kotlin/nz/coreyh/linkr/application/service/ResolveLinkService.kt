package nz.coreyh.linkr.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import nz.coreyh.linkr.application.port.`in`.ResolveLinkUseCase
import nz.coreyh.linkr.application.port.`in`.result.ResolveLinkResult
import nz.coreyh.linkr.application.port.out.ClockPort
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.domain.model.ShortCode

private val logger = KotlinLogging.logger {}

class ResolveLinkService(
    private val linkRepository: LinkRepository,
    private val clock: ClockPort,
) : ResolveLinkUseCase {
    override fun resolve(rawCode: String): ResolveLinkResult {
        logger.trace { "resolve called rawCode=$rawCode" }

        val code =
            ShortCode.parse(rawCode) ?: run {
                logger.debug { "Resolve rejected, rawCode=$rawCode is not a valid short code" }
                return ResolveLinkResult.NotFound
            }

        val link =
            linkRepository.findByCode(code) ?: run {
                logger.debug { "Resolve code=$code -> not found" }
                return ResolveLinkResult.NotFound
            }
        logger.trace { "Loaded link $link" }

        val now = clock.now()
        if (link.isExpiredAt(now)) {
            logger.debug { "Resolve code=$code -> expired (expiresAt=${link.expiresAt}, now=$now)" }
            return ResolveLinkResult.Expired
        }

        logger.debug { "Resolve code=$code -> ${link.target}" }
        return ResolveLinkResult.Found(link.target)
    }
}
