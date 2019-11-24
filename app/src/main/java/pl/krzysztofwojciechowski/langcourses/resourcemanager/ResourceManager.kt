package pl.krzysztofwojciechowski.langcourses.resourcemanager

import android.content.Context
import android.net.Uri
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.Course
import java.io.File
import java.io.FileInputStream

abstract class ResourceManager {
    abstract fun getCourseData(courseID: Int): Course
    fun getAsset(course: Course, path: String): ManagedAsset = getAsset(course.courseID, path)
    abstract fun getAsset(courseID: Int, path: String): ManagedAsset
    abstract fun extractZipData(zipFile: File, coursePath: String)  // TODO file handle?

    fun getChapter(courseID: Int, chapterID: Int): Chapter {
        return getCourseData(courseID).getChapterByID(chapterID)
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

fun getChapter(context: Context, courseID: Int, chapterID: Int): Chapter {
    return getResourceManager(context).getChapter(courseID, chapterID)
}