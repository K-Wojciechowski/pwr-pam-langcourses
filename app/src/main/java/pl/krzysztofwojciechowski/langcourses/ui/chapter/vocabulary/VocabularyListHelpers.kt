package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import androidx.recyclerview.widget.GridLayoutManager
import pl.krzysztofwojciechowski.langcourses.VocabularyBox
import pl.krzysztofwojciechowski.langcourses.VocabularyEntry

enum class VocabularyListItemType(val num: Int) { TITLE(1), ENTRY(2) }
class VocabularyListItem(val type: VocabularyListItemType, val original: String, val translated: String, val entry: VocabularyEntry? = null)

fun getBestVocabularySpan(vocabularyListItems: List<VocabularyListItem>): Int {
    val maxLength = vocabularyListItems.filter { it.type == VocabularyListItemType.ENTRY }.map { kotlin.math.max(it.original.length, it.translated.length) }.max() ?: Int.MAX_VALUE

    return when {
        maxLength > 20 -> 1
        maxLength > 14 -> 2
        else -> 3
    };
}

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

class VocabularySpanSizeLookup(private val vocabularyListItems: List<VocabularyListItem>, private val span: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when (vocabularyListItems[position].type) {
            VocabularyListItemType.ENTRY -> 1
            VocabularyListItemType.TITLE -> span
        }
    }
}