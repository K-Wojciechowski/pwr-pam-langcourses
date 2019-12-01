package pl.krzysztofwojciechowski.langcourses.ui.chapter.conversation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.ConversationItem
import pl.krzysztofwojciechowski.langcourses.ConversationSide
import pl.krzysztofwojciechowski.langcourses.R

val SIDE_TO_INT = mapOf(ConversationSide.ME to 1, ConversationSide.THEM to 2)
val INT_TO_SIDE = mapOf(1 to ConversationSide.ME, 2 to ConversationSide.THEM)

class ConversationListAdapter(private val items: List<ConversationItem> = mutableListOf()) :
    RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder>() {

    constructor(chapter: Chapter): this(chapter.conversations)

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val original: TextView = itemView.findViewById(R.id.rv_chapter_conv_original)
        val translated: TextView = itemView.findViewById(R.id.rv_chapter_conv_translated)
    }

    override fun getItemViewType(position: Int): Int {
        return SIDE_TO_INT[items[position].side] ?: error("Unknown side")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationViewHolder {
        val layout = if (viewType == SIDE_TO_INT[ConversationSide.ME]) {
            R.layout.rv_chapter_conv_me
        } else {
            R.layout.rv_chapter_conv_them
        }
        return ConversationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layout, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val item = items[position]
        holder.original.text = item.text
        holder.translated.text = item.translation

//        holder.itemView.setOnClickListener {
//            openDefinition(item.entry!!)
//        }
    }

    override fun getItemCount() = items.size
}
