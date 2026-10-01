package nz.coreyh.linkr.adapter.out.codegen

import io.github.oshai.kotlinlogging.KotlinLogging
import java.security.SecureRandom
import nz.coreyh.linkr.application.port.out.ShortCodeGenerator
import nz.coreyh.linkr.domain.model.ShortCode
import nz.coreyh.linkr.domain.model.TargetUrl

private val logger = KotlinLogging.logger {}

class RandomBase62CodeGeneratorAdapter(
    private val length: Int,
    private val random: SecureRandom = SecureRandom(),
) : ShortCodeGenerator {
    init {
        require(length in ShortCode.MIN_LENGTH..ShortCode.MAX_LENGTH) {
            "length must be in ${ShortCode.MIN_LENGTH}..${ShortCode.MAX_LENGTH}, was $length"
        }
    }

    override fun generate(
        target: TargetUrl,
        attempt: Int,
    ): ShortCode {
        val chars = CharArray(length) { ALPHABET[random.nextInt(ALPHABET.length)] }
        return ShortCode
            .fromTrusted(String(chars))
            .also { logger.trace { "Generated code=$it (attempt=$attempt)" } }
    }

    private companion object {
        const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    }
}
