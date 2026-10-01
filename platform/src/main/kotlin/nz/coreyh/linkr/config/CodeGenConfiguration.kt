package nz.coreyh.linkr.config

import nz.coreyh.linkr.adapter.out.codegen.RandomBase62CodeGeneratorAdapter
import nz.coreyh.linkr.application.port.out.ShortCodeGenerator
import nz.coreyh.linkr.config.properties.LinkrProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CodeGenConfiguration {
    @Bean
    fun randomGenerator(props: LinkrProperties): ShortCodeGenerator = RandomBase62CodeGeneratorAdapter(length = props.codeLength)
}
