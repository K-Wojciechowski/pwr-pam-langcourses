package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.app.Application
import androidx.lifecycle.*
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.db.*

class ChapterListViewModel(application: Application) : AndroidViewModel(application) {
    var courseID = MutableLiveData(-1)
    private var courseProgressDao: CourseProgressDao
    private var courseProgress: LiveData<List<CourseProgress>>? = null
    val chapters = MutableLiveData(listOf<Chapter>())
    val progress = MutableLiveData(mapOf<Chapter, ChapterProgress>())
    private val progressObserver = Observer<List<CourseProgress>> { cp -> rebuildProgress(cp, null) }

    init {
        val database = MLCDatabase.getDatabase(application)
        courseProgressDao = database.courseProgressDao()

        courseID.observeForever {
            if (courseProgress != null) {
                courseProgress!!.removeObserver(progressObserver)
            }
            courseProgress = courseProgressDao.getProgressForCourse(it)
            courseProgress!!.observeForever(progressObserver)
        }

        chapters.observeForever { rebuildProgress(null, it) }
    }

    private fun rebuildProgress(progArg: List<CourseProgress>?, chapArg: List<Chapter>?) {
        val courseProgress = progArg ?: courseProgress?.value ?: return
        val chapters = chapArg ?: chapters.value ?: return
        val chaptersById = chapters.associateBy { it.chapterID }

        val progress = chapters.associateWith { ChapterProgress.NOT_STARTED }.toMutableMap()

        courseProgress.forEach {
            val chapter = chaptersById[it.chapterID] ?: error("Unknown chapter")
            progress[chapter] = when {
                it.completed -> ChapterProgress.COMPLETE
                it.started -> ChapterProgress.IN_PROGRESS
                else -> ChapterProgress.NOT_STARTED
            }
        }
        this.progress.value = progress
    }
}