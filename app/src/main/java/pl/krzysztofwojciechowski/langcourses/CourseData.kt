package pl.krzysztofwojciechowski.langcourses

import android.net.Uri
import com.google.gson.annotations.SerializedName
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ManagedAsset
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ManagedCourseItem
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ManagedEntity
import pl.krzysztofwojciechowski.langcourses.resourcemanager.ResourceManager
import java.io.File

data class Course(
    val courseID: Int,
    val name: String,
    val language: String,
    val baselang: String,
    val level: String,
    val chapters: List<Chapter>
) : ManagedEntity() {
    override fun registerResourceManager(resourceManager: ResourceManager) {
        super.registerResourceManager(resourceManager)
        chapters.forEach { ch -> ch.registerResourceManager(resourceManager, this) }
    }

    fun getChapterByID(chapterID: Int): Chapter =
        chapters.find { it.chapterID == chapterID } ?: throw Exception("No such chapter")
}

data class Chapter(
    val chapterID: Int,
    val name: String,
    val translatedName: String,
    val mediaCredits: String?,
    val vocabulary: List<VocabularyBox>,
    val conversations: List<ConversationItem>,
    val quiz: List<Question>
) : ManagedCourseItem() {
    fun getVocabularyPlaylist(): List<File> =
        vocabularyListItems.flatMap { vli -> vli.entry.allAudioFiles }

    fun getConversationPlaylist(): List<File> =
        conversations.map { it.audioFile }

    private var _vocabularyListItems: List<VocabularyListItem>? = null

    val vocabularyListItems: List<VocabularyListItem>
        get() =
            if (_vocabularyListItems != null) _vocabularyListItems!!
            else {
                    _vocabularyListItems = buildVocabularyListItems(vocabulary)
                    _vocabularyListItems!!
                }

    override fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        super.registerResourceManager(resourceManager, course)
        vocabulary.forEach { vb -> vb.registerResourceManager(resourceManager, course) }
        conversations.forEach { ci -> ci.registerResourceManager(resourceManager, course) }
        quiz.forEach { q -> q.registerResourceManager(resourceManager, course) }
    }
}

abstract class VocabularyBase: ManagedCourseItem() {
    abstract val audioFile: File?
    abstract val translatedAudioFile: File?

    val allAudioFiles: List<File>
        get() = listOfNotNull(audioFile, translatedAudioFile)
}

data class VocabularyBox(
    val header: String,
    val translatedHeader: String,
    val words: List<VocabularyEntry>,
    val audio: String?,
    val translatedAudio: String?
) : VocabularyBase() {
    override fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        super.registerResourceManager(resourceManager, course)
        words.forEach { w -> w.registerResourceManager(resourceManager, course) }
    }

    override val audioFile: File?
        get() = getAssetFile(audio)

    override val translatedAudioFile: File?
        get() = getAssetFile(translatedAudio)
}

data class VocabularyEntry(
    val image: String?,
    val word: String,
    val translation: String,
    val definition: String?,
    val audio: String?,
    val translatedAudio: String?
) : VocabularyBase() {
    val imageUri: Uri?
        get() = getAssetUri(image)

    override val audioFile: File?
        get() = getAssetFile(audio)

    override val translatedAudioFile: File?
        get() = getAssetFile(translatedAudio)
}

enum class ConversationSide {
    @SerializedName("me") ME,
    @SerializedName("them") THEM
}

enum class QuestionType {
    @SerializedName("images") IMAGES,
    @SerializedName("describe") DESCRIBE,
    @SerializedName("text") TEXT
}

data class ConversationItem(
    val side: ConversationSide,
    val audio: String,
    val text: String,
    val translation: String
) : ManagedCourseItem() {
    val audioFile: File
        get() = resourceManager!!.getAsset(course!!, audio).getFile()
}

data class Question(
    val question: String,
    val type: QuestionType,
    val image: String?,
    val answers: List<QuizAnswer>
) : ManagedCourseItem() {
    override fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        super.registerResourceManager(resourceManager, course)
        answers.forEach { a -> a.registerResourceManager(resourceManager, course) }
    }

    val imageUri: Uri?
        get() = getAssetUri(image)
}

data class QuizAnswer(
    val text: String?,
    val image: String?,
    val correct: Boolean
) : ManagedCourseItem() {

    val imageUri: Uri?
        get() = getAssetUri(image)
}

enum class VocabularyListItemType(val num: Int) { TITLE(1), ENTRY(2) }
class VocabularyListItem(
    val type: VocabularyListItemType,
    val original: String,
    val translated: String,
    val entry: VocabularyBase
): ManagedCourseItem() {

    val audioFile: File?
        get() = entry.audioFile

    val translatedAudioFile: File?
        get() = entry.translatedAudioFile
}

fun buildVocabularyListItems(boxes: List<VocabularyBox>): List<VocabularyListItem> {
    val items = mutableListOf<VocabularyListItem>()
    boxes.forEach { box ->
        items.add(
            VocabularyListItem(
                VocabularyListItemType.TITLE,
                box.header,
                box.translatedHeader,
                box
            )
        )
        items.addAll(box.words.map {
            VocabularyListItem(
                VocabularyListItemType.ENTRY,
                it.word,
                it.translation,
                it
            )
        })
    }
    return items
}
