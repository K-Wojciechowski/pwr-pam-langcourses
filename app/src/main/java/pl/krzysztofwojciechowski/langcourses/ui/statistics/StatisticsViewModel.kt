package pl.krzysztofwojciechowski.langcourses.ui.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.krzysztofwojciechowski.langcourses.db.*

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val availableCourseDao: AvailableCourseDao
    private val courseProgressDao: CourseProgressDao
    private val quizAttemptDao: QuizAttemptDao
    private val availableCourses: LiveData<List<AvailableCourse>>
    private val courseProgress: LiveData<List<CourseProgress>>
    private val quizAttempts: LiveData<List<QuizAttempt>>
    val completion = MutableLiveData(0)
    val quizAverage = MutableLiveData(0)
    val statistics = MutableLiveData(listOf<StatisticsEntry>())

    init {
        val database = MLCDatabase.getDatabase(application)
        availableCourseDao = database.availableCourseDao()
        courseProgressDao = database.courseProgressDao()
        quizAttemptDao = database.quizAttemptDao()
        availableCourses = availableCourseDao.getAvailableCourses()
        courseProgress = courseProgressDao.getProgress()
        quizAttempts = quizAttemptDao.getAllAttempts()
        quizAttempts.observeForever { qa ->
            quizAverage.value = qa.map { it.correct * 100 / it.total.toDouble() }.average().toInt()
        }
        availableCourses.observeForever { updateStatistics(it, null) }
        courseProgress.observeForever { updateStatistics(null, it) }
    }

    private fun updateStatistics(
        coursesArg: List<AvailableCourse>?,
        progressArg: List<CourseProgress>?
    ) {
        val courses = coursesArg ?: availableCourses.value ?: return
        val progress = progressArg ?: courseProgress.value ?: return

        val progressForCourse = courses.associate { Pair(it.id, mutableListOf<CourseProgress>()) }
        progress.forEach { progressForCourse[it.chapterID]?.add(it) }
        var globalCompleted = 0.0
        var globalChapterCount = 0.0
        statistics.value = courses.map {
            val completed =
                getCompletedChapterCount(progressForCourse[it.id] ?: error("Missing course"))
            val completionPercent = completed * 100 / it.chapterCount.toDouble()
            globalCompleted += completed
            globalChapterCount += it.chapterCount
            StatisticsEntry(
                it.id,
                it.name,
                it.path,
                completed == it.chapterCount,
                completionPercent,
                completed,
                it.chapterCount
            )
        }
        completion.value = (globalCompleted * 100.0 / globalChapterCount).toInt()
    }
}
