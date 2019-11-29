package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.Chapter
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getChapter

class ConversationFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_chapter_conv, container, false)
        val viewManager = LinearLayoutManager(context)
        val viewAdapter = ConversationListAdapter(chapter)

        val recyclerView: RecyclerView = root.findViewById(R.id.chapter_rv_conversations)

        recyclerView.apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }

    companion object {
        private const val ARG_COURSEID = "courseID"
        private const val ARG_CHAPTERID = "courseID"

        @JvmStatic
        fun newInstance(chapter: Chapter): ConversationFragment {
            return ConversationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COURSEID, chapter.course!!.courseID)
                    putInt(ARG_CHAPTERID, chapter.chapterID)
                }
            }
        }
    }
}