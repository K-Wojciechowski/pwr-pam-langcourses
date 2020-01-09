package pl.krzysztofwojciechowski.langcourses.db

import androidx.room.*

@Dao
interface AvailableCourseDao {
    @Query("SELECT * from available_course")
    fun getAvailableCourses(): List<AvailableCourse>

    @Query("SELECT * from available_course WHERE id = :courseID")
    fun getCourse(courseID: Int): AvailableCourse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(course: AvailableCourse)
    fun insert(course: AvailableCourse)

    @Update
//  TODO  suspend fun update(course: AvailableCourse)
    fun update(course: AvailableCourse)

    @Query("DELETE FROM available_course")
    suspend fun deleteAll()

}

@Dao
interface DownloadedCourseDao {
    @Query(value = "SELECT * FROM downloaded_course")
    fun getDownloadedCourses(): List<DownloadedCourse>

    @Query(value = "SELECT * FROM downloaded_course WHERE courseid IN (:courseIds) ORDER BY courseid ASC, version ASC")
    fun getDownloadedCourses(courseIds: IntArray): List<DownloadedCourse>

    @Query("DELETE FROM downloaded_course WHERE courseid=:courseId")
    suspend fun deleteById(courseId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(course: DownloadedCourse)
    fun insert(course: DownloadedCourse)

    @Update
    suspend fun update(course: DownloadedCourse)

    @Delete
    suspend fun delete(course: DownloadedCourse)
}

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM course_progress WHERE courseid = :courseId")
    fun getProgressForCourse(courseId: Int): List<CourseProgress>

    @Query("SELECT * FROM course_progress WHERE courseid = :courseId and chapterid = :chapterId")
    fun getProgressForChapter(courseId: Int, chapterId: Int): CourseProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: CourseProgress)

    @Update
    suspend fun update(progress: CourseProgress)

    @Delete
    suspend fun delete(progress: CourseProgress)
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempt WHERE courseid = :courseId")
    fun getAttemptsForCourse(courseId: Int): List<QuizAttempt>

    @Query("SELECT * FROM quiz_attempt WHERE courseid = :courseId and chapterid = :chapterId")
    fun getAttemptsForChapter(courseId: Int, chapterId: Int): QuizAttempt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: QuizAttempt)

    @Update
    suspend fun update(attempt: QuizAttempt)

    @Delete
    suspend fun delete(attempt: QuizAttempt)
}
