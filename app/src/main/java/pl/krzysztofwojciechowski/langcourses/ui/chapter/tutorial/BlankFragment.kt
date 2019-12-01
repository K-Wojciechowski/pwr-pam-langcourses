package pl.krzysztofwojciechowski.langcourses.ui.chapter.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View(context)
        container?.addView(view)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(): BlankFragment {
            return BlankFragment()
        }
    }
}