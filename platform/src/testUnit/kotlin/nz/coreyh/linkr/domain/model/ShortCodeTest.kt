package nz.coreyh.linkr.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import nz.coreyh.linkr.support.kotest.UnitTest
import nz.coreyh.linkr.support.kotest.withStringTests

@UnitTest
class ShortCodeTest :
    FunSpec({
        context("parse") {
            test("returns null when raw is shorter than MIN_LENGTH") {
                val raw = "a".repeat(ShortCode.MIN_LENGTH - 1)

                ShortCode.parse(raw).shouldBeNull()
            }

            test("returns null when raw is longer than MAX_LENGTH") {
                val raw = "a".repeat(ShortCode.MAX_LENGTH + 1)

                ShortCode.parse(raw).shouldBeNull()
            }

            context("returns null when raw contains a character outside [A-Za-z0-9_-]") {
                withStringTests(listOf("abc 123", "abc.123", "abc/123", "abc@123", "abcé123")) {
                    ShortCode.parse(it).shouldBeNull()
                }
            }

            test("trims surrounding whitespace before validating") {
                ShortCode.parse("  abc123  ").shouldNotBeNull {
                    value shouldBe "abc123"
                }
            }

            test("returns a ShortCode when raw is exactly MIN_LENGTH") {
                val raw = "a".repeat(ShortCode.MIN_LENGTH)

                ShortCode.parse(raw).shouldNotBeNull {
                    value shouldBe raw
                }
            }

            test("returns a ShortCode when raw is exactly MAX_LENGTH") {
                val raw = "a".repeat(ShortCode.MAX_LENGTH)

                ShortCode.parse(raw).shouldNotBeNull {
                    value shouldBe raw
                }
            }

            test("returns a ShortCode when raw contains letters, digits, underscores, and hyphens") {
                val raw = "Aa0_-9"

                ShortCode.parse(raw).shouldNotBeNull {
                    value shouldBe raw
                }
            }
        }

        context("toString") {
            test("returns the underlying value") {
                val raw = "abc123"
                val shortCode = ShortCode.fromTrusted(raw)

                shortCode.toString() shouldBe raw
            }
        }
    })
