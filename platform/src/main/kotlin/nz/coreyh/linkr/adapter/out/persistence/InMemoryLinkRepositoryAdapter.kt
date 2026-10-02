package nz.coreyh.linkr.adapter.out.persistence

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.concurrent.ConcurrentHashMap
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.application.port.out.result.SaveLinkResult
import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.ShortLink

private val logger = KotlinLogging.logger {}

class InMemoryLinkRepositoryAdapter : LinkRepository {
    private val links = ConcurrentHashMap<ShortCode, ShortLink>()

    override fun save(link: ShortLink): SaveLinkResult {
        if (links.putIfAbsent(link.code, link) != null) {
            logger.trace { "Save rejected, code=${link.code} already taken" }
            return SaveLinkResult.CodeTaken
        }
        logger.trace { "Saved link code=${link.code} (store size=${links.size})" }
        return SaveLinkResult.Saved
    }

    override fun findByCode(code: ShortCode): ShortLink? =
        links[code]
            .also { logger.trace { "findByCode code=$code found=${it != null}" } }

    override fun existsByCode(code: ShortCode): Boolean = links.containsKey(code)

    override fun delete(code: ShortCode): Boolean =
        (links.remove(code) != null)
            .also { logger.trace { "delete code=$code removed=$it" } }
}
