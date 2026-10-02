package nz.coreyh.linkr.support.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.test.AssertionMode
import io.kotest.extensions.spring.SpringExtension

class KotestConfiguration : AbstractProjectConfig() {
    override val assertionMode = AssertionMode.Error
    override val extensions = listOf(SpringExtension())
}
