package nz.coreyh.linkr.application.port.out

import kotlin.time.Instant

/**
 * Outbound port: current time, kept behind an interface so it can be
 * controlled in tests.
 */
interface ClockPort {
    fun now(): Instant
}
