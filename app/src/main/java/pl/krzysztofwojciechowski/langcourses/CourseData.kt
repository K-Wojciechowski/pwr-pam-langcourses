package pl.krzysztofwojciechowski.langcourses

import com.google.gson.annotations.SerializedName

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
}

data class Chapter(
    val name: String,
    val translatedName: String,
    val mediaCredits: String?,
    val vocabulary: List<VocabularyBox>,
    val conversations: List<ConversationItem>,
    val quiz: List<Question>
) : ManagedCourseItem() {
    fun getVocabularyPlaylist(): List<ManagedAsset> =
        vocabulary.flatMap(VocabularyBox::getWordPlaylist)

    fun getConversationPlaylist(): List<ManagedAsset> =
        conversations.map { it.audio }.map { resourceManager!!.getAsset(course!!, it) }

    override fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        super.registerResourceManager(resourceManager, course)
        vocabulary.forEach { vb -> vb.registerResourceManager(resourceManager, course) }
        conversations.forEach { ci -> ci.registerResourceManager(resourceManager, course) }
        quiz.forEach { q -> q.registerResourceManager(resourceManager, course) }
    }
}

data class VocabularyBox(
    val header: String,
    val translatedHeader: String,
    val words: List<VocabularyEntry>
) : ManagedCourseItem() {
    override fun registerResourceManager(resourceManager: ResourceManager, course: Course) {
        super.registerResourceManager(resourceManager, course)
        words.forEach { w -> w.registerResourceManager(resourceManager, course) }
    }

    fun getWordPlaylist(): List<ManagedAsset> =
        words.mapNotNull { it.audio }.map { resourceManager!!.getAsset(course!!, it) }
}

data class VocabularyEntry(
    val image: String?,
    val audio: String?,
    val word: String,
    val translation: String,
    val definition: String?
) : ManagedCourseItem()

enum class ConversationSide {
    @SerializedName("me") ME,
    @SerializedName("them") THEM
}

enum class QuestionType {
    @SerializedName("image") IMAGE,
    @SerializedName("word") WORD,
    @SerializedName("conversation") CONVERSATION
}

data class ConversationItem(
    val side: ConversationSide,
    val audio: String,
    val text: String,
    val translation: String
) : ManagedCourseItem()

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
}

data class QuizAnswer(
    val text: String?,
    val image: String?,
    val correct: Boolean
) : ManagedCourseItem()