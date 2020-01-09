package pl.krzysztofwojciechowski.langcourses.ui.chapter.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.krzysztofwojciechowski.langcourses.ConversationItem
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.ui.chapter.ChapterActivity
import pl.krzysztofwojciechowski.langcourses.ui.chapter.PageViewModel

class ConversationFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_chapter_conv, container, false)
        val viewManager = LinearLayoutManager(context)
        val viewAdapter =
            ConversationListAdapter(
                this::playPhrase,
                pageViewModel.chapter.value!!
            )

        val recyclerView: RecyclerView = root.findViewById(R.id.chapter_rv_conversations)

        recyclerView.apply {
            setHasFixedSize(false)

            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }

    private fun playPhrase(item: ConversationItem) {
        (activity as ChapterActivity).saveInteraction()
        (activity as ChapterActivity).startPlaying(item.audioFile)
    }

    companion object {
        @JvmStatic
        fun newInstance(): ConversationFragment {
            return ConversationFragment()
        }
    }
}