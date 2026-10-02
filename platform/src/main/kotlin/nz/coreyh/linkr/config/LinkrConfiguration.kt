package nz.coreyh.linkr.config

import nz.coreyh.linkr.config.properties.LinkrProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LinkrProperties::class)
class LinkrConfiguration(
    props: LinkrProperties,
)
