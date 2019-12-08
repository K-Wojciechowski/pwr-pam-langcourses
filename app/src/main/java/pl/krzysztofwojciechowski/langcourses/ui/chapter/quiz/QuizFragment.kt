package pl.krzysztofwojciechowski.langcourses.ui.chapter.quiz

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_chapter_quiz_base.*
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

    private lateinit var quizButton: Button
    private lateinit var answerRadios: List<RadioButton>
    private lateinit var answerRadioGroup: RadioGroup
    private lateinit var answerImages: List<ImageView>
    private lateinit var quizStateCopy: QuizState
    private lateinit var questionTypeCopy: QuestionType

    private var currentSelectedAnswer: MutableLiveData<Int> = MutableLiveData(-1)

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
        quizButton = root.findViewById(R.id.quiz_button)
        quizButton.setOnClickListener(this::handleButton)
        pageViewModel.currentQuizQuestion.observeForever(this::showQuestion)
        pageViewModel.quizState.observeForever(this::setQuizState)
        pageViewModel.answerRevealed.observeForever(this::setAnswerRevealed)
        pageViewModel.currentQuizQuestionNumber.observeForever(this::setQuestionNumber)
        pageViewModel.correctCount.observeForever(this::setCorrectCount)
        currentSelectedAnswer.observeForever(this::setEnableQuizButton)

        answerRadioGroup = root.findViewById(R.id.quiz_answer_rg)
        answerRadios = listOf(
            root.findViewById(R.id.quiz_answer1),
            root.findViewById(R.id.quiz_answer2),
            root.findViewById(R.id.quiz_answer3),
            root.findViewById(R.id.quiz_answer4)
        )

        answerImages = listOf(
            root.findViewById(R.id.quiz_img_answer1),
            root.findViewById(R.id.quiz_img_answer2),
            root.findViewById(R.id.quiz_img_answer3),
            root.findViewById(R.id.quiz_img_answer4)
        )

        val displayMetrics = context!!.resources.displayMetrics
        val quizImageSize = (displayMetrics.widthPixels / 2f - 48 * displayMetrics.density).toInt()

        answerImages.forEachIndexed { index, iv ->
            iv.layoutParams.height = quizImageSize
            iv.layoutParams.width = quizImageSize
            iv.setOnClickListener { imageAnswerClicked(index) }
        }

        answerRadios.forEachIndexed { index, rb -> rb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) currentSelectedAnswer.value = index
        } }

        return root
    }

    fun setEnableQuizButton(currentAnswerInt: Int) {
        quizButton.isEnabled = !(currentAnswerInt == -1 && quizStateCopy == QuizState.INPROGRESS)
    }

    fun imageAnswerClicked(currentAnswerInt: Int) {
        if (pageViewModel.answerRevealed.value!!) return
        currentSelectedAnswer.value = currentAnswerInt
        markImages()
    }

    fun markImages() {
        val currentAnswerInt = currentSelectedAnswer.value!!
        val answerRevealed = pageViewModel.answerRevealed.value!!
        val correctAnswer= pageViewModel.correctQuizAnswers[pageViewModel.currentQuizQuestion.value!!]!!
        answerImages.forEachIndexed { index, iv ->
            when {
                currentAnswerInt == -1 -> markImageUndecided(iv)
                answerRevealed && index == correctAnswer -> markImageCorrect(iv)
                answerRevealed && index == currentAnswerInt -> markImageIncorrect(iv)
                index == currentAnswerInt -> markImageSelected(iv)
                else -> markImageNotSelected(iv)
            }
        }
    }

    fun markImageUndecided(iv: ImageView) {
        iv.setColorFilter(null)
        iv.setImageAlpha(255)
        iv.background = null
    }

    // https://stackoverflow.com/questions/28308325/androidset-gray-scale-filter-to-imageview/28312202
    fun markImageNotSelected(iv: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) //0 means grayscale

        val cf = ColorMatrixColorFilter(matrix)
        iv.setColorFilter(cf)
        iv.setImageAlpha(128) // 128 = 0.5
        iv.background = null
    }

    fun markImageSelected(iv: ImageView) {
        markImageUndecided(iv)
        iv.setBackgroundColor(ContextCompat.getColor(context!!, R.color.img_selected_border))
    }

    fun markImageCorrect(iv: ImageView) {
        markImageUndecided(iv)
        iv.setBackgroundColor(ContextCompat.getColor(context!!, R.color.img_correct_border))
    }

    fun markImageIncorrect(iv: ImageView) {
        markImageNotSelected(iv)
        iv.setBackgroundColor(ContextCompat.getColor(context!!, R.color.img_incorrect_border))
    }

    fun setQuizState(state: QuizState) {
        quizStateCopy = state
        when (state) {
            QuizState.NOTSTARTED -> {
                root.findViewById<View>(R.id.quiz_notstarted).visibility = View.VISIBLE
                root.findViewById<View>(R.id.quiz_answer_holder).visibility = View.GONE
                root.findViewById<View>(R.id.quiz_finished).visibility = View.GONE
                root.findViewById<Button>(R.id.quiz_button).setText(R.string.quiz_start)
                root.findViewById<TextView>(R.id.question_number).setText(R.string.quiz_question_number_notinprogress)
                root.findViewById<TextView>(R.id.question_correct).setText(R.string.quiz_question_correct_notinprogress)
            }
            QuizState.INPROGRESS -> {
                quiz_notstarted.visibility = View.GONE
                quiz_answer_holder.visibility = View.VISIBLE
                quiz_finished.visibility = View.GONE
                setEnableQuizButton(-1)
                quiz_button.setText(R.string.quiz_check)
            }
            QuizState.FINISHED -> {
                quiz_notstarted.visibility = View.GONE
                quiz_answer_holder.visibility = View.GONE
                quiz_finished.visibility = View.VISIBLE
                quiz_button.setText(R.string.quiz_try_again)
                root.findViewById<TextView>(R.id.question_number).setText(R.string.quiz_question_number_notinprogress)
                root.findViewById<TextView>(R.id.question_correct).setText(R.string.quiz_question_correct_notinprogress)
            }
        }
    }

    fun setAnswerRevealed(answerRevealed: Boolean) {
        if (quizStateCopy != QuizState.INPROGRESS) {
            return
        }
        quiz_button.setText(if (answerRevealed) R.string.quiz_next else R.string.quiz_check)
        if (questionTypeCopy == QuestionType.DESCRIBE || questionTypeCopy == QuestionType.TEXT) {
            setRadioAnswerTextEnable(answerRevealed)
        } else {
            markImages()
        }
    }

    fun handleButton(b: View) {
        when (pageViewModel.quizState.value!!) {
            QuizState.NOTSTARTED -> startQuiz()
            QuizState.INPROGRESS -> {
                if (pageViewModel.answerRevealed.value!!) {
                    pageViewModel.nextQuestion()
                } else {
                    checkAnswer()
                }
            }
            QuizState.FINISHED -> startQuiz()
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
        currentSelectedAnswer.value = -1
        quiz_question.text = question.question
        questionTypeCopy = question.type
        when (question.type) {
            QuestionType.TEXT -> {
                quiz_text.visibility = View.VISIBLE
                quiz_img.visibility = View.GONE
                quizView = quiz_text
            }

            QuestionType.DESCRIBE -> {
                quiz_text.visibility = View.VISIBLE
                quiz_img.visibility = View.GONE
                quiz_image_top.setImageURI(question.imageUri)
                quizView = quiz_text
            }

            QuestionType.IMAGES -> {
                quiz_text.visibility = View.GONE
                quiz_img.visibility = View.VISIBLE
                quizView = quiz_img
            }
        }

        if (question.type == QuestionType.IMAGES) {
            setImageAnswer()
            markImages()
        } else {
            setRadioAnswerTextEnable(false)
        }
    }

    fun setImageAnswer() {
        val question = pageViewModel.currentQuizQuestion.value!!
        for (i in answerRadios.indices) {
            answerImages[i].setImageURI(question.answers[i].imageUri)
        }
    }

    fun setRadioAnswerTextEnable(reveal: Boolean) {
        val question = pageViewModel.currentQuizQuestion.value!!
        if (reveal) {
            answerRadioGroup.clearCheck()
            for (i in answerRadios.indices) {
                val baseText = question.answers[i].text!!
                answerRadios[i].isEnabled = false
                answerRadios[i].text =
                    when {
                        question.answers[i].correct -> getString(R.string.quiz_answer_correct_wrap, baseText)
                        currentSelectedAnswer.value!! == i -> getString(R.string.quiz_answer_incorrect_wrap, baseText)
                        else -> baseText
                    }
            }
        } else {
            for (i in answerRadios.indices) {
                answerRadios[i].isEnabled = true
                answerRadios[i].text = question.answers[i].text!!
            }
        }
    }

    fun checkAnswer() {
//            pageViewModel.checkAnswer(answerRadios.indexOfFirst { rb -> rb.isChecked })
        pageViewModel.checkAnswer(currentSelectedAnswer.value!!)
    }

    fun setQuestionNumber(n: Int) {
        root.findViewById<TextView>(R.id.question_number).text =
            getString(R.string.quiz_question_number, n + 1, pageViewModel.chapter.value!!.quiz.size)
    }

    fun setCorrectCount(n: Int) {
        root.findViewById<TextView>(R.id.question_correct).text = getString(R.string.quiz_correct_count, n)
    }

        companion object {
            @JvmStatic
            fun newInstance(): QuizFragment {
                return QuizFragment()
            }
        }
    }