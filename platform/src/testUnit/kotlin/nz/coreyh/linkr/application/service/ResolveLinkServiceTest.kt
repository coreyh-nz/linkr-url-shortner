package nz.coreyh.linkr.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nz.coreyh.linkr.application.port.`in`.result.ResolveLinkResult
import nz.coreyh.linkr.application.port.out.ClockPort
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.support.fixtures.FIXED_NOW
import nz.coreyh.linkr.support.fixtures.invalidShortCode
import nz.coreyh.linkr.support.fixtures.validShortCode
import nz.coreyh.linkr.support.fixtures.validShortLink
import nz.coreyh.linkr.support.kotest.UnitTest
import kotlin.time.Duration.Companion.seconds

@UnitTest
class ResolveLinkServiceTest :
    FunSpec({
        lateinit var linkRepository: LinkRepository
        lateinit var clock: ClockPort
        lateinit var resolveLinkService: ResolveLinkService

        beforeEach {
            linkRepository = mockk()
            clock = mockk()
            resolveLinkService = ResolveLinkService(linkRepository, clock)
        }

        context("resolve") {
            test("returns NotFound when rawCode is not a valid short code") {
                val rawCode = invalidShortCode()

                val result = resolveLinkService.resolve(rawCode)

                result shouldBe ResolveLinkResult.NotFound
            }

            test("does not call the repository when rawCode is not a valid short code") {
                val rawCode = invalidShortCode()

                resolveLinkService.resolve(rawCode)

                verify { linkRepository wasNot Called }
            }

            test("returns NotFound when no link exists for the code") {
                val code = validShortCode()
                every { linkRepository.findByCode(code) } returns null

                val result = resolveLinkService.resolve(code.value)

                result shouldBe ResolveLinkResult.NotFound
            }

            test("returns Expired when now is at the link's expiresAt") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)
                every { linkRepository.findByCode(shortLink.code) } returns shortLink
                every { clock.now() } returns expiresAt

                val result = resolveLinkService.resolve(shortLink.code.value)

                result shouldBe ResolveLinkResult.Expired
            }

            test("returns Expired when now is after the link's expiresAt") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)
                every { linkRepository.findByCode(shortLink.code) } returns shortLink
                every { clock.now() } returns expiresAt + 1.seconds

                val result = resolveLinkService.resolve(shortLink.code.value)

                result shouldBe ResolveLinkResult.Expired
            }

            test("returns Found with the link's target when the link has not yet expired") {
                val expiresAt = FIXED_NOW
                val shortLink = validShortLink(expiresAt = expiresAt)
                every { linkRepository.findByCode(shortLink.code) } returns shortLink
                every { clock.now() } returns expiresAt - 1.seconds

                val result = resolveLinkService.resolve(shortLink.code.value)

                result shouldBe ResolveLinkResult.Found(shortLink.target)
            }

            test("returns Found with the link's target when the link has no expiry") {
                val shortLink = validShortLink()
                every { linkRepository.findByCode(shortLink.code) } returns shortLink
                every { clock.now() } returns FIXED_NOW

                val result = resolveLinkService.resolve(shortLink.code.value)

                result shouldBe ResolveLinkResult.Found(shortLink.target)
            }
        }
    })
