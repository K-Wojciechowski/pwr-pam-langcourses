package pl.krzysztofwojciechowski.langcourses

import android.content.Context

fun hasSeenTutorial(context: Context): Boolean {
    val sp = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    return sp.getBoolean(SP_SEEN_TUTORIAL, false)
}

fun setSeenTutorial(context: Context, value: Boolean = true) {
    val sp = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putBoolean(SP_SEEN_TUTORIAL, value)
    editor.apply()
}
