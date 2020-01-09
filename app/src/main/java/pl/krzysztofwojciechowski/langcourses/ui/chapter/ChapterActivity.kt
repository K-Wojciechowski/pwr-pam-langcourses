package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chapter.*
import kotlinx.coroutines.launch
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.db.saveInteractionWith
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getChapter
import pl.krzysztofwojciechowski.langcourses.ui.chapter.tutorial.TutorialActivity
import java.io.File


class ChapterActivity : AppCompatActivity() {
    private lateinit var pageViewModel: PageViewModel
    private lateinit var musicService: MusicPlayerService
    private var musicBound = false
    private lateinit var currentTabID: ChapterTab

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)
        setSupportActionBar(chapter_toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        musicService = MusicPlayerService()

        val courseID = intent.extras!!.getInt(IE_COURSEID)
        val coursePath = intent.extras!!.getString(IE_COURSEPATH)!!
        val chapterID = intent.extras!!.getInt(IE_CHAPTERID)
        loadChapter(courseID, coursePath, chapterID)
    }

    private fun loadChapter(courseID: Int, coursePath: String, chapterID: Int) {
        val chapter = getChapter(applicationContext, courseID, coursePath, chapterID)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java)
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

        currentTabID = tabIDs[0]

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
                currentTabID = tabIDs[position]
                if (currentTabID == ChapterTab.QUIZ) {
                    fab.hide()
                } else {
                    fab.show()
                }
            }
        })

        fab.setOnClickListener { view ->
            saveInteraction()
            when {
                musicService.mediaPlayer.isPlaying -> musicService.stopPlaying()
                currentTabID == ChapterTab.VOCABULARY -> startPlaying(pageViewModel.chapter.value!!.getVocabularyPlaylist())
                currentTabID == ChapterTab.CONVERSATIONS -> startPlaying(pageViewModel.chapter.value!!.getConversationPlaylist())
            }
        }

        if (!hasSeenTutorial(applicationContext)) {
            openTutorial(false)
        }
    }

    fun saveInteraction() {
        val chapter = pageViewModel.chapter.value!!
        pageViewModel.viewModelScope.launch {
            saveInteractionWith(chapter.course!!.courseID, chapter.chapterID, applicationContext)
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

    fun startNextChapter() {
        val courseID = pageViewModel.chapter.value!!.course!!.courseID
        val nextChapterID = pageViewModel.nextChapterID.value ?: return
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, courseID)
        bundle.putString(IE_COURSEPATH, pageViewModel.chapter.value!!.course!!.path)
        bundle.putInt(IE_CHAPTERID, nextChapterID)
        openIntent.putExtras(bundle)
        finish()
        startActivity(openIntent)
    }


    // MUSIC SERVICE
    // Communication handlers
    private var playIntent: Intent? = null
    private val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicPlayerService.MusicBinder
            this@ChapterActivity.musicService = binder.getService()
            bindToService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            unbindService()
        }
    }

    private val guiUpdateRunnable = Runnable { setPlayPauseIcon() }


    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicPlayerService::class.java)
            bindService(playIntent!!, musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!musicBound) bindToService()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicService.stopPlaying()
        unbindService()
    }

    override fun onStop() {
        super.onStop()
        musicService.stopPlaying()
        unbindService()
    }

    // Service communication
    fun bindToService() {
        musicService.files = listOf()
        musicService.guiUpdateRunnable = guiUpdateRunnable
        musicService.stateChangeCallback = this::stateChangeCallback
        musicBound = true

        if (musicService.nowPlaying != null) {
            musicService.startUpdating()
        }
        setPlayPauseIcon()
    }

    fun unbindService() {
        musicService.guiUpdateRunnable = null
        musicService.stateChangeCallback = null
        musicBound = false
    }

    // Callbacks
    fun startPlaying(playlist: List<File>) {
        musicService.files = playlist
        musicService.startPlaying(playlist[0])
        setPlayPauseIcon()
    }

    fun startPlaying(file: File) {
        startPlaying(listOf(file))
    }

    private fun setPlayPauseIcon() {
        if (musicService.state == AudioState.PLAY) fab.setImageResource(R.drawable.ic_action_stop_chapter)
        else fab.setImageResource(R.drawable.ic_action_play_chapter)
    }

    private fun stateChangeCallback(type: AudioState) {
        setPlayPauseIcon()
    }
}
