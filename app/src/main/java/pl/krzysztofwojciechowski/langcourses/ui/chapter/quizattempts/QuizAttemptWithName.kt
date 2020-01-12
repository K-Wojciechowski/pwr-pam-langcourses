package pl.krzysztofwojciechowski.langcourses.ui.chapter.quizattempts

import pl.krzysztofwojciechowski.langcourses.db.QuizAttempt

data class QuizAttemptWithName(
    var courseID: Int,
    var chapterID: Int,
    var attemptDate: String, // timestamp
    var correct: Int,
    var total: Int,
    var chapterName: String
) {
    val percCorrect: Double
        get() = correct * 100 / total.toDouble()

    constructor(qa: QuizAttempt, name: String) : this(
        qa.courseID,
        qa.chapterID,
        qa.attemptDate,
        qa.correct,
        qa.total,
        name
    )
}
