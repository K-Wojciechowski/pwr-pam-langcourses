package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.VocabularyEntry
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getChapter
import pl.krzysztofwojciechowski.langcourses.ui.chapterlist.ChapterListAdapter

/**
 * A placeholder fragment containing a simple view.
 */
class VocabularyFragment : Fragment() {

    private lateinit var chapter: Chapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val courseID = arguments?.getInt(ARG_COURSEID)!!
        val chapterID = arguments?.getInt(ARG_CHAPTERID)!!

        chapter = getChapter(context!!, courseID, chapterID)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chapter_vocab, container, false)
        val viewManager = LinearLayoutManager(context)
        val viewAdapter =
            VocabularyListAdapter(
                this::openDefinition,
                chapter
            )

        val recyclerView: RecyclerView = root.findViewById(R.id.chapter_rv_vocabulary)

        recyclerView.apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }
    fun openDefinition(entry: VocabularyEntry) {
        throw NotImplementedError("Not implemented!")
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_COURSEID = "courseID"
        private const val ARG_CHAPTERID = "courseID"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(chapter: Chapter): VocabularyFragment {
            return VocabularyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COURSEID, chapter.course!!.courseID)
                    putInt(ARG_CHAPTERID, chapter.chapterID)
                }
            }
        }
    }
}