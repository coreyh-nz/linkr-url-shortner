package nz.coreyh.linkr.domain.policy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import nz.coreyh.linkr.support.fixtures.FIXED_NOW
import nz.coreyh.linkr.support.fixtures.validShortLink
import nz.coreyh.linkr.support.kotest.UnitTest
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@UnitTest
class LinkPolicyTest :
    FunSpec({
        context("isTtlAllowed") {
            test("returns true when ttl is null") {
                val result = LinkPolicy.isTtlAllowed(null)

                result shouldBe true
            }

            test("returns false when ttl is zero") {
                val result = LinkPolicy.isTtlAllowed(0.seconds)

                result shouldBe false
            }

            test("returns false when ttl is negative") {
                val result = LinkPolicy.isTtlAllowed((-1).seconds)

                result shouldBe false
            }

            test("returns true when ttl is exactly MAX_TTL") {
                val result = LinkPolicy.isTtlAllowed(LinkPolicy.MAX_TTL)

                result shouldBe true
            }

            test("returns false when ttl exceeds MAX_TTL") {
                val result = LinkPolicy.isTtlAllowed(LinkPolicy.MAX_TTL + 1.seconds)

                result shouldBe false
            }

            test("returns true when ttl is positive and within MAX_TTL") {
                val result = LinkPolicy.isTtlAllowed(7.days)

                result shouldBe true
            }
        }

        context("expiresAtFrom") {
            test("returns null when ttl is null") {
                val result = LinkPolicy.expiresAtFrom(FIXED_NOW, null)

                result.shouldBeNull()
            }

            test("returns now plus ttl when ttl is not null") {
                val ttl = 7.days

                val result = LinkPolicy.expiresAtFrom(FIXED_NOW, ttl)

                result shouldBe FIXED_NOW + ttl
            }
        }

        context("isActive") {
            test("returns true when the link is not expired at now") {
                val link = validShortLink(expiresAt = FIXED_NOW + 1.seconds)

                val result = LinkPolicy.isActive(link, FIXED_NOW)

                result shouldBe true
            }

            test("returns false when the link is expired at now") {
                val link = validShortLink(expiresAt = FIXED_NOW - 1.seconds)

                val result = LinkPolicy.isActive(link, FIXED_NOW)

                result shouldBe false
            }
        }
    })
