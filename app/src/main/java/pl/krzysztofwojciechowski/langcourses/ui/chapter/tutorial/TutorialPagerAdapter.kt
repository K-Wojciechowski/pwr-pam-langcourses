package pl.krzysztofwojciechowski.langcourses.ui.chapter.tutorial

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import pl.krzysztofwojciechowski.langcourses.ChapterTab
import pl.krzysztofwojciechowski.langcourses.ui.chapter.conversation.ConversationFragment
import pl.krzysztofwojciechowski.langcourses.ui.chapter.quiz.QuizFragment
import pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary.VocabularyFragment

class TutorialPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val tabStrings: List<Int>,
    private val tabIDs: List<ChapterTab>
) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (tabIDs[position]) {
            ChapterTab.VOCABULARY -> VocabularyFragment.newInstance()
            ChapterTab.CONVERSATIONS -> ConversationFragment.newInstance()
            ChapterTab.QUIZ -> QuizFragment.newInstance()
            ChapterTab.BLANK -> BlankFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(tabStrings[position])
    }

    override fun getCount(): Int {
        return tabStrings.size
    }
}