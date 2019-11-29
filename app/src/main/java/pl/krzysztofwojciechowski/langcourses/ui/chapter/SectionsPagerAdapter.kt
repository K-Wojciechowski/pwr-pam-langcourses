package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.ChapterTab

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager,
                           private val chapter: Chapter,
                           private val tabStrings: List<Int>, private val tabIDs: List<ChapterTab>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (tabIDs[position]) {
            ChapterTab.VOCABULARY -> VocabularyFragment.newInstance(chapter)
            ChapterTab.CONVERSATIONS -> ConversationFragment.newInstance(chapter)
            else -> PlaceholderFragment.newInstance(tabIDs[position].toString())
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabStrings[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return tabStrings.size
    }
}