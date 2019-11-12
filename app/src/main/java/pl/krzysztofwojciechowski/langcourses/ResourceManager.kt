package pl.krzysztofwojciechowski.langcourses

import com.google.gson.Gson

abstract class ResourceManager {
    private val gson = Gson()
    private fun parseJsonToCourseData(jsonText: String): Course {
        val c = gson.fromJson(jsonText, Course::class.java)
        c.registerResourceManager(this)
        return c
    }

    abstract fun getCourseData(courseID: Int): Course
    fun getAsset(course: Course, path: String): ManagedAsset = getAsset(course.courseID, path)
    abstract fun getAsset(courseID: Int, path: String): ManagedAsset

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

abstract class ManagedAsset(val path: String)
abstract class ManagedImage(path: String): ManagedAsset(path)
abstract class ManagedAudio(path: String): ManagedAsset(path)
