package pl.krzysztofwojciechowski.langcourses.ui.chapter.quizattempts

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_quizattempts.*
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapterlist.CourseChaptersActivity


class QuizAttemptsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: QuizAttemptsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: QuizAttemptsViewModel

    private var courseID: Int = 0
    private var chapterID: Int = 0
    private lateinit var coursePath: String
    private var hasChapter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizattempts)

        viewModel = ViewModelProvider(this).get(QuizAttemptsViewModel::class.java)
        courseID = intent.extras!!.getInt(IE_COURSEID)
        viewModel.courseID.value = courseID
        coursePath = intent.extras!!.getString(IE_COURSEPATH)!!
        viewModel.coursePath.value = coursePath

        hasChapter = intent.extras!!.getBoolean(IE_HASCHAPTER)
        if (hasChapter) {
            val chapterName = intent.extras!!.getString(IE_CHAPTERNAME)!!
            chapterID = intent.extras!!.getInt(IE_CHAPTERID)
            viewModel.chapterName.value = chapterName
            viewModel.chapterID.value = chapterID
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter =
            QuizAttemptsAdapter(
                this::showChapter,
                isClickable = !hasChapter
            )

        recyclerView = findViewById<RecyclerView>(R.id.quizattempts_rv).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewModel.quizAttemptsWithNames.observe(this, Observer {
            viewAdapter.setList(it)
            quizattempts_attempts.text = getString(R.string.stats_number_format, it.size)
            quizattempts_best.text = getString(
                R.string.stats_percent_format,
                it.map { a -> a.percCorrect }.max()?.toInt() ?: 0
            )
            quizattempts_average.text = getString(
                R.string.stats_percent_format,
                it.map { a -> a.percCorrect }.average().toInt()
            )
        })
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return parentActivityIntent
    }

    override fun getParentActivityIntent(): Intent? {
        return if (hasChapter) {
            getChapterIntent(chapterID)
        } else {
            val i = Intent(applicationContext, CourseChaptersActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val bundle = Bundle()
            bundle.putInt(IE_COURSEID, courseID)
            bundle.putString(IE_COURSEPATH, coursePath)
            i.putExtras(bundle)
            i
        }
    }

    private fun getChapterIntent(chapterID: Int): Intent {
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        openIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, courseID)
        bundle.putString(IE_COURSEPATH, coursePath)
        bundle.putInt(IE_CHAPTERID, chapterID)
        openIntent.putExtras(bundle)
        return openIntent
    }

    private fun showChapter(chapterID: Int) {
        startActivity(getChapterIntent(chapterID))
    }
}
