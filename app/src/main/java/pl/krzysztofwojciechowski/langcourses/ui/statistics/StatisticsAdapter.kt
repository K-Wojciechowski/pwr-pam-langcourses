package pl.krzysztofwojciechowski.langcourses.ui.statistics

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.R

class StatisticsAdapter(
    private val showCourse: (Int, String) -> Unit
) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder>() {
    private val attempts: MutableList<StatisticsEntry> = mutableListOf()

    class StatisticsViewHolder(itemView: View, var context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.rv_statistics_card)
        val layout: ConstraintLayout = itemView.findViewById(R.id.rv_statistics_layout)
        val name: TextView = itemView.findViewById(R.id.rv_statistics_course_name_lbl)
        val completion: TextView = itemView.findViewById(R.id.rv_statistics_completion_lbl)
        val progress: ProgressBar = itemView.findViewById(R.id.rv_statistics_progress)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatisticsViewHolder {
        return StatisticsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_statistics, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val item = attempts[position]
        holder.name.text = item.courseName
        holder.completion.text = holder.context.getString(
            R.string.stats_percent_complete,
            item.completionPercent.toInt()
        )
        val finishedBgColor =
            ContextCompat.getColor(holder.context, R.color.stats_finished_background)
        if (item.isComplete) {
            holder.card.setCardBackgroundColor(finishedBgColor)
            holder.progress.visibility = View.GONE
        } else {
            holder.card.setCardBackgroundColor(Color.TRANSPARENT)
            holder.progress.visibility = View.VISIBLE
            holder.progress.progress = item.chapterCompleteCount
            holder.progress.max = item.chapterCount
        }
        holder.itemView.setOnClickListener {
            showCourse(item.courseID, item.coursePath)
        }
    }

    fun setList(entries: List<StatisticsEntry>) {
        attempts.clear()
        attempts.addAll(0, entries)
        notifyDataSetChanged()
    }

    override fun getItemCount() = attempts.size
}
