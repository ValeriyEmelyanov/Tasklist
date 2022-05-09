package tasklist.model

enum class ColorMark(val mark: String) {
    RED("\u001B[101m \u001B[0m"),
    YELLOW("\u001B[103m \u001B[0m"),
    GREEN("\u001B[102m \u001B[0m"),
    BLUE("\u001B[104m \u001B[0m"),
}