package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.IE_CHAPTERID
import pl.krzysztofwojciechowski.langcourses.IE_COURSEID
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.db.ChapterProgress
import pl.krzysztofwojciechowski.langcourses.db.getChapterProgress
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity

class CourseChaptersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ChapterListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_chapters)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

        val courseID = intent.extras!!.getInt(IE_COURSEID)
        val resourceManager = getResourceManager(applicationContext)
        val course = resourceManager.getCourseData(courseID)
        val chapters = course.chapters
        supportActionBar?.title = course.name

        val chaptersByID = course.chapters.associateBy { it.chapterID }
        val progress = course.chapters.associateWith { ChapterProgress.NOT_STARTED }.toMutableMap()
        val progressInts = getChapterProgress(courseID, applicationContext)
        progress.putAll(progressInts.mapKeys { chaptersByID[it.key] ?: error("Unknown chapter") })

        viewManager = LinearLayoutManager(this)
        viewAdapter =
            ChapterListAdapter(
                this::showChapter,
                chapters,
                progress
            )

        recyclerView = findViewById<RecyclerView>(R.id.cc_rv_course_chapters).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun showChapter(chapter: Chapter) {
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, chapter.course!!.courseID)
        bundle.putInt(IE_CHAPTERID, chapter.chapterID)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }
}
