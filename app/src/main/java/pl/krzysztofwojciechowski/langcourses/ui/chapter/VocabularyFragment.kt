package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.resourcemanager.getChapter

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
        val openIntent = Intent(context, DefinitionDialogActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(IE_DEF_IMAGEURI, entry.imageUri)
//        bundle.putString(IE_DEF_AUDIO, entry.audio)
        bundle.putString(IE_DEF_WORD, entry.word)
        bundle.putString(IE_DEF_TRANSLATION, entry.translation)
        bundle.putString(IE_DEF_DEFINITION, entry.definition)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }

    companion object {
        private const val ARG_COURSEID = "courseID"
        private const val ARG_CHAPTERID = "chapterID"

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