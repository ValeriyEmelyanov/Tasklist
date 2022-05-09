package tasklist.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class Task(var task: String, var priority: Priority, var dataTime: String) {

    constructor(task: String, priority: Priority, dateTime: LocalDateTime) : this(task, priority, dateTime.toString())

    fun setDateTime(dateTime: LocalDateTime) {
        dataTime = dateTime.toString()
    }

    fun getDateTime(): LocalDateTime = LocalDateTime.parse(dataTime)

    fun getDate(): LocalDate = LocalDateTime.parse(dataTime).date
}