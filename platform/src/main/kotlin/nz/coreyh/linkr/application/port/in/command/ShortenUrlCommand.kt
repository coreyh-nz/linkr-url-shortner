package nz.coreyh.linkr.application.port.`in`.command

import kotlin.time.Duration

data class ShortenUrlCommand(
    val rawUrl: String,
    val customCode: String? = null,
    val ttl: Duration? = null,
)
