package pl.krzysztofwojciechowski.langcourses.ui.chapter.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_chapter_quiz_base.*
import kotlinx.android.synthetic.main.fragment_chapter_quiz_img.*
import kotlinx.android.synthetic.main.fragment_chapter_quiz_text.*
import pl.krzysztofwojciechowski.langcourses.Question
import pl.krzysztofwojciechowski.langcourses.QuestionType
import pl.krzysztofwojciechowski.langcourses.QuizState
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.ui.chapter.PageViewModel

class QuizFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private lateinit var root: View
    private lateinit var quizView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_chapter_quiz_base, container, false)
        pageViewModel.currentQuizQuestion.observeForever(this::showQuestion)
        pageViewModel.quizState.observeForever(this::setQuizState)
        pageViewModel.answerRevealed.observeForever(this::setAnswerRevealed)
        root.findViewById<View>(R.id.quiz_button).setOnClickListener(this::handleButton)

        return root
    }

    fun setQuizState(state: QuizState) {
        when (state) {
            QuizState.NOTSTARTED -> {
                root.findViewById<View>(R.id.quiz_notstarted).visibility = View.VISIBLE
                root.findViewById<View>(R.id.quiz_answer_holder).visibility = View.GONE
                root.findViewById<View>(R.id.quiz_finished).visibility = View.GONE
                root.findViewById<Button>(R.id.quiz_button).setText(R.string.quiz_start)
            }
            QuizState.INPROGRESS -> {
                quiz_notstarted.visibility = View.GONE
                quiz_answer_holder.visibility = View.VISIBLE
                quiz_finished.visibility = View.GONE
                quiz_button.setText(R.string.quiz_check)
            }
            QuizState.FINISHED -> {
                quiz_notstarted.visibility = View.GONE
                quiz_answer_holder.visibility = View.GONE
                quiz_finished.visibility = View.VISIBLE
                quiz_button.setText(R.string.quiz_try_again)
            }
        }
    }

    fun setAnswerRevealed(answerRevealed: Boolean) {
        if (pageViewModel.quizState.value!! != QuizState.INPROGRESS) {
            return
        }
        quiz_button.setText(if (answerRevealed) R.string.quiz_next else R.string.quiz_check)
    }

    fun handleButton(b: View) {
        when (pageViewModel.quizState.value!!) {
            QuizState.NOTSTARTED -> startQuiz()
            QuizState.INPROGRESS -> {
                if (pageViewModel.answerRevealed.value!!) {
                    pageViewModel.nextQuestion()
                } else {
                    pageViewModel.revealAnswer()
                }
            }
        }
    }

    fun startQuiz() {
        pageViewModel.startQuiz()
    }

    fun showQuestion(question: Question?) {
        if (question == null) {
            setQuizState(pageViewModel.quizState.value!!)
            return
        }
        quiz_question.text = question.question
        when (question.type) {
            QuestionType.CONVERSATION -> {
                quiz_text.visibility = View.VISIBLE
                quiz_img.visibility = View.GONE
                quizView = quiz_text
            }

            QuestionType.WORD -> {
                quiz_text.visibility = View.VISIBLE
                quiz_img.visibility = View.GONE
                quiz_image_top.setImageURI(question.imageUri)
                quizView = quiz_text
            }

            QuestionType.IMAGE -> {
                quiz_text.visibility = View.GONE
                quiz_img.visibility = View.VISIBLE
                quizView = quiz_img
            }
        }

        if (question.type == QuestionType.IMAGE) {
            quiz_img_answer1.setImageURI(question.answers[0].imageUri)
            quiz_img_answer2.setImageURI(question.answers[1].imageUri)
            quiz_img_answer3.setImageURI(question.answers[2].imageUri)
            quiz_img_answer4.setImageURI(question.answers[3].imageUri)
        } else {
            quizView.findViewById<RadioButton>(R.id.quiz_answer1).text = question.answers[0].text!!
            quizView.findViewById<RadioButton>(R.id.quiz_answer2).text = question.answers[1].text!!
            quizView.findViewById<RadioButton>(R.id.quiz_answer3).text = question.answers[2].text!!
            quizView.findViewById<RadioButton>(R.id.quiz_answer4).text = question.answers[3].text!!
        }
    }

        companion object {
            @JvmStatic
            fun newInstance(): QuizFragment {
                return QuizFragment()
            }
        }
    }