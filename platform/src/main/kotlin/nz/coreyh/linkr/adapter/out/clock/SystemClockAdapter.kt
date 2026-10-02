package nz.coreyh.linkr.adapter.out.clock

import nz.coreyh.linkr.application.port.out.ClockPort
import kotlin.time.Clock
import kotlin.time.Instant

class SystemClockAdapter(
    private val clock: Clock = Clock.System,
) : ClockPort {
    override fun now(): Instant = clock.now()
}
