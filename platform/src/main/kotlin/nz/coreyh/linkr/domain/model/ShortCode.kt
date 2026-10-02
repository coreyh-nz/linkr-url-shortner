package nz.coreyh.linkr.domain.model

/**
 * A validated short-link code: 4-16 chars, `[A-Za-z0-9_-]` only. Construct
 * via [parse]; use [fromTrusted] only for values already known to be valid
 * (e.g. read from storage).
 */
@JvmInline
value class ShortCode private constructor(
    val value: String,
) {
    companion object {
        private val ALLOWED = Regex("[A-Za-z0-9_-]+")
        const val MIN_LENGTH = 4
        const val MAX_LENGTH = 16

        fun parse(raw: String): ShortCode? {
            val trimmed = raw.trim()
            if (trimmed.length !in MIN_LENGTH..MAX_LENGTH) return null
            if (!ALLOWED.matches(trimmed)) return null
            return ShortCode(trimmed)
        }

        fun fromTrusted(raw: String): ShortCode = ShortCode(raw)
    }

    override fun toString(): String = value
}
