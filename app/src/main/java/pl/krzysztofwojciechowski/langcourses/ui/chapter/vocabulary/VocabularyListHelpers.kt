package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import androidx.recyclerview.widget.GridLayoutManager
import pl.krzysztofwojciechowski.langcourses.VOC_GRID_SPAN
import pl.krzysztofwojciechowski.langcourses.VOC_GRID_SPAN_ENTRY
import pl.krzysztofwojciechowski.langcourses.VocabularyBox
import pl.krzysztofwojciechowski.langcourses.VocabularyEntry

enum class VocabularyListItemType(val num: Int) { TITLE(1), ENTRY(2) }
class VocabularyListItem(val type: VocabularyListItemType, val original: String, val translated: String, val entry: VocabularyEntry? = null)

fun buildVocabularyListItems(boxes: List<VocabularyBox>): List<VocabularyListItem> {
    val items = mutableListOf<VocabularyListItem>()
    boxes.forEach { box ->
        items.add(
            VocabularyListItem(
                VocabularyListItemType.TITLE,
                box.header,
                box.translatedHeader
            )
        )
        items.addAll(box.words.map {
            VocabularyListItem(
                VocabularyListItemType.ENTRY,
                it.word,
                it.translation,
                it
            )
        })
    }
    return items
}

class VocabularySpanSizeLookup(private val vocabularyListItems: List<VocabularyListItem>) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when (vocabularyListItems[position].type) {
            VocabularyListItemType.ENTRY -> VOC_GRID_SPAN_ENTRY
            VocabularyListItemType.TITLE -> VOC_GRID_SPAN
        }
    }
}