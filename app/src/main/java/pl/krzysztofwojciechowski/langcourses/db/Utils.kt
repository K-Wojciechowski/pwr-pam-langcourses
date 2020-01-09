package pl.krzysztofwojciechowski.langcourses.db

import android.content.Context

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
            it.chapterId,
            when {
                it.completed -> ChapterProgress.COMPLETE
                it.started -> ChapterProgress.IN_PROGRESS
                else -> ChapterProgress.NOT_STARTED
            }
        )
    }
}

fun getCoursePath(courseID: Int, context: Context): String {
    val db = MLCDatabase.getDatabase(context)
    return db.availableCourseDao().getCourse(courseID).path
}

fun getNextChapterId(courseID: Int, context: Context): Int? {
    val db = MLCDatabase.getDatabase(context)
    val course = db.availableCourseDao().getCourse(courseID)
    val progress = db.courseProgressDao().getProgressForCourse(courseID)
    val startedIncomplete = progress.firstOrNull { it.started && !it.completed }
    if (startedIncomplete != null) return startedIncomplete.chapterId
    val lastComplete = progress.lastOrNull { it.completed } ?: return 1 // first chapter
    val next = lastComplete.chapterId + 1
    return if (next <= course.chapterCount) next else null
}
