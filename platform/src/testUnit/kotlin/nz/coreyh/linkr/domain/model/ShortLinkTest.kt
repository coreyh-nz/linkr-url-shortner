package nz.coreyh.linkr.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import nz.coreyh.linkr.support.fixtures.FIXED_NOW
import nz.coreyh.linkr.support.fixtures.validShortLink
import nz.coreyh.linkr.support.kotest.UnitTest
import kotlin.time.Duration.Companion.seconds

@UnitTest
class ShortLinkTest :
    FunSpec({
        context("isExpiredAt") {
            test("returns false when expiresAt is null") {
                val shortLink = validShortLink(expiresAt = null)

                val result = shortLink.isExpiredAt(FIXED_NOW)

                result shouldBe false
            }

            test("returns false when now is before expiresAt") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)

                val result = shortLink.isExpiredAt(expiresAt - 1.seconds)

                result shouldBe false
            }

            test("returns true when now equals expiresAt") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)

                val result = shortLink.isExpiredAt(expiresAt)

                result shouldBe true
            }

            test("returns true when now is after expiresAt") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)

                val result = shortLink.isExpiredAt(expiresAt + 1.seconds)

                result shouldBe true
            }
        }
    })
