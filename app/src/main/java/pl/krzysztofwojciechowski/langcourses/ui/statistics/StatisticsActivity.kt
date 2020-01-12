package pl.krzysztofwojciechowski.langcourses.ui.statistics

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_statistics.*
import pl.krzysztofwojciechowski.langcourses.IE_COURSEID
import pl.krzysztofwojciechowski.langcourses.IE_COURSEPATH
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.ui.chapterlist.CourseChaptersActivity

class StatisticsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: StatisticsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        viewManager = LinearLayoutManager(this)
        viewAdapter =
            StatisticsAdapter(this::showCourse)

        recyclerView = findViewById<RecyclerView>(R.id.stats_rv).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
        viewModel.completion.observe(
            this,
            Observer {
                stats_completion_overall.text = getString(R.string.stats_percent_format, it)
            })
        viewModel.quizAverage.observe(
            this,
            Observer {
                stats_quiz_percent_overall.text = getString(R.string.stats_percent_format, it)
            })
        viewModel.statistics.observe(this, Observer {
            viewAdapter.setList(it)
        })
    }

    private fun showCourse(courseID: Int, coursePath: String) {
        val openIntent = Intent(applicationContext, CourseChaptersActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, courseID)
        bundle.putString(IE_COURSEPATH, coursePath)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }

}
