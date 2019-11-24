package pl.krzysztofwojciechowski.langcourses.ui.main

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.resourcemanager.DownloadBroadcastReceiver
import pl.krzysztofwojciechowski.langcourses.resourcemanager.DownloadCompletionHandler
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ResourceManager
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapterlist.CourseChaptersActivity
import java.io.File

class CourseListActivity : AppCompatActivity(),
    DownloadCompletionHandler {
    override fun downloadComplete(course: CourseCardData, path: File) {
        Log.e("X", "Y")
        try {
            resourceManager.extractZipData(path, course.coursePath)
        } finally {
            path.delete()
        }
    }

    override fun downloadFailed(course: CourseCardData, path: File, reason: Int) {
        Log.e("z", "z")
        path.delete()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CourseListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var resourceManager: ResourceManager

    val courseCardData = mutableListOf<CourseCardData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courselist)

        resourceManager =
            getResourceManager(
                applicationContext
            )

         // TODO placeholder
        courseCardData.add(
            CourseCardData(
                1,
                "Angielski Podstawowy",
                1,
                1,
                4096,
                getDrawable(R.drawable.sample_cover),
                Color.parseColor("#0D47A1"),
                Color.WHITE,
                true,
                0.25,
                "angielski-podstawowy-1-1",
                "https://krzysztofwojciechowski.pl/pwr/pam/demo.zip"
            )
        )

        viewManager = LinearLayoutManager(this)
        viewAdapter =
            CourseListAdapter(
                this::showCourse,
                this::continueCourse,
                courseCardData
            )

        recyclerView = findViewById<RecyclerView>(R.id.main_rv_courselist).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.course_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.ph_download) {
            startDownload(courseCardData[0])
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startDownload(course: CourseCardData) {
        val request = DownloadManager.Request(Uri.parse(course.url))
        request.setDestinationInExternalFilesDir(applicationContext, null, "downloads/" + course.coursePath)
        request.setTitle(course.name)
        request.setVisibleInDownloadsUi(false)
        val downloadManager = applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = downloadManager.enqueue(request)
        val downloadReceiver =
            DownloadBroadcastReceiver(
                id,
                course,
                this
            )

        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun showCourse(course: CourseCardData) {
        val openIntent = Intent(applicationContext, CourseChaptersActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, course.courseid)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }

    private fun continueCourse(course: CourseCardData) {
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, course.courseid)
        bundle.putInt(IE_CHAPTERID, -1) // TODO find first incomplete/notstarted chapter ID in database
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }
}