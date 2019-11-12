package pl.krzysztofwojciechowski.langcourses

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CourseListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CourseListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courselist)

         // TODO placeholder
        val courseCardData = mutableListOf(
            CourseCardData(1, "Angielski Podstawowy", 1,    1, 4096,
                getDrawable(R.drawable.sample_cover),
                Color.parseColor("#0D47A1"), Color.WHITE, true, 0.25)
        )

        viewManager = LinearLayoutManager(this)
        viewAdapter = CourseListAdapter(this::showCourse, courseCardData)

        recyclerView = findViewById<RecyclerView>(R.id.main_rv_courselist).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun showCourse(course: CourseCardData) {

    }
}