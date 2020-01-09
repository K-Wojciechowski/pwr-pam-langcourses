package pl.krzysztofwojciechowski.langcourses.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AvailableCourseDao {
    @Query("SELECT * from available_course")
    fun getAvailableCourses(): LiveData<List<AvailableCourse>>

    @Query("SELECT * from available_course WHERE id = :courseID")
    suspend fun getCourse(courseID: Int): AvailableCourse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: AvailableCourse)

    @Update
    suspend fun update(course: AvailableCourse)

    @Query("DELETE FROM available_course")
    suspend fun deleteAll()

}

@Dao
interface DownloadedCourseDao {
    @Query(value = "SELECT * FROM downloaded_course")
    fun getDownloadedCourses(): LiveData<List<DownloadedCourse>>

    @Query(value = "SELECT * FROM downloaded_course WHERE courseid IN (:courseIDs) ORDER BY courseid ASC, version ASC")
    suspend fun getDownloadedCourses(courseIDs: IntArray): List<DownloadedCourse>

    @Query("DELETE FROM downloaded_course WHERE courseid=:courseID")
    suspend fun deleteByID(courseID: Int)

    @Query("DELETE FROM downloaded_course WHERE courseid IN (:courseIDs)")
    suspend fun deleteByIDs(courseIDs: IntArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: DownloadedCourse)

    @Update
    suspend fun update(course: DownloadedCourse)

    @Delete
    suspend fun delete(course: DownloadedCourse)
}

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM course_progress WHERE courseid = :courseID ORDER BY chapterid")
    fun getProgressForCourse(courseID: Int): LiveData<List<CourseProgress>>

    @Query("SELECT * FROM course_progress WHERE courseid = :courseID ORDER BY chapterid")
    suspend fun getProgressForCourseDirect(courseID: Int): List<CourseProgress>

    @Query("SELECT * FROM course_progress WHERE courseid = :courseID and chapterid = :chapterID")
    suspend fun getProgressForChapter(courseID: Int, chapterID: Int): CourseProgress?

    @Query("SELECT * FROM course_progress ORDER BY courseid")
    fun getProgress(): LiveData<List<CourseProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: CourseProgress)

    @Update
    suspend fun update(progress: CourseProgress)

    @Delete
    suspend fun delete(progress: CourseProgress)

}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempt WHERE courseid = :courseID")
    fun getAttemptsForCourse(courseID: Int): List<QuizAttempt>

    @Query("SELECT * FROM quiz_attempt WHERE courseid = :courseID and chapterid = :chapterID")
    fun getAttemptsForChapter(courseID: Int, chapterID: Int): QuizAttempt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: QuizAttempt)

    @Update
    suspend fun update(attempt: QuizAttempt)

    @Delete
    suspend fun delete(attempt: QuizAttempt)
}
