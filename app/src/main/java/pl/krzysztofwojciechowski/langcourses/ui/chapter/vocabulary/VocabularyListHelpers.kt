package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import androidx.recyclerview.widget.GridLayoutManager
import pl.krzysztofwojciechowski.langcourses.VocabularyListItem
import pl.krzysztofwojciechowski.langcourses.VocabularyListItemType

fun getBestVocabularySpan(vocabularyListItems: List<VocabularyListItem>): Int {
    val maxLength = vocabularyListItems.filter { it.type == VocabularyListItemType.ENTRY }.map { kotlin.math.max(it.original.length, it.translated.length) }.max() ?: Int.MAX_VALUE

    return when {
        maxLength > 20 -> 1
        maxLength > 14 -> 2
        else -> 3
    }
}

class VocabularySpanSizeLookup(private val vocabularyListItems: List<VocabularyListItem>, private val span: Int) : GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when (vocabularyListItems[position].type) {
            VocabularyListItemType.ENTRY -> 1
            VocabularyListItemType.TITLE -> span
        }
    }
}