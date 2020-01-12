package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_course_chapters.*
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.db.ChapterProgress
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapter.quizattempts.QuizAttemptsActivity

class CourseChaptersActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ChapterListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var courseID: Int = -1
    private lateinit var course: Course
    private lateinit var viewModel: ChapterListViewModel

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

        viewModel = ViewModelProvider(this).get(ChapterListViewModel::class.java)
        viewModel.courseID.value = courseID
        viewModel.chapters.value = chapters

        viewManager = LinearLayoutManager(this)
        viewAdapter = ChapterListAdapter(this::showChapter, chapters)

        viewModel.progress.observe(this, Observer {
            viewAdapter.setProgress(it)
            val complete = it.count { m -> m.value == ChapterProgress.COMPLETE }
            val percent = if (it.isNotEmpty()) (complete * 100 / it.size.toDouble()).toInt() else 0
            cc_percent.text = getString(R.string.cc_percent_format, percent)
            cc_progress.max = it.size
            cc_progress.setProgress(complete, true)
        })

        recyclerView = findViewById<RecyclerView>(R.id.cc_rv_course_chapters).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chapter_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_quizattempts -> {
                showQuizAttempts()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showQuizAttempts() {
        val openIntent = Intent(applicationContext, QuizAttemptsActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, course.courseID)
        bundle.putString(IE_COURSEPATH, course.path)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
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
