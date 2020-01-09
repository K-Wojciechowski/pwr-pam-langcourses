package pl.krzysztofwojciechowski.langcourses.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [AvailableCourse::class, DownloadedCourse::class, CourseProgress::class, QuizAttempt::class],
    version = 1,
    exportSchema = false
)
abstract class MLCDatabase : RoomDatabase() {

    abstract fun availableCourseDao(): AvailableCourseDao
    abstract fun downloadedCourseDao(): DownloadedCourseDao
    abstract fun courseProgressDao(): CourseProgressDao
    abstract fun quizAttemptDao(): QuizAttemptDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MLCDatabase? = null

        fun getDatabase(context: Context): MLCDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MLCDatabase::class.java,
                    "mlc_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}