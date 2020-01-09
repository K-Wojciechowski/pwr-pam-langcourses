package pl.krzysztofwojciechowski.langcourses

import pl.krzysztofwojciechowski.langcourses.db.AvailableCourse

data class RawCourseCardData(
    val courseid: Int,
    val coursename: String,
    val coursepath: String,
    val courseversion: Int,
    val language: String,
    val baselang: String,
    val level: String,
    val chaptercount: Int,
    val filesize: Double,
    val coverimage: String,
    val coverbgcolor: String,
    val covertextcolor: String,
    val url: String
) {
    fun toAvailableCourse(): AvailableCourse {
        return AvailableCourse(
            courseid,
            coursename,
            coursepath,
            courseversion,
            language,
            level,
            chaptercount,
            coverimage,
            coverbgcolor,
            covertextcolor,
            filesize,
            url
        )
    }
}

data class CourseCardData(
    val course: AvailableCourse,
    var downloadedVersion: Int? = null,
    var inProgress: Boolean = false,
    var currentProgress: Double = 0.0
)

