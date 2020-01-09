package pl.krzysztofwojciechowski.langcourses

import org.threeten.bp.format.DateTimeFormatter

val QUIZ_CORRECT_PERCENT = 75

val IE_COURSEID = "courseID"
val IE_CHAPTERID = "chapterID"
val IE_DEF_IMAGEURI = "defImageURI"
val IE_DEF_WORD = "defWord"
val IE_DEF_TRANSLATION = "defTranslation"
val IE_DEF_DEFINITION = "defDefinition"
val IE_SHOW_BACK_BUTTON = "showBackButton"

val SHARED_PREFS = "mlcSharedPrefs"
val SP_SEEN_TUTORIAL = "seenTutorial"

val COURSE_LIST_URL = "https://krzysztofwojciechowski.pl/pwr/pam/courses/courselist.json"

const val INTENT_PLAYPAUSE = "pl.krzysztofwojciechowski.langcourses.playpause"
const val INTENT_PREVIOUS = "pl.krzysztofwojciechowski.langcourses.previous"
const val INTENT_NEXT = "pl.krzysztofwojciechowski.langcourses.next"
const val INTENT_STOP = "pl.krzysztofwojciechowski.langcourses.stop"

enum class ChapterTab { VOCABULARY, CONVERSATIONS, QUIZ, BLANK }

val TEXT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")