package pl.krzysztofwojciechowski.langcourses

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MLCApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}