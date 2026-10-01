package nz.coreyh.linkr.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("linkr")
data class LinkrProperties(
    val maxCodeAttempts: Int,
    val codeLength: Int,
)
