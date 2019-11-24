package pl.krzysztofwojciechowski.langcourses

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import pl.krzysztofwojciechowski.langcourses.ui.main.SectionsPagerAdapter

class ChapterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        val courseID = intent.extras!!.getInt(IE_COURSEID)
        val chapterID = intent.extras!!.getInt(IE_CHAPTERID)

        Toast.makeText(applicationContext, "This is chapter $chapterID of course $courseID", Toast.LENGTH_SHORT)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "This is chapter $chapterID of course $courseID", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}