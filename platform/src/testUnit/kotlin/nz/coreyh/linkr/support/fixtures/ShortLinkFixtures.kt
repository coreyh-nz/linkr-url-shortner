package nz.coreyh.linkr.support.fixtures

import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.ShortLink
import nz.coreyh.linkr.domain.model.TargetUrl
import kotlin.time.Instant

fun validShortLink(
    code: ShortCode = validShortCode(),
    target: TargetUrl = validTargetUrl(),
    createdAt: Instant = FIXED_NOW,
    expiresAt: Instant? = null,
): ShortLink =
    ShortLink(
        code = code,
        target = target,
        createdAt = createdAt,
        expiresAt = expiresAt,
    )
