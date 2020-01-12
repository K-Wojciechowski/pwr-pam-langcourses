package pl.krzysztofwojciechowski.langcourses.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.krzysztofwojciechowski.langcourses.CourseCardData
import pl.krzysztofwojciechowski.langcourses.db.*
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager

class CourseListViewModel(application: Application) : AndroidViewModel(application) {
    private val availableCourseDao: AvailableCourseDao
    private var downloadedCourseDao: DownloadedCourseDao
    private var courseProgressDao: CourseProgressDao

    val availableCourses: LiveData<List<AvailableCourse>>
    val downloadedCourses: LiveData<List<DownloadedCourse>>
    val courseProgress: LiveData<List<CourseProgress>>

    val courseCardData = MutableLiveData(listOf<CourseCardData>())

    init {
        val database = MLCDatabase.getDatabase(application)
        availableCourseDao = database.availableCourseDao()
        downloadedCourseDao = database.downloadedCourseDao()
        courseProgressDao = database.courseProgressDao()
        availableCourses = availableCourseDao.getAvailableCourses()
        downloadedCourses = downloadedCourseDao.getDownloadedCourses()
        courseProgress = courseProgressDao.getProgress()

        availableCourses.observeForever { updateCourseCardData(it, null, null) }
        downloadedCourses.observeForever { updateCourseCardData(null, it, null) }
        courseProgress.observeForever { updateCourseCardData(null, null, it) }
    }

    private fun updateCourseCardData(
        avArg: List<AvailableCourse>?,
        dnArg: List<DownloadedCourse>?,
        cpArg: List<CourseProgress>?
    ) {
        val available: List<AvailableCourse> = avArg ?: availableCourses.value ?: return
        val downloaded: List<DownloadedCourse> = dnArg ?: downloadedCourses.value ?: listOf()
        val progress: List<CourseProgress> = cpArg ?: courseProgress.value ?: listOf()

        val ccById: Map<Int, CourseCardData> =
            available.map { CourseCardData(it, null, false, 0.0) }
                .associateBy { it.course.id }
        downloaded.forEach {
            ccById[it.courseID]?.downloadedVersion = it.version
        }
        val ccs = ccById.values.toList()
        ccs.forEach { cc ->
            val progressInfo = progress.filter { it.courseID == cc.course.id }
            val completed = getCompletedChapterCount(progressInfo)
            cc.currentProgress = completed / cc.course.chapterCount.toDouble()
            cc.inProgress = progressInfo.any { it.started || it.completed }
        }
        courseCardData.value = ccs
    }

    fun insertAvailableCourses(availableCourses: List<AvailableCourse>) {
        viewModelScope.launch {
            availableCourses.forEach {
                availableCourseDao.insert(it)
            }
        }
    }

    /** Insert new version to the database and delete files from older versions. */
    fun handleNewVersion(courseID: Int, version: Int, path: String, doneCallback: () -> Unit) {
        viewModelScope.launch {
            val dc = downloadedCourseDao.getDownloadedCourses(intArrayOf(courseID))
            getResourceManager(getApplication()).deleteOldDownloads(dc)
            downloadedCourseDao.deleteByIDs(dc.map { it.courseID }.toIntArray())
            downloadedCourseDao.insert(DownloadedCourse(courseID, version, path))
            doneCallback()
        }
    }
}