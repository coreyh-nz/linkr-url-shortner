package nz.coreyh.linkr.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import nz.coreyh.linkr.application.port.`in`.command.ShortenUrlCommand
import nz.coreyh.linkr.application.port.`in`.result.ShortenUrlResult
import nz.coreyh.linkr.application.port.out.ClockPort
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.application.port.out.ShortCodeGenerator
import nz.coreyh.linkr.application.port.out.result.SaveLinkResult
import nz.coreyh.linkr.domain.policy.LinkPolicy
import nz.coreyh.linkr.support.fixtures.FIXED_NOW
import nz.coreyh.linkr.support.fixtures.invalidRawUrls
import nz.coreyh.linkr.support.fixtures.invalidShortCode
import nz.coreyh.linkr.support.fixtures.validShortCode
import nz.coreyh.linkr.support.fixtures.validTargetUrl
import nz.coreyh.linkr.support.kotest.UnitTest
import nz.coreyh.linkr.support.kotest.withStringTests
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

@UnitTest
class ShortenUrlServiceTest :
    FunSpec({
        lateinit var linkRepository: LinkRepository
        lateinit var shortCodeGenerator: ShortCodeGenerator
        lateinit var clock: ClockPort
        lateinit var shortenUrlService: ShortenUrlService

        beforeEach {
            linkRepository = mockk()
            shortCodeGenerator = mockk()
            clock = mockk()
            shortenUrlService = ShortenUrlService(linkRepository, shortCodeGenerator, clock, maxAttempts = 5)
        }

        context("shorten") {
            context("returns InvalidUrl when rawUrl is not a valid target URL") {
                withStringTests(invalidRawUrls) { rawUrl ->
                    val result = shortenUrlService.shorten(ShortenUrlCommand(rawUrl = rawUrl))

                    result shouldBe ShortenUrlResult.InvalidUrl
                }
            }

            test("does not call the repository when rawUrl is not a valid target URL") {
                val command = ShortenUrlCommand(rawUrl = invalidRawUrls.first())

                shortenUrlService.shorten(command)

                verify { linkRepository wasNot Called }
            }

            test("returns InvalidTtl when ttl is outside the allowed range") {
                val targetUrl = validTargetUrl()
                val ttl = LinkPolicy.MAX_TTL + 1.seconds
                val command = ShortenUrlCommand(rawUrl = targetUrl.value, ttl = ttl)

                val result = shortenUrlService.shorten(command)

                result shouldBe ShortenUrlResult.InvalidTtl
            }

            test("does not call the repository when ttl is outside the allowed range") {
                val targetUrl = validTargetUrl()
                val command = ShortenUrlCommand(rawUrl = targetUrl.value, ttl = LinkPolicy.MAX_TTL + 1.seconds)

                shortenUrlService.shorten(command)

                verify { linkRepository wasNot Called }
            }

            test("sets createdAt from clock.now()") {
                val targetUrl = validTargetUrl()
                val command = ShortenUrlCommand(rawUrl = targetUrl.value)
                val shortCode = validShortCode()
                every { shortCodeGenerator.generate(targetUrl, 0) } returns shortCode
                every { clock.now() } returns FIXED_NOW
                every { linkRepository.save(any()) } returns SaveLinkResult.Saved

                val result = shortenUrlService.shorten(command)

                result.shouldBeTypeOf<ShortenUrlResult.Created> {
                    it.link.createdAt shouldBe FIXED_NOW
                }
            }

            test("sets expiresAt from LinkPolicy.expiresAtFrom(now, ttl)") {
                val targetUrl = validTargetUrl()
                val ttl = 7.days
                val command = ShortenUrlCommand(rawUrl = targetUrl.value, ttl = ttl)
                val shortCode = validShortCode()
                val expiresAt = LinkPolicy.expiresAtFrom(FIXED_NOW, ttl)
                every { shortCodeGenerator.generate(targetUrl, 0) } returns shortCode
                every { clock.now() } returns FIXED_NOW
                every { linkRepository.save(any()) } returns SaveLinkResult.Saved

                val result = shortenUrlService.shorten(command)

                result.shouldBeTypeOf<ShortenUrlResult.Created> {
                    it.link.expiresAt shouldBe expiresAt
                }
            }

            context("with a custom code") {
                test("returns InvalidCode when the custom code is not a valid short code") {
                    val targetUrl = validTargetUrl()
                    val rawCode = invalidShortCode()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value, customCode = rawCode)
                    every { clock.now() } returns FIXED_NOW

                    val result = shortenUrlService.shorten(command)

                    result shouldBe ShortenUrlResult.InvalidCode
                }

                test("does not call the repository when the custom code is not a valid short code") {
                    val targetUrl = validTargetUrl()
                    val rawCode = invalidShortCode()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value, customCode = rawCode)
                    every { clock.now() } returns FIXED_NOW

                    shortenUrlService.shorten(command)

                    verify { linkRepository wasNot Called }
                }

                test("returns Created with the custom code when it is valid and unused") {
                    val targetUrl = validTargetUrl()
                    val shortCode = validShortCode()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value, customCode = shortCode.value)
                    every { clock.now() } returns FIXED_NOW
                    every { linkRepository.save(any()) } returns SaveLinkResult.Saved

                    val result = shortenUrlService.shorten(command)

                    result.shouldBeTypeOf<ShortenUrlResult.Created>()
                }

                test("returns CodeTaken when the custom code is already in use, without retrying") {
                    val targetUrl = validTargetUrl()
                    val shortCode = validShortCode()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value, customCode = shortCode.value)
                    every { clock.now() } returns FIXED_NOW
                    every { linkRepository.save(any()) } returns SaveLinkResult.CodeTaken

                    val result = shortenUrlService.shorten(command)

                    result shouldBe ShortenUrlResult.CodeTaken
                }
            }

            context("without a custom code") {
                test("generates a code via ShortCodeGenerator and returns Created when it saves successfully") {
                    val targetUrl = validTargetUrl()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value)
                    val shortCode = validShortCode()
                    every { clock.now() } returns FIXED_NOW
                    every { shortCodeGenerator.generate(targetUrl, 0) } returns shortCode
                    every { linkRepository.save(any()) } returns SaveLinkResult.Saved

                    val result = shortenUrlService.shorten(command)

                    result.shouldBeTypeOf<ShortenUrlResult.Created> {
                        it.link.code shouldBe shortCode
                    }
                }

                test("retries with an incrementing attempt number when a generated code collides") {
                    val targetUrl = validTargetUrl()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value)
                    val collidingCode = validShortCode("abc123")
                    val freeCode = validShortCode("def456")
                    every { clock.now() } returns FIXED_NOW
                    every { shortCodeGenerator.generate(targetUrl, 0) } returns collidingCode
                    every { shortCodeGenerator.generate(targetUrl, 1) } returns freeCode
                    every { linkRepository.save(match { it.code == collidingCode }) } returns SaveLinkResult.CodeTaken
                    every { linkRepository.save(match { it.code == freeCode }) } returns SaveLinkResult.Saved

                    val result = shortenUrlService.shorten(command)

                    result.shouldBeTypeOf<ShortenUrlResult.Created> {
                        it.link.code shouldBe freeCode
                    }
                    verifyOrder {
                        shortCodeGenerator.generate(targetUrl, 0)
                        shortCodeGenerator.generate(targetUrl, 1)
                    }
                }

                test("returns Created as soon as a generated code saves successfully, without exhausting retries") {
                    val targetUrl = validTargetUrl()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value)
                    val shortCode = validShortCode()
                    every { clock.now() } returns FIXED_NOW
                    every { shortCodeGenerator.generate(targetUrl, 0) } returns shortCode
                    every { linkRepository.save(any()) } returns SaveLinkResult.Saved

                    shortenUrlService.shorten(command)

                    verify(exactly = 1) { shortCodeGenerator.generate(any(), any()) }
                }

                test("returns CouldNotGenerateCode after maxAttempts consecutive collisions") {
                    val targetUrl = validTargetUrl()
                    val command = ShortenUrlCommand(rawUrl = targetUrl.value)
                    val maxAttempts = 3
                    val serviceWithMaxAttempts =
                        ShortenUrlService(linkRepository, shortCodeGenerator, clock, maxAttempts = maxAttempts)
                    every { clock.now() } returns FIXED_NOW
                    every { shortCodeGenerator.generate(targetUrl, any()) } returns validShortCode()
                    every { linkRepository.save(any()) } returns SaveLinkResult.CodeTaken

                    val result = serviceWithMaxAttempts.shorten(command)

                    result shouldBe ShortenUrlResult.CouldNotGenerateCode
                    verifySequence {
                        repeat(maxAttempts) { attempt ->
                            shortCodeGenerator.generate(targetUrl, attempt)
                        }
                    }
                }
            }
        }
    })
