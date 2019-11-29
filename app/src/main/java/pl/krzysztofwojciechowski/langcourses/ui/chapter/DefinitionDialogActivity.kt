package pl.krzysztofwojciechowski.langcourses.ui.chapter

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.activity_definition_dialog.*
import pl.krzysztofwojciechowski.langcourses.*

class DefinitionDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_definition_dialog)
        def_image.setImageURI(intent.extras!!.getParcelable(IE_DEF_IMAGEURI) as Uri)
        def_title.text = getString(
            R.string.definition_word_translation,
            intent.extras!!.getString(IE_DEF_WORD),
            intent.extras!!.getString(IE_DEF_TRANSLATION)
        )
        def_text.text = intent.extras!!.getString(IE_DEF_DEFINITION)
    }
}
