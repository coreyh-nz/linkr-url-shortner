package nz.coreyh.linkr.config

import nz.coreyh.linkr.adapter.out.clock.SystemClockAdapter
import nz.coreyh.linkr.application.port.out.ClockPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClockConfiguration {
    @Bean
    fun clockPort(): ClockPort = SystemClockAdapter()
}
