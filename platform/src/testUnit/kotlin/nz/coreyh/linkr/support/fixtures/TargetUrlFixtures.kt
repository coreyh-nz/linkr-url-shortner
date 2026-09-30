package nz.coreyh.linkr.support.fixtures

import nz.coreyh.linkr.domain.model.TargetUrl

fun validTargetUrl(value: String = validRawUrls.first()): TargetUrl = TargetUrl.fromTrusted(value)

/** Missing a scheme, so it fails [TargetUrl.parse]. */
fun invalidTargetUrl(): String = "example.com"

val validRawUrls =
    listOf(
        "https://example.com",
        "http://example.com",
        "https://example.com/path?query=1",
        "https://sub.example.com:8080",
    )

val invalidRawUrls =
    listOf(
        "",
        "not a url",
        "example.com",
        "ftp://example.com",
        "https://",
        "https://" + "a".repeat(2048),
    )
