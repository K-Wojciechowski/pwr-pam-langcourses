package pl.krzysztofwojciechowski.langcourses.db

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "available_course")
data class AvailableCourse(
    @PrimaryKey val id: Int,
    var name: String,
    var path: String,
    var version: Int,
    var language: String,
    var level: String,
    @ColumnInfo(name = "chapter_count") var chapterCount: Int,
    @ColumnInfo(name = "cover_image") var coverImage: String,
    @ColumnInfo(name = "cover_bg_color") var coverBgColor: String,
    @ColumnInfo(name = "cover_text_color") var coverTextColor: String,
    @ColumnInfo(name = "file_size") var fileSize: Double,
    var url: String
) {
    val coverBgColorA: Int
        get() = Color.parseColor(coverBgColor)

    val coverTextColorA: Int
        get() = Color.parseColor(coverTextColor)
}

@Entity(
    tableName = "downloaded_course",
    foreignKeys = [ForeignKey(
        entity = AvailableCourse::class,
        parentColumns = ["id"],
        childColumns = ["courseid"]
    )]
)
data class DownloadedCourse(
    @PrimaryKey @ColumnInfo(name = "courseid") var courseID: Int,
    var version: Int,
    var path: String
)

@Entity(
    tableName = "course_progress",
    foreignKeys = [ForeignKey(
        entity = AvailableCourse::class,
        parentColumns = ["id"],
        childColumns = ["courseid"]
    )],
    primaryKeys = ["courseid", "chapterid"]
)
data class CourseProgress(
    @ColumnInfo(name = "courseid") var courseID: Int,
    @ColumnInfo(name = "chapterid") var chapterID: Int,
    var started: Boolean,
    var completed: Boolean
)

@Entity(
    tableName = "quiz_attempt",
    foreignKeys = [ForeignKey(
        entity = AvailableCourse::class,
        parentColumns = ["id"],
        childColumns = ["courseid"]
    )],
    primaryKeys = ["courseid", "chapterid", "attempt_date"]
)
data class QuizAttempt(
    @ColumnInfo(name = "courseid") var courseID: Int,
    @ColumnInfo(name = "chapterid") var chapterID: Int,
    @ColumnInfo(name = "attempt_date") var attemptDate: String, // timestamp
    var correct: Int,
    var total: Int
)
