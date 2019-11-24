package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
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
        val resourceManager =
            getResourceManager(
                applicationContext
            )
        val course = resourceManager.getCourseData(courseID)
        val chapters = course.chapters
        supportActionBar?.title = course.name

        val progress = mutableMapOf<Chapter, ChapterProgress>()
        chapters.forEach { c -> progress[c] =
            ChapterProgress.NOT_STARTED
        }

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
