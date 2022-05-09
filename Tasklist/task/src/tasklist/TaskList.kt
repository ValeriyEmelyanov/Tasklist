package tasklist

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime
import kotlinx.datetime.daysUntil
import tasklist.datetime.DateTimeService
import tasklist.model.ColorMark
import tasklist.model.Priority
import tasklist.model.Task
import java.io.File

class TaskList(private val dateTimeService: DateTimeService) {
    private val tasks = mutableListOf<Task>()

    fun restoreTasks(fileName: String) {
        val file = File(fileName)
        if (!file.exists()) return

        val adapter = getJsonAdapter()

        val jsonText = file.readText()
        val tempList = adapter.fromJson(jsonText)!!

        for (task in tempList) {
            if (task == null) continue
            tasks.add(task)
        }

    }

    fun saveTasks(fileName: String) {
        val adapter = getJsonAdapter()

        val jsonText = adapter.toJson(tasks)
        val file = File(fileName)
        file.writeText(jsonText)
    }

    private fun getJsonAdapter(): JsonAdapter<List<Task?>> {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(
            List::class.java,
            Task::class.java,
            Priority::class.java
        )
        return moshi.adapter(type)
    }

    fun addTask() {
        val priority = inputPriority()
        val date = inputDate()
        val time = inputTime()
        val lines = inputDescription()

        if (lines.isEmpty()) {
            println("The task is blank")
            return
        }

        tasks.add(Task(lines.joinToString("\n"), priority, date.atTime(time.first, time.second)))
    }

    private fun inputPriority(): Priority {
        while (true) {
            println("Input the task priority (C, H, N, L):")
            val priorityName = readLine()!!
            try {
                return Priority.valueOf(priorityName.uppercase())
            } catch (ignored: Exception) {
            }
        }
    }

    private fun inputDate(): LocalDate {
        while (true) {
            println("Input the date (yyyy-mm-dd):")
            try {
                val (year, month, day) = readLine()!!.split("-").map { it.toInt() }
                return LocalDate(year, month, day)
            } catch (e: Exception) {
                println("The input date is invalid")
            }
        }
    }

    private fun inputTime(): Pair<Int, Int> {
        val regex = "(([0-1]?[0-9])|(2[0-3])):[0-5]?[0-9]".toRegex()
        while (true) {
            println("Input the time (hh:mm):")
            val input = readLine()!!
            if (input.matches(regex)) {
                val (minutes, seconds) = input.split(":")
                return Pair(minutes.toInt(), seconds.toInt())
            }
            println("The input time is invalid")
        }
    }

    private fun inputDescription(): MutableList<String> {
        println("Input a new task (enter a blank line to end):")

        val lines = mutableListOf<String>()
        while (true) {
            val input = readLine()!!.trim()
            if (input.isBlank()) return lines
            lines.add(input)
        }
    }

    fun editTask() {
        if (isEmpty()) return

        printTasks()
        editTask(getTaskIndex())
        println("The task is changed")
    }

    private fun getTaskIndex(): Int {
        while (true) {
            println("Input the task number (1-${tasks.size}):")
            val input = readLine()!!

            val num: Int
            try {
                num = input.toInt()
            } catch (ignored: Exception) {
                println("Invalid task number")
                continue
            }

            if (num < 1 || num > tasks.size) {
                println("Invalid task number")
                continue
            }

            return num - 1
        }
    }

    private fun editTask(index: Int) {
        while (true) {
            val task = tasks[index]

            println("Input a field to edit (priority, date, time, task):")

            when (readLine()!!) {
                "priority" -> task.priority = inputPriority()
                "date" -> task.setDateTime(inputDate().atTime(task.getDateTime().hour, task.getDateTime().minute))
                "time" -> {
                    val time = inputTime()
                    task.setDateTime(task.getDate().atTime(time.first, time.second))
                }
                "task" -> task.task = inputDescription().joinToString("\n")
                else -> {
                    println("Invalid field")
                    continue
                }
            }

            break
        }
    }

    fun deleteTask() {
        if (isEmpty()) return

        printTasks()
        tasks.removeAt(getTaskIndex())
        println("The task is deleted")
    }

    fun printTasks() {
        if (isEmpty()) return

        println(HORIZ)
        println(HEADS)

        for ((i, task) in tasks.withIndex()) {
            println(HORIZ)

            val dateTime = task.dataTime.split("T")

            val n = (i + 1).toString().padEnd(PREFIX_LEN, ' ')
            val date = dateTime[0]
            val time = dateTime[1]
            val p = task.priority.mark
            val d = toDueColor(task.getDate())

            val lines = task.task.split("\n")
            for ((j, s) in lines.withIndex()) {
                var shift = 0
                while (shift < s.length) {
                    val sub = s.substring(shift, minOf(shift + DESC_LEN, s.length))
                    if (j == 0 && shift == 0) {
                        println(String.format(LINE1, n, date, time, p, d, sub.padEnd(DESC_LEN)))
                    } else {
                        println(String.format(LINE2, sub.padEnd(DESC_LEN)))
                    }
                    shift += DESC_LEN
                }
            }
        }

        println(HORIZ)
    }

    private fun toDueColor(date: LocalDate): String {
        return when (getDue(date)) {
            "I" -> ColorMark.GREEN.mark
            "T" -> ColorMark.YELLOW.mark
            "O" -> ColorMark.RED.mark
            else -> " "
        }
    }

    private fun getDue(date: LocalDate): String {
        val currentDate = dateTimeService.getCurrentDate()
        val numOfDays = currentDate.daysUntil(date)
        return when {
            numOfDays == 0 -> "T"
            numOfDays > 0 -> "I"
            else -> "O"
        }
    }

    private fun isEmpty(): Boolean {
        if (tasks.isEmpty()) {
            println("No tasks have been input")
            return true
        }
        return false
    }

    private companion object {
        const val PREFIX_LEN = 2
        const val DESC_LEN = 44
        const val DESC2_LEN = 23
        val HORIZ = "+----+------------+-------+---+---+" + "-".repeat(DESC_LEN) + "+"
        val HEADS = "| N  |    Date    | Time  | P | D |" + "Task".padStart(DESC2_LEN).padEnd(DESC_LEN) + "|"
        val LINE1 = "| %s | %s | %s | %s | %s |%s|"
        val LINE2 = "|    |            |       |   |   |%s|"
    }
}
