package pl.krzysztofwojciechowski.langcourses.ui.statistics

data class StatisticsEntry(
    val courseID: Int,
    val courseName: String,
    val coursePath: String,
    val isComplete: Boolean,
    val completionPercent: Double,
    val chapterCompleteCount: Int,
    val chapterCount: Int
)
