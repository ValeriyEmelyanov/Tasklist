package tasklist.datetime

import kotlinx.datetime.LocalDate

interface DateTimeService {
    fun getCurrentDate(): LocalDate
}