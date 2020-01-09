package pl.krzysztofwojciechowski.langcourses.resourcemanager

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import pl.krzysztofwojciechowski.langcourses.Course
import pl.krzysztofwojciechowski.langcourses.db.getCoursePath
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipFile

class StoredResourceManager(val context: Context) : ResourceManager() {

    override fun getCourseData(courseID: Int): Course {
        val coursePath = getCoursePath(
            courseID,
            context
        )
        val file = context.filesDir.resolve("coursedata/$coursePath/coursecontent.json")
        val course = Gson().fromJson(file.reader(), Course::class.java)
        course.registerResourceManager(this)
        return course
    }


    override fun getAsset(courseID: Int, path: String): ManagedAsset {
        val coursePath = getCoursePath(
            courseID,
            context
        )
        return StoredManagedAsset(
            context, "coursedata/$coursePath/assets/$path"
        )
    }

    override fun extractZipData(zipFile: File, coursePath: String) {
        val dir = File(context.filesDir, "coursedata/$coursePath")
        dir.deleteRecursively()
        val zip = ZipFile(zipFile)
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val name = entry.name.removePrefix("$coursePath/")
            if (name.isEmpty()) {
                continue
            }
            val newFile = dir.resolve(name)
            if (entry.isDirectory) {
                newFile.mkdirs()
            } else {
                newFile.parentFile.mkdirs()
                val inputStream = zip.getInputStream(entry)
                val outputStream = FileOutputStream(newFile)
                val buffer = ByteArray(4096)
                while (true) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1)
                        break
                    outputStream.write(buffer, 0, bytesRead)
                }
                inputStream.close()
                outputStream.close()
            }
        }
    }
}

class StoredManagedAsset(val context: Context, path: String) : ManagedAsset(path) {
    override fun getFile(): File {
        return context.filesDir.resolve(path)
    }

    override fun getInputStream(): FileInputStream {
        return context.openFileInput(path)
    }

    override fun getUri(): Uri {
        return Uri.fromFile(getFile())
    }
}