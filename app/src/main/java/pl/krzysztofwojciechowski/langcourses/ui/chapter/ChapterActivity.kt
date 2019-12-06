package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chapter.*
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getChapter
import pl.krzysztofwojciechowski.langcourses.ui.chapter.tutorial.TutorialActivity

class ChapterActivity : AppCompatActivity() {
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        setSupportActionBar(chapter_toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val courseID = intent.extras!!.getInt(IE_COURSEID)
        val chapterID = intent.extras!!.getInt(IE_CHAPTERID)

        val chapter = getChapter(applicationContext, courseID, chapterID)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        pageViewModel.chapter.value = chapter


        supportActionBar?.title = chapter.name
        supportActionBar?.subtitle = chapter.translatedName

        val tabNames = mutableListOf<Int>()
        val tabIDs = mutableListOf<ChapterTab>()

        if (chapter.vocabulary.isNotEmpty()) {
            tabNames.add(R.string.tab_vocabulary)
            tabIDs.add(ChapterTab.VOCABULARY)
        }
        if (chapter.conversations.isNotEmpty()) {
            tabNames.add(R.string.tab_conversations)
            tabIDs.add(ChapterTab.CONVERSATIONS)
        }
        if (chapter.quiz.isNotEmpty()) {
            tabNames.add(R.string.tab_quiz)
            tabIDs.add(ChapterTab.QUIZ)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager,
            tabNames,
            tabIDs
        )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (tabIDs[position] == ChapterTab.QUIZ) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }
        })

        fab.setOnClickListener { view ->
            Snackbar.make(view, "This is chapter $chapterID of course $courseID", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (!hasSeenTutorial(applicationContext)) {
            openTutorial(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chapter, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_tutorial) {
            openTutorial(true)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openTutorial(showBackButton: Boolean) {
        val openIntent = Intent(applicationContext, TutorialActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(IE_SHOW_BACK_BUTTON, showBackButton)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }
}