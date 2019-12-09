package pl.krzysztofwojciechowski.langcourses

enum class ChapterProgress {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETE
}

fun getCoursePath(courseID: Int): String {
    // TODO DB
    if (courseID == 1) return "angielski-podstawowy-1-1"
    else throw Exception("Unknown course (PLACEHOLDER)")
}

fun getNextChapterId(courseID: Int): Int? {
    return 0
}
