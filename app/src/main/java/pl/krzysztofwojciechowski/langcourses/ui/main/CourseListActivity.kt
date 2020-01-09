package pl.krzysztofwojciechowski.langcourses.ui.main

import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_courselist.*
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.db.AvailableCourse
import pl.krzysztofwojciechowski.langcourses.db.getNextChapterId
import pl.krzysztofwojciechowski.langcourses.resourcemanager.DownloadBroadcastReceiver
import pl.krzysztofwojciechowski.langcourses.resourcemanager.DownloadCompletionHandler
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ResourceManager
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getResourceManager
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapterlist.CourseChaptersActivity
import java.io.File

class CourseListActivity : AppCompatActivity(),
    DownloadCompletionHandler {
    override fun downloadComplete(
        course: AvailableCourse,
        path: File,
        openAfter: Boolean
    ) {
        try {
            resourceManager.extractZipData(path, course.path)
        } finally {
            path.delete()
        }
        if (!openAfter) {
            snackbar(getString(R.string.download_complete, course.name))
        }
        viewModel.handleNewVersion(course.id, course.version, course.path) {
            if (openAfter) {
                if (progressDialog != null) progressDialog!!.hide()
                showCourse(course)
            }
        }
    }

    override fun downloadFailed(
        course: AvailableCourse,
        path: File,
        reason: Int,
        openAfter: Boolean
    ) {
        path.delete()
        if (progressDialog != null) progressDialog!!.hide()
        AlertDialog.Builder(this).setTitle(R.string.download_failed_dialog)
            .setMessage(getString(R.string.download_failed, course.name))
            .setPositiveButton(android.R.string.ok, null).show()
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CourseListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var resourceManager: ResourceManager
    private lateinit var volleyQueue: com.android.volley.RequestQueue
    @Suppress("DEPRECATION")
    private var progressDialog: ProgressDialog? = null
    private lateinit var viewModel: CourseListViewModel

    private var courseCardData = listOf<CourseCardData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courselist)

        viewModel = ViewModelProviders.of(this).get(CourseListViewModel::class.java)

        volleyQueue = Volley.newRequestQueue(this)

        resourceManager =
            getResourceManager(
                applicationContext
            )

        viewManager = LinearLayoutManager(this)
        viewAdapter =
            CourseListAdapter(
                this::downloadCourse,
                this::showCourse,
                this::continueCourse
            )

        recyclerView = findViewById<RecyclerView>(R.id.main_rv_courselist).apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }

        offline_btn.setOnClickListener { refreshCourseCards() }
        swiperefresh.setOnRefreshListener(this::refreshCourseCards)

        viewModel.courseCardData.observeForever {
            courseCardData = it
            viewAdapter.setList(courseCardData)
        }
        refreshCourseCards(onStart = true)
    }

    private fun snackbar(i: Int) {
        Snackbar.make(main_layout, i, Snackbar.LENGTH_SHORT).show()
    }

    private fun snackbar(s: CharSequence) {
        Snackbar.make(main_layout, s, Snackbar.LENGTH_SHORT).show()
    }

    private fun showOffline() {
        offline_icon.visibility = View.VISIBLE
        offline_header.visibility = View.VISIBLE
        offline_description.visibility = View.VISIBLE
        offline_btn.visibility = View.VISIBLE
    }

    private fun hideOffline() {
        offline_icon.visibility = View.GONE
        offline_header.visibility = View.GONE
        offline_description.visibility = View.GONE
        offline_btn.visibility = View.GONE
    }

    private fun refreshCourseCards() {
        refreshCourseCards(false)
    }

    private fun refreshCourseCards(onStart: Boolean = false) {
        swiperefresh.isRefreshing = true
        hideOffline()

        val request = StringRequest(Request.Method.GET, COURSE_LIST_URL,
            Response.Listener<String> { response ->
                val gson = Gson()
                val rawData = gson.fromJson(response, Array<RawCourseCardData>::class.java)
                val availableCourses = rawData.map { it.toAvailableCourse() }.toList()
                viewModel.insertAvailableCourses(availableCourses)
                swiperefresh.isRefreshing = false
            },
            Response.ErrorListener {
                swiperefresh.isRefreshing = false
                if (it is NoConnectionError) {
                    when {
                        courseCardData.isEmpty() -> showOffline()
                        onStart -> snackbar(R.string.go_online_to_refresh)
                        else -> snackbar(R.string.refresh_failed_offline)
                    }
                } else {
                    snackbar(R.string.refresh_failed)
                }
            }
        )

        volleyQueue.add(request)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.course_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                refreshCourseCards()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun startDownload(course: AvailableCourse, openAfter: Boolean) {
        val request = DownloadManager.Request(Uri.parse(course.url))
        request.setDestinationInExternalFilesDir(
            applicationContext,
            null,
            "downloads/" + course.path
        )
        request.setTitle(course.name)
        request.setVisibleInDownloadsUi(false)
        val downloadManager =
            applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = downloadManager.enqueue(request)
        val downloadReceiver =
            DownloadBroadcastReceiver(
                id,
                course,
                openAfter,
                this
            )

        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun downloadCourse(course: AvailableCourse, openAfter: Boolean = false) {
        if (isOnline(applicationContext)) {
            startDownload(course, openAfter)
            if (openAfter) {
                @Suppress("DEPRECATION")
                progressDialog = ProgressDialog.show(
                    this,
                    getString(R.string.downloading_dialog),
                    getString(R.string.downloading_please_wait),
                    true
                )
            } else {
                snackbar(R.string.downloading_please_wait)
            }
        } else {
            AlertDialog.Builder(this).setMessage(R.string.download_offline_error)
                .setTitle(R.string.download_failed_dialog)
                .setPositiveButton(android.R.string.ok, null).show()
        }
    }

    private fun showCourse(course: AvailableCourse) {
        val openIntent = Intent(applicationContext, CourseChaptersActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, course.id)
        bundle.putString(IE_COURSEPATH, course.path)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }

    private fun continueCourse(course: AvailableCourse) {
        val openIntent = Intent(applicationContext, ChapterActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(IE_COURSEID, course.id)
        bundle.putString(IE_COURSEPATH, course.path)
        val chapterID =
            getNextChapterId(
                course.id,
                applicationContext
            )
        if (chapterID == null) {
            // fallback
            showCourse(course)
            return
        } else {
            bundle.putInt(IE_CHAPTERID, chapterID)
        }
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }
}