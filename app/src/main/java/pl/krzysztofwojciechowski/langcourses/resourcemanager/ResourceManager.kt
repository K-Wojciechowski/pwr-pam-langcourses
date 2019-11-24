package pl.krzysztofwojciechowski.langcourses.resourcemanager

import android.content.Context
import pl.krzysztofwojciechowski.langcourses.Course
import java.io.File
import java.io.FileInputStream

abstract class ResourceManager {
    abstract fun getCourseData(courseID: Int): Course
    fun getAsset(course: Course, path: String): ManagedAsset = getAsset(course.courseID, path)
    abstract fun getAsset(courseID: Int, path: String): ManagedAsset
    abstract fun extractZipData(zipFile: File, coursePath: String)  // TODO file handle?

    fun getImage(course: Course, path: String): ManagedImage = getAsset(course, path) as ManagedImage
    fun getAudio(course: Course, path: String): ManagedAudio = getAsset(course, path) as ManagedAudio
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
}
abstract class ManagedImage(path: String): ManagedAsset(path)
abstract class ManagedAudio(path: String): ManagedAsset(path)

fun getResourceManager(context: Context): ResourceManager {
    return StoredResourceManager(
        context
    )
}