package nz.coreyh.linkr.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import nz.coreyh.linkr.application.port.`in`.result.DeleteLinkResult
import nz.coreyh.linkr.application.port.out.LinkRepository
import nz.coreyh.linkr.support.fixtures.invalidShortCode
import nz.coreyh.linkr.support.fixtures.validShortCode
import nz.coreyh.linkr.support.kotest.UnitTest

@UnitTest
class DeleteLinkServiceTest :
    FunSpec({
        lateinit var linkRepository: LinkRepository
        lateinit var deleteLinkService: DeleteLinkService

        beforeEach {
            linkRepository = mockk()
            deleteLinkService = DeleteLinkService(linkRepository)
        }

        context("delete") {
            test("returns NotFound when rawCode is not a valid short code") {
                val rawCode = invalidShortCode()

                val result = deleteLinkService.delete(rawCode)

                result shouldBe DeleteLinkResult.NotFound
            }

            test("does not call the repository when rawCode is not a valid short code") {
                val rawCode = invalidShortCode()

                deleteLinkService.delete(rawCode)

                verify { linkRepository wasNot Called }
            }

            test("returns NotFound when the repository reports no such code") {
                val code = validShortCode()
                every { linkRepository.delete(code) } returns false

                val result = deleteLinkService.delete(code.value)

                result shouldBe DeleteLinkResult.NotFound
            }

            test("returns Deleted when the repository deletes the code") {
                val code = validShortCode()
                every { linkRepository.delete(code) } returns true

                val result = deleteLinkService.delete(code.value)

                result shouldBe DeleteLinkResult.Deleted
            }
        }
    })
