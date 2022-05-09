package tasklist.model

enum class Priority(val mark: String) {
    C(ColorMark.RED.mark),
    H(ColorMark.YELLOW.mark),
    N(ColorMark.GREEN.mark),
    L(ColorMark.BLUE.mark),
}
