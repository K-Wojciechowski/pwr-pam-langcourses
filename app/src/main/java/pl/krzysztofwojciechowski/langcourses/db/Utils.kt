package pl.krzysztofwojciechowski.langcourses.db

import android.content.Context
import org.threeten.bp.LocalDateTime
import pl.krzysztofwojciechowski.langcourses.TEXT_DATE_TIME_FORMAT
import pl.krzysztofwojciechowski.langcourses.quizPassed

enum class ChapterProgress {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETE
}

fun getNextChapterId(courseID: Int, context: Context): Int? {
    val db = MLCDatabase.getDatabase(context)
    val course = db.availableCourseDao().getCourse(courseID)
    val progressLD = db.courseProgressDao().getProgressForCourse(courseID)
    while (progressLD.value == null) { android.util.Log.e("WAIT", "TODO") }
    val progress = progressLD.value!!
    val startedIncomplete = progress.firstOrNull { it.started && !it.completed }
    if (startedIncomplete != null) return startedIncomplete.chapterID
    val lastComplete = progress.lastOrNull { it.completed } ?: return 1 // first chapter
    val next = lastComplete.chapterID + 1
    return if (next <= course.chapterCount) next else null
}

suspend fun saveInteractionWith(courseID: Int, chapterID: Int, context: Context) {
    val db = MLCDatabase.getDatabase(context)
    val courseProgressDao = db.courseProgressDao()
    val progress = courseProgressDao.getProgressForChapter(courseID, chapterID)
    if (progress == null) {
        courseProgressDao.insert(
            CourseProgress(courseID, chapterID,
                started = true,
                completed = false
            )
        )
    }
}

suspend fun saveQuizAttempt(
    courseID: Int,
    chapterID: Int,
    correct: Int,
    total: Int,
    context: Context
) {
    val db = MLCDatabase.getDatabase(context)
    val quizAttemptDao = db.quizAttemptDao()
    val courseProgressDao = db.courseProgressDao()
    val attemptDate = LocalDateTime.now()
    val attemptDateString = attemptDate.format(TEXT_DATE_TIME_FORMAT)
    quizAttemptDao.insert(QuizAttempt(
        courseID, chapterID, attemptDateString, correct, total
    ))
    if (quizPassed(correct, total)) {
        courseProgressDao.insert(CourseProgress(
            courseID, chapterID,
            started = true,
            completed = true
        ))
    }
}