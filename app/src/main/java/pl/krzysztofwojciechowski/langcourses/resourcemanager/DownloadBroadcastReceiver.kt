package pl.krzysztofwojciechowski.langcourses.resourcemanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.DownloadManager
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toFile
import pl.krzysztofwojciechowski.langcourses.CourseCardData


class DownloadBroadcastReceiver(val id: Long, val course: CourseCardData, private val handler: DownloadCompletionHandler) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val action = intent.action
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val c = dm.query(query)
            if (c.moveToFirst()) {
                val statusColumnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val status = c.getInt(statusColumnIndex)

                val uriColumnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val uriString = c.getString(uriColumnIndex)
                val uri = Uri.parse(uriString)

                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    handler.downloadComplete(course, uri.toFile())
                    // TODO
                    Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show()
                } else {
                    handler.downloadFailed(course, uri.toFile(), status)
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
