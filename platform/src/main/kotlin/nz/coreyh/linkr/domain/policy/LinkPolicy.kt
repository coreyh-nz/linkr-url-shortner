package nz.coreyh.linkr.domain.policy

import nz.coreyh.linkr.domain.model.ShortLink
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object LinkPolicy {
    val MAX_TTL: Duration = 365.days

    fun isTtlAllowed(ttl: Duration?): Boolean = ttl == null || (ttl.isPositive() && ttl <= MAX_TTL)

    fun expiresAtFrom(
        now: Instant,
        ttl: Duration?,
    ): Instant? = ttl?.let { now.plus(it) }

    fun isActive(
        link: ShortLink,
        now: Instant,
    ): Boolean = !link.isExpiredAt(now)
}
