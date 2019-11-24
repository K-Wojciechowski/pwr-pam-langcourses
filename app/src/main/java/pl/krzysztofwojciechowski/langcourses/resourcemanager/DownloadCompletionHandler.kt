package pl.krzysztofwojciechowski.langcourses.resourcemanager

import pl.krzysztofwojciechowski.langcourses.CourseCardData
import java.io.File

interface DownloadCompletionHandler {
    fun downloadComplete(course: CourseCardData, path: File)
    fun downloadFailed(course: CourseCardData, path: File, reason: Int)
}