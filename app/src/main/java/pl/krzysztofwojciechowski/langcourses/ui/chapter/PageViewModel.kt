package pl.krzysztofwojciechowski.langcourses.ui.chapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.Question
import pl.krzysztofwojciechowski.langcourses.QuizState
import pl.krzysztofwojciechowski.langcourses.R

class PageViewModel : ViewModel() {
    val chapter = MutableLiveData<Chapter>()

    val currentQuizQuestion = MutableLiveData<Question?>()
    val currentQuizQuestionNumber = MutableLiveData<Int>()
    val quizAnswers = MutableLiveData<Map<Question, Int>>()
    val quizState = MutableLiveData(QuizState.NOTSTARTED)
    val answerRevealed = MutableLiveData(false)

    val questionCount: LiveData<Int> = Transformations.map(chapter) { it.quiz.size }

    val correctCount: LiveData<Int> = Transformations.map(quizAnswers) {
        it.count { entry -> correctQuizAnswers.value!![entry.key] == entry.value }
    }

    val correctQuizAnswers: LiveData<Map<Question, Int>> = Transformations.map(chapter) {
        it.quiz.associateWith { q -> q.answers.indexOfFirst { a -> a.correct }}
    }

    fun startQuiz() {
        val ch = chapter.value!!
        currentQuizQuestion.value = ch.quiz[0]
        currentQuizQuestionNumber.value = 0
        quizAnswers.value = ch.quiz.associateWith { -1 }
        quizState.value = QuizState.INPROGRESS
        answerRevealed.value = false
    }

    fun checkAnswer(answerIndex: Int): Int {
        val q = currentQuizQuestion.value!!
        quizAnswers.value = quizAnswers.value!!.plus(q to answerIndex)
        answerRevealed.value = true
        return correctQuizAnswers.value!![q] ?: error("Missing answer")
    }

    fun nextQuestion() {
        answerRevealed.value = false
        val num = currentQuizQuestionNumber.value!! + 1
        currentQuizQuestion.value = chapter.value!!.quiz[num]
        currentQuizQuestionNumber.value = num
    }

    fun revealAnswer() {
        answerRevealed.value = true
    }
}
