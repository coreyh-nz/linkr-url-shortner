package nz.coreyh.linkr.domain.model

import java.net.URI

/**
 * A validated redirect target: http/https only, non-blank host, max 2048
 * chars. Construct via [parse]; use [fromTrusted] only for values already
 * known to be valid (e.g. read from storage).
 */
@JvmInline
value class TargetUrl private constructor(
    val value: String,
) {
    companion object {
        private const val MAX_LENGTH = 2048
        private val ALLOWED_SCHEMES = setOf("http", "https")

        fun parse(raw: String): TargetUrl? {
            val trimmed = raw.trim()
            if (trimmed.isEmpty() || trimmed.length > MAX_LENGTH) return null

            val uri = runCatching { URI(trimmed) }.getOrNull() ?: return null
            val scheme = uri.scheme?.lowercase() ?: return null
            if (scheme !in ALLOWED_SCHEMES) return null
            if (uri.host.isNullOrBlank()) return null

            return TargetUrl(trimmed)
        }

        fun fromTrusted(raw: String): TargetUrl = TargetUrl(raw)
    }

    override fun toString(): String = value
}
