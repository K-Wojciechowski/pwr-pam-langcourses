package pl.krzysztofwojciechowski.langcourses.ui.chapter.vocabulary

import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
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
        val displayMetrics = applicationContext.resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        var propWidth = 0.8 * width
        var propHeight =
            0.8 * width * def_image.drawable.intrinsicHeight / def_image.drawable.intrinsicWidth
        if (propHeight > 0.8 * height) {
            propHeight = 0.8 * height
            propWidth =
                0.8 * height * def_image.drawable.intrinsicWidth / def_image.drawable.intrinsicHeight
        }
        def_image.layoutParams.height = propHeight.toInt()
        def_image.layoutParams.width = propWidth.toInt()
    }
}
