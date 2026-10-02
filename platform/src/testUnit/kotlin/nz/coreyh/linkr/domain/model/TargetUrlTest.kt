package nz.coreyh.linkr.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import nz.coreyh.linkr.support.fixtures.invalidTargetUrl
import nz.coreyh.linkr.support.fixtures.validRawUrls
import nz.coreyh.linkr.support.kotest.UnitTest
import nz.coreyh.linkr.support.kotest.withStringTests

@UnitTest
class TargetUrlTest :
    FunSpec({
        context("parse") {
            test("returns null when raw is blank") {
                TargetUrl.parse("   ").shouldBeNull()
            }

            test("returns null when raw exceeds MAX_LENGTH") {
                val raw = "https://" + "a".repeat(2048)

                TargetUrl.parse(raw).shouldBeNull()
            }

            test("returns null when raw has no scheme") {
                TargetUrl.parse(invalidTargetUrl()).shouldBeNull()
            }

            test("returns null when the scheme is not http or https") {
                TargetUrl.parse("ftp://example.com").shouldBeNull()
            }

            test("returns null when the host is blank") {
                TargetUrl.parse("https://").shouldBeNull()
            }

            test("trims surrounding whitespace before validating") {
                TargetUrl.parse("  https://example.com  ").shouldNotBeNull {
                    value shouldBe "https://example.com"
                }
            }

            context("returns a TargetUrl for a valid http or https URL") {
                withStringTests(validRawUrls) { raw ->
                    TargetUrl.parse(raw).shouldNotBeNull {
                        value shouldBe raw
                    }
                }
            }
        }

        context("toString") {
            test("returns the underlying value") {
                val raw = "https://example.com"
                val targetUrl = TargetUrl.fromTrusted(raw)

                targetUrl.toString() shouldBe raw
            }
        }
    })
