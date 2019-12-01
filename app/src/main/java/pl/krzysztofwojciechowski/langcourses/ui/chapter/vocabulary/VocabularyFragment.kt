package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.*
import pl.krzysztofwojciechowski.langcourses.ui.chapter.PageViewModel

class VocabularyFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = activity?.run {
            ViewModelProviders.of(this).get(PageViewModel::class.java)
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
        val viewManager = GridLayoutManager(context, VOC_GRID_SPAN)
        viewManager.spanSizeLookup =
            VocabularySpanSizeLookup(
                vocabularyListItems
            )
        val viewAdapter =
            VocabularyListAdapter(
                this::openDefinition,
                vocabularyListItems
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
        @JvmStatic
        fun newInstance(): VocabularyFragment {
            return VocabularyFragment()
        }
    }
}