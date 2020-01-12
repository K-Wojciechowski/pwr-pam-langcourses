package pl.krzysztofwojciechowski.langcourses.ui.chapter.quizattempts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import pl.krzysztofwojciechowski.langcourses.db.MLCDatabase
import pl.krzysztofwojciechowski.langcourses.db.QuizAttempt
import pl.krzysztofwojciechowski.langcourses.db.QuizAttemptDao
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager

class QuizAttemptsViewModel(application: Application) : AndroidViewModel(application) {
    val courseID = MutableLiveData<Int?>(null)
    val coursePath = MutableLiveData<String?>(null)
    val chapterID = MutableLiveData<Int?>(null)
    val chapterName = MutableLiveData<String?>(null)
    val chapterNames = MutableLiveData(mapOf<Int, String>())
    private val quizAttemptDao: QuizAttemptDao
    private var quizAttempts: LiveData<List<QuizAttempt>>? = null
    private var quizAttemptsObserver =
        Observer<List<QuizAttempt>> { updateQuizAttemptsWithNames(it) }
    var quizAttemptsWithNames = MutableLiveData(listOf<QuizAttemptWithName>())

    init {
        val database = MLCDatabase.getDatabase(application)
        quizAttemptDao = database.quizAttemptDao()

        courseID.observeForever {
            if (it == null) return@observeForever
            updateChapterNames()
            rebuildChapterCourseObservers(it, chapterID.value)
        }

        coursePath.observeForever {
            if (courseID.value == null) return@observeForever
            updateChapterNames()
            if (quizAttempts?.value != null) updateQuizAttemptsWithNames(quizAttempts!!.value!!)
        }

        chapterID.observeForever {
            if (courseID.value == null) return@observeForever
            updateChapterNames()
            rebuildChapterCourseObservers(courseID.value!!, it)
        }
    }

    private fun rebuildChapterCourseObservers(course: Int, chapter: Int?) {
        quizAttempts?.removeObserver(quizAttemptsObserver)
        quizAttempts = if (chapter == null) {
            quizAttemptDao.getAttemptsForCourse(course)
        } else {
            quizAttemptDao.getAttemptsForChapter(course, chapter)
        }
        quizAttempts!!.observeForever(quizAttemptsObserver)
    }

    private fun updateChapterNames() {
        if (courseID.value != null && coursePath.value != null && chapterID.value == null) {
            val course = getResourceManager(getApplication()).getCourseData(
                courseID.value!!,
                coursePath.value!!
            )
            chapterNames.value = course.chapters.associate { Pair(it.chapterID, it.name) }
        }
    }

    private fun updateQuizAttemptsWithNames(attempts: List<QuizAttempt>) {
        quizAttemptsWithNames.value = attempts.map {
            when {
                chapterName.value != null -> QuizAttemptWithName(it, chapterName.value!!)
                chapterNames.value == null -> QuizAttemptWithName(it, "??")
                else -> QuizAttemptWithName(it, chapterNames.value!![it.chapterID] ?: "??")
            }
        }
    }
}
