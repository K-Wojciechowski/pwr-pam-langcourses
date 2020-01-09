package pl.krzysztofwojciechowski.langcourses.resourcemanager

import android.content.Context
import android.net.Uri
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.Course
import pl.krzysztofwojciechowski.langcourses.db.DownloadedCourse
import java.io.File
import java.io.FileInputStream

abstract class ResourceManager {
    abstract fun getCourseData(courseID: Int, coursePath: String): Course
    fun getAsset(course: Course, path: String): ManagedAsset = getAsset(course.courseID, course.path!!, path)
    abstract fun getAsset(courseID: Int, coursePath: String, path: String): ManagedAsset
    abstract fun extractZipData(zipFile: File, coursePath: String)
    abstract fun deleteOldDownloads(downloadedCourses: List<DownloadedCourse>)

    fun getChapter(courseID: Int, coursePath: String, chapterID: Int): Chapter {
        return getCourseData(courseID, coursePath).getChapterByID(chapterID)
    }
}

abstract class ManagedEntity {
    var resourceManager: ResourceManager? = null

    open fun registerResourceManager(resourceManager: ResourceManager) {
        this.resourceManager = resourceManager
    }
}

abstract class ManagedCourseItem : ManagedEntity() {
    var course: Course? = null

    open fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        this.resourceManager = resourceManager
        this.course = course
    }

    fun getAssetUri(path: String?): Uri? {
        return if (path == null) {
            null
        } else {
            resourceManager!!.getAsset(course!!, path).getUri()
        }
    }


    fun getAssetFile(path: String?): File? {
        return if (path == null) {
            null
        } else {
            resourceManager!!.getAsset(course!!, path).getFile()
        }
    }
}

abstract class ManagedAsset(val path: String) {
    abstract fun getFile(): File
    abstract fun getInputStream(): FileInputStream
    abstract fun getUri(): Uri
}

fun getResourceManager(context: Context): ResourceManager {
    return StoredResourceManager(
        context
    )
}

fun getChapter(context: Context, courseID: Int, coursePath: String, chapterID: Int): Chapter {
    return getResourceManager(context).getChapter(courseID, coursePath, chapterID)
}