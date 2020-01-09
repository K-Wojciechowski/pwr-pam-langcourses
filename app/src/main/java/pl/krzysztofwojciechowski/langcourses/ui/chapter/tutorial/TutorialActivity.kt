package pl.krzysztofwojciechowski.langcourses.ui.chapter.tutorial

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_tutorial.*
import pl.krzysztofwojciechowski.langcourses.ChapterTab
import pl.krzysztofwojciechowski.langcourses.IE_SHOW_BACK_BUTTON
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.setSeenTutorial
import pl.krzysztofwojciechowski.langcourses.ui.chapter.SectionsPagerAdapter


class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        setSupportActionBar(tutorial_toolbar)

        val showBackButton = intent.extras?.getBoolean(IE_SHOW_BACK_BUTTON) ?: false
        supportActionBar?.setDisplayShowHomeEnabled(showBackButton)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)

        supportActionBar?.setSubtitle(R.string.tutorial_subtitle)

        val tabNames =
            listOf(R.string.tab_vocabulary, R.string.tab_conversations, R.string.tab_quiz)

        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                supportFragmentManager,
                tabNames,
                listOf(ChapterTab.VOCABULARY, ChapterTab.VOCABULARY, ChapterTab.VOCABULARY)
            )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.isEnabled = false
        val tabStrip = tabs.getChildAt(0) as LinearLayout
        tabStrip.isEnabled = false
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).isClickable = false
        }

        startLearningButton.setOnClickListener { finish() }

        setSeenTutorial(applicationContext)
    }
}