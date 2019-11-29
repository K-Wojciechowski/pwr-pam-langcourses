package pl.krzysztofwojciechowski.langcourses.ui.chapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pl.krzysztofwojciechowski.langcourses.Chapter

class PageViewModel : ViewModel() {
    val chapter = MutableLiveData<Chapter>()

    private val _index = MutableLiveData<String>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: String) {
        _index.value = index
    }
}