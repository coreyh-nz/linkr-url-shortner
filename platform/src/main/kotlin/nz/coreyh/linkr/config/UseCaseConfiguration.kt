package nz.coreyh.linkr.config

import io.github.oshai.kotlinlogging.KotlinLogging
import nz.coreyh.linkr.application.port.`in`.DeleteLinkUseCase
import nz.coreyh.linkr.application.port.`in`.ResolveLinkUseCase
import nz.coreyh.linkr.application.port.`in`.ShortenUrlUseCase
import nz.coreyh.linkr.application.port.out.ClockPort
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.application.port.out.ShortCodeGenerator
import nz.coreyh.linkr.application.service.DeleteLinkService
import nz.coreyh.linkr.application.service.ResolveLinkService
import nz.coreyh.linkr.application.service.ShortenUrlService
import nz.coreyh.linkr.config.properties.LinkrProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class UseCaseConfiguration {
    @Bean
    fun shortenUrlUseCase(
        links: LinkRepository,
        codes: ShortCodeGenerator,
        clock: ClockPort,
        props: LinkrProperties,
    ): ShortenUrlUseCase = ShortenUrlService(links, codes, clock, props.maxCodeAttempts)

    @Bean
    fun resolveLinkUseCase(
        links: LinkRepository,
        clock: ClockPort,
    ): ResolveLinkUseCase = ResolveLinkService(links, clock)

    @Bean
    fun deleteLinkUseCase(links: LinkRepository): DeleteLinkUseCase = DeleteLinkService(links)
}
