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

fun getChapterProgress(courseID: Int, context: Context): Map<Int, ChapterProgress> {
    val db = MLCDatabase.getDatabase(context)
    val progress = db.courseProgressDao().getProgressForCourse(courseID)
    return progress.associate {
        Pair(
            it.chapterID,
            when {
                it.completed -> ChapterProgress.COMPLETE
                it.started -> ChapterProgress.IN_PROGRESS
                else -> ChapterProgress.NOT_STARTED
            }
        )
    }
}

fun getNextChapterId(courseID: Int, context: Context): Int? {
    val db = MLCDatabase.getDatabase(context)
    val course = db.availableCourseDao().getCourse(courseID)
    val progress = db.courseProgressDao().getProgressForCourse(courseID)
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