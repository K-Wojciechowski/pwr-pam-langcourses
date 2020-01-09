package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapter.PageViewModel

class VocabularyFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProvider(this).get(PageViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chapter_vocab, container, false)
        val chapter = pageViewModel.chapter.value!!
        val vocabularyListItems =
            buildVocabularyListItems(
                chapter.vocabulary
            )
        val span = getBestVocabularySpan(vocabularyListItems)
        val viewManager: RecyclerView.LayoutManager?
        if (span == 1) {
            viewManager = LinearLayoutManager(context)
        } else {
            viewManager = GridLayoutManager(context, span)
            viewManager.spanSizeLookup =
                VocabularySpanSizeLookup(
                    vocabularyListItems,
                    span
                )
        }
        val viewAdapter =
            VocabularyListAdapter(
                this::openDefinition,
                vocabularyListItems,
                span > 1
            )

        val recyclerView: RecyclerView = root.findViewById(R.id.chapter_rv_vocabulary)

        recyclerView.apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }


    private fun openDefinition(entry: VocabularyEntry) {
        (activity as ChapterActivity).saveInteraction()
        val audioFile = entry.audioFile
        if (audioFile != null) {
            (activity as ChapterActivity).startPlaying(audioFile)
        }
        val openIntent = Intent(context, DefinitionDialogActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(IE_DEF_IMAGEURI, entry.imageUri)
        bundle.putString(IE_DEF_WORD, entry.word)
        bundle.putString(IE_DEF_TRANSLATION, entry.translation)
        bundle.putString(IE_DEF_DEFINITION, entry.definition)
        openIntent.putExtras(bundle)
        startActivity(openIntent)
    }

    companion object {
        @JvmStatic
        fun newInstance(): VocabularyFragment {
            return VocabularyFragment()
        }
    }
}