package pl.krzysztofwojciechowski.langcourses

import android.graphics.drawable.Drawable

data class CourseCardData(
    val courseid: Int,
    val name: String,
    val latestVersion: Int,
    val downloadedVersion: Int?,
    val fileSize: Int,
    val coverImage: Drawable?,
    val coverBgColor: Int,
    val coverFgColor: Int,
    val inProgress: Boolean,
    val currentProgress: Double?
)