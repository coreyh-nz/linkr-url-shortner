package nz.coreyh.linkr.support.fixtures

import nz.coreyh.linkr.domain.model.ShortCode

fun validShortCode(value: String = "abc123"): ShortCode = ShortCode.fromTrusted(value)

fun invalidShortCode(): String = ""
