package pl.krzysztofwojciechowski.langcourses.resourcemanager

import pl.krzysztofwojciechowski.langcourses.db.AvailableCourse
import java.io.File

interface DownloadCompletionHandler {
    fun downloadComplete(
        course: AvailableCourse,
        path: File,
        openAfter: Boolean
    )

    fun downloadFailed(
        course: AvailableCourse,
        path: File,
        reason: Int,
        openAfter: Boolean
    )
}