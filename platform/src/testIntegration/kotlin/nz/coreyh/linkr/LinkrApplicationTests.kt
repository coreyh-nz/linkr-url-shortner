package nz.coreyh.linkr

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class LinkrApplicationTests :
    FunSpec({
        test("context loads")
    })
