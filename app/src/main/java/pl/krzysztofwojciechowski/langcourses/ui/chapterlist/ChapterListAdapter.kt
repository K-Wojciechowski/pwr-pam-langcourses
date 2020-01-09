package pl.krzysztofwojciechowski.langcourses.ui.chapterlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.db.ChapterProgress

class ChapterListAdapter(
    private val openChapter: (Chapter) -> Unit,
    private val items: List<Chapter> = mutableListOf(),
    private val progress: Map<Chapter, ChapterProgress>
) :
    RecyclerView.Adapter<ChapterListAdapter.ChapterViewHolder>() {
    class ChapterViewHolder(itemView: View, var context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.rv_course_chapter_name)
        val translated: TextView = itemView.findViewById(R.id.rv_course_chapter_translated)
        val number: TextView = itemView.findViewById(R.id.rv_course_chapter_number)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChapterViewHolder {
        return ChapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_course_chapters,
                parent,
                false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.translated.text = item.translatedName
        holder.number.text = (position + 1).toString()
        holder.number.setBackgroundResource(
            when (progress[item]) {
                ChapterProgress.NOT_STARTED -> R.drawable.rv_course_chapters_circle_notstarted
                ChapterProgress.IN_PROGRESS -> R.drawable.rv_course_chapters_circle_inprogress
                ChapterProgress.COMPLETE -> R.drawable.rv_course_chapters_circle_complete
                else -> R.drawable.rv_course_chapters_circle_notstarted
            }
        )

        holder.itemView.setOnClickListener {
            openChapter(item)
        }
    }

    override fun getItemCount() = items.size
}
