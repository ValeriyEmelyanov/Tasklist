package tasklist

import tasklist.datetime.SystemDateTimeService

fun main() {
    val taskList = TaskList(SystemDateTimeService())

    val fileName = "tasklist.json"
    taskList.restoreTasks(fileName)

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        val action = readLine()!!
        when (action) {
            "add" -> taskList.addTask()
            "edit" -> taskList.editTask()
            "delete" -> taskList.deleteTask()
            "print" -> taskList.printTasks()
            "end" -> break
            else -> println("The input action is invalid")
        }
    }

    taskList.saveTasks(fileName)
    println("Tasklist exiting!")
}
