package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.VocabularyEntry

class VocabularyListAdapter(private val openDefinition: (VocabularyEntry) -> Unit, private val items: List<VocabularyListItem> = mutableListOf(), private val isGridMode: Boolean) :
    RecyclerView.Adapter<VocabularyListAdapter.VocabularyViewHolder>() {

    open class VocabularyViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView)

    class VocabularyTitleViewHolder(itemView: View, context: Context) : VocabularyViewHolder(itemView, context) {
        val original: TextView = itemView.findViewById(R.id.rv_chapter_vocab_title_original)
        val translated: TextView = itemView.findViewById(R.id.rv_chapter_vocab_title_translated)
    }

    class VocabularyEntryViewHolder(itemView: View, context: Context) : VocabularyViewHolder(itemView, context) {
        val original: TextView = itemView.findViewById(R.id.rv_chapter_vocab_original)
        val translated: TextView = itemView.findViewById(R.id.rv_chapter_vocab_translated)
        val image: ImageView = itemView.findViewById(R.id.rv_chapter_vocab_image)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type.num
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VocabularyViewHolder {
        if (viewType == VocabularyListItemType.TITLE.num) {
            return VocabularyTitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.rv_chapter_vocab_title,
                    parent,
                    false
                ),
                parent.context
            )
        } else {

            return VocabularyEntryViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    if (isGridMode) R.layout.rv_chapter_vocab_entry_grid
                    else R.layout.rv_chapter_vocab_entry_line,
                    parent,
                    false
                ),
                parent.context
            )
        }
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val item = items[position]
        if (item.type == VocabularyListItemType.TITLE) {
            val titleHolder = holder as VocabularyTitleViewHolder
            titleHolder.original.text = item.original
            titleHolder.translated.text = item.translated
        } else {
            val entryHolder = holder as VocabularyEntryViewHolder
            entryHolder.original.text = item.original
            entryHolder.translated.text = item.translated
            entryHolder.image.setImageURI(item.entry!!.imageUri)

            holder.itemView.setOnClickListener {
                openDefinition(item.entry)
            }
        }
    }

    override fun getItemCount() = items.size
}
