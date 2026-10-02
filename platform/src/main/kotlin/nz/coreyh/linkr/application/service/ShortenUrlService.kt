package nz.coreyh.linkr.application.service

import io.github.oshai.kotlinlogging.KotlinLogging
import nz.coreyh.linkr.application.port.`in`.ShortenUrlUseCase
import nz.coreyh.linkr.application.port.`in`.command.ShortenUrlCommand
import nz.coreyh.linkr.application.port.`in`.result.ShortenUrlResult
import nz.coreyh.linkr.application.port.out.ClockPort
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.application.port.out.ShortCodeGenerator
import nz.coreyh.linkr.application.port.out.result.SaveLinkResult
import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.ShortLink
import nz.coreyh.linkr.domain.model.TargetUrl
import nz.coreyh.linkr.domain.policy.LinkPolicy

private val logger = KotlinLogging.logger {}

class ShortenUrlService(
    private val linkRepository: LinkRepository,
    private val shortCodeGenerator: ShortCodeGenerator,
    private val clock: ClockPort,
    private val maxAttempts: Int,
) : ShortenUrlUseCase {
    init {
        require(maxAttempts > 0) { "maxAttempts must be positive, was $maxAttempts" }
    }

    override fun shorten(command: ShortenUrlCommand): ShortenUrlResult {
        logger.trace { "shorten called with $command" }

        val target =
            TargetUrl.parse(command.rawUrl) ?: run {
                logger.debug { "Shorten rejected, invalid URL rawUrl=${command.rawUrl}" }
                return ShortenUrlResult.InvalidUrl
            }

        if (!LinkPolicy.isTtlAllowed(command.ttl)) {
            logger.debug { "Shorten rejected, ttl=${command.ttl} outside allowed range (max=${LinkPolicy.MAX_TTL})" }
            return ShortenUrlResult.InvalidTtl
        }

        val now = clock.now()
        val expiresAt = LinkPolicy.expiresAtFrom(now, command.ttl)
        logger.trace { "Computed expiresAt=$expiresAt from now=$now ttl=${command.ttl}" }

        val customCode = command.customCode
        if (customCode != null) {
            val code =
                ShortCode.parse(customCode) ?: run {
                    logger.debug { "Shorten rejected, invalid custom code=$customCode" }
                    return ShortenUrlResult.InvalidCode
                }
            logger.trace { "Using custom code=$code" }
            return save(ShortLink(code, target, now, expiresAt))
        }

        repeat(maxAttempts) { attempt ->
            val link = ShortLink(shortCodeGenerator.generate(target, attempt), target, now, expiresAt)
            val result = save(link)
            if (result !is ShortenUrlResult.CodeTaken) return result
            logger.debug { "Generated code=${link.code} collided (attempt ${attempt + 1}/$maxAttempts), retrying" }
        }

        logger.debug { "Shorten failed, no free code after $maxAttempts attempts for target=$target" }
        return ShortenUrlResult.CouldNotGenerateCode
    }

    private fun save(link: ShortLink): ShortenUrlResult =
        when (linkRepository.save(link)) {
            SaveLinkResult.Saved -> {
                logger.debug { "Created link code=${link.code} -> ${link.target} (expiresAt=${link.expiresAt})" }
                ShortenUrlResult.Created(link)
            }

            SaveLinkResult.CodeTaken -> {
                logger.trace { "Save reported code=${link.code} already taken" }
                ShortenUrlResult.CodeTaken
            }
        }
}
