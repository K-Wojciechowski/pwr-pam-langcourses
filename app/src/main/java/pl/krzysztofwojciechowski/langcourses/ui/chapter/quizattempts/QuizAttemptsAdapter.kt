package pl.krzysztofwojciechowski.langcourses.ui.chapter.quizattempts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.quizPassed

class QuizAttemptsAdapter(
    private val showChapter: (Int) -> Unit,
    private val isClickable: Boolean
) :
    RecyclerView.Adapter<QuizAttemptsAdapter.QuizAttemptViewHolder>() {
    private val attempts: MutableList<QuizAttemptWithName> = mutableListOf()

    class QuizAttemptViewHolder(itemView: View, var context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.rv_quizattempts_card)
        val name: TextView = itemView.findViewById(R.id.rv_quizattempts_chapter_name_lbl)
        val correct: TextView = itemView.findViewById(R.id.rv_quizattempts_correct_lbl)
        val date: TextView = itemView.findViewById(R.id.rv_quizattempts_date_lbl)
        val result: TextView = itemView.findViewById(R.id.rv_quizattempts_result_lbl)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuizAttemptViewHolder {
        return QuizAttemptViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_quizattempts, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: QuizAttemptViewHolder, position: Int) {
        val item = attempts[position]
        val isPassed = quizPassed(item.correct, item.total)
        holder.name.text = item.chapterName
        holder.correct.text =
            holder.context.getString(R.string.quizattempts_correct, item.percCorrect.toInt())
        holder.result.setText(if (isPassed) R.string.quizattempts_pass else R.string.quizattempts_fail)
        holder.date.text = item.attemptDate
        val bgColor = ContextCompat.getColor(
            holder.context,
            if (isPassed) R.color.quizattempts_pass_background else R.color.quizattempts_fail_background
        )
        holder.card.setCardBackgroundColor(bgColor)
        if (isClickable) {
            holder.itemView.setOnClickListener {
                showChapter(item.chapterID)
            }
        }
    }

    fun setList(entries: List<QuizAttemptWithName>) {
        attempts.clear()
        attempts.addAll(0, entries)
        notifyDataSetChanged()
    }

    override fun getItemCount() = attempts.size
}
