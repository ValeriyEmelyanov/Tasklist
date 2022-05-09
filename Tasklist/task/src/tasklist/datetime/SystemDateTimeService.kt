package tasklist.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SystemDateTimeService : DateTimeService {
    override fun getCurrentDate() = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
}