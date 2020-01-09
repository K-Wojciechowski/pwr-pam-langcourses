package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.db.ChapterProgress
import pl.krzysztofwojciechowski.langcourses.db.getChapterProgress
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity

class CourseChaptersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ChapterListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var courseID: Int = -1
    private lateinit var course: Course
    private var chaptersByID: Map<Int, Chapter> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_chapters)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

        courseID = intent.extras!!.getInt(IE_COURSEID)
        val coursePath = intent.extras!!.getString(IE_COURSEPATH)!!
        val resourceManager = getResourceManager(applicationContext)
        course = resourceManager.getCourseData(courseID, coursePath)
        val chapters = course.chapters
        supportActionBar?.title = course.name

        chaptersByID = course.chapters.associateBy { it.chapterID }

        viewManager = LinearLayoutManager(this)
        viewAdapter = ChapterListAdapter(this::showChapter, chapters)

        recyclerView = findViewById<RecyclerView>(R.id.cc_rv_course_chapters).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        setProgress()
    }

    private fun setProgress() {
        val progress = course.chapters.associateWith { ChapterProgress.NOT_STARTED }.toMutableMap()
        val progressInts = getChapterProgress(courseID, applicationContext)
        progress.putAll(progressInts.mapKeys { chaptersByID[it.key] ?: error("Unknown chapter") })
        viewAdapter.setProgress(progress)
    }

    private fun showChapter(chapter: Chapter) {
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, chapter.course!!.courseID)
        bundle.putString(IE_COURSEPATH, chapter.course!!.path)
        bundle.putInt(IE_CHAPTERID, chapter.chapterID)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }
}
