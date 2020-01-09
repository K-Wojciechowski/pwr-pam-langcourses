package pl.krzysztofwojciechowski.langcourses.db

import android.content.Context
import org.threeten.bp.LocalDateTime
import pl.krzysztofwojciechowski.langcourses.Course
import pl.krzysztofwojciechowski.langcourses.TEXT_DATE_TIME_FORMAT
import pl.krzysztofwojciechowski.langcourses.quizPassed
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager

enum class ChapterProgress {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETE
}

suspend fun getNextChapterId(courseID: Int, context: Context): Int? {
    val db = MLCDatabase.getDatabase(context)
    val progress = db.courseProgressDao().getProgressForCourseDirect(courseID)
    val avCourse = db.availableCourseDao().getCourse(courseID)
    val course = getResourceManager(context).getCourseData(courseID, avCourse.path)
    return getNextChapterId(course, progress, context)
}

fun getNextChapterId(course: Course, progress: List<CourseProgress>, context: Context): Int? {
    val startedIncomplete = progress.firstOrNull { it.started && !it.completed }
    if (startedIncomplete != null) return startedIncomplete.chapterID
    val lastComplete = progress.lastOrNull { it.completed }
    return if (lastComplete == null) {
        course.chapters.first().chapterID
    } else {
        val lastCompleteIndex =
            course.chapters.indexOfFirst { it.chapterID == lastComplete.chapterID }
        course.chapters.getOrNull(lastCompleteIndex + 1)?.chapterID
    }
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