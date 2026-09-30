package nz.coreyh.linkr.domain.model

import kotlin.time.Instant

/** A short code mapped to its redirect target, with an optional expiry. */
data class ShortLink(
    val code: ShortCode,
    val target: TargetUrl,
    val createdAt: Instant,
    val expiresAt: Instant? = null,
) {
    fun isExpiredAt(now: Instant): Boolean = expiresAt != null && now >= expiresAt
}
