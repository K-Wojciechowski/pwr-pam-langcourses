package pl.krzysztofwojciechowski.langcourses.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pl.krzysztofwojciechowski.langcourses.CourseCardData
import pl.krzysztofwojciechowski.langcourses.R
import pl.krzysztofwojciechowski.langcourses.db.AvailableCourse

fun bytesToString(bytes: Double): String {
    var doubleBytes: Double = bytes
    if (doubleBytes < 1024.0) {
        return String.format("%.0f B", doubleBytes)
    }
    doubleBytes /= 1024
    if (doubleBytes < 1024.0) {
        return String.format("%.1f KB", doubleBytes)
    }

    doubleBytes /= 1024
    return String.format("%.1f MB", doubleBytes)
}

class CourseListAdapter(
    private val downloadCourse: (AvailableCourse, Boolean) -> Unit,
    private val openCourse: (AvailableCourse) -> Unit,
    private val continueCourse: (AvailableCourse) -> Unit
) :
    RecyclerView.Adapter<CourseListAdapter.CourseCardViewHolder>() {
    private val courses: MutableList<CourseCardData> = mutableListOf()

    class CourseCardViewHolder(itemView: View, var context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.rv_main_courses_card)
        val coverImage: ImageView = itemView.findViewById(R.id.rv_main_courses_cover_img)
        val title: TextView = itemView.findViewById(R.id.rv_main_courses_title_lbl)
        val version: TextView = itemView.findViewById(R.id.rv_main_courses_version_lbl)
        val progress: TextView = itemView.findViewById(R.id.rv_main_courses_progress_lbl)
        val downloadBtn: Button = itemView.findViewById(R.id.rv_main_courses_download_btn)
        val startContBtn: Button = itemView.findViewById(R.id.rv_main_courses_startcont_btn)
        val openBtn: Button = itemView.findViewById(R.id.rv_main_courses_open_btn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseCardViewHolder {
        return CourseCardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rv_courselist,
                parent,
                false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: CourseCardViewHolder, position: Int) {
        val item = courses[position]
        holder.title.text = item.course.name
        when {
            item.downloadedVersion == null -> {
                holder.version.text = holder.context.getString(
                    R.string.rv_main_courses_version_awaitdownload,
                    item.course.version,
                    bytesToString(item.course.fileSize)
                )
                holder.downloadBtn.visibility = View.VISIBLE
                holder.downloadBtn.setText(R.string.rv_main_courses_download)
            }
            item.downloadedVersion != item.course.version -> {
                holder.version.text = holder.context.getString(
                    R.string.rv_main_courses_version_outdated,
                    item.downloadedVersion,
                    item.course.version,
                    bytesToString(item.course.fileSize)
                )
                holder.downloadBtn.visibility = View.VISIBLE
                holder.downloadBtn.setText(R.string.rv_main_courses_update)
            }
            else -> {
                holder.version.text = holder.context.getString(
                    R.string.rv_main_courses_version,
                    item.downloadedVersion
                )
                holder.downloadBtn.visibility = View.GONE
            }
        }

        holder.startContBtn.setText(R.string.rv_main_courses_continue)
        holder.openBtn.visibility = View.VISIBLE

        var buttonIsStart = false

        if (!item.inProgress) {
            holder.progress.setText(R.string.rv_main_courses_progress_zero)
            holder.startContBtn.setText(R.string.rv_main_courses_start)
            buttonIsStart = true
            holder.openBtn.visibility = View.GONE
        } else if (item.currentProgress == 1.0) {
            holder.progress.setText(R.string.rv_main_courses_progress_complete)
        } else {
            holder.progress.text =
                holder.context.getString(
                    R.string.rv_main_courses_progress_partial,
                    item.currentProgress * 100
                )
        }

        Glide.with(holder.context).load(item.course.coverImage).into(holder.coverImage)

        holder.card.setCardBackgroundColor(item.course.coverBgColorA)
        holder.title.setTextColor(item.course.coverTextColorA)
        holder.version.setTextColor(item.course.coverTextColorA)
        holder.progress.setTextColor(item.course.coverTextColorA)
        holder.downloadBtn.setTextColor(item.course.coverTextColorA)
        holder.startContBtn.setTextColor(item.course.coverTextColorA)
        holder.openBtn.setTextColor(item.course.coverTextColorA)
        holder.itemView.setOnClickListener {
            openCourse(item.course)
        }

        holder.downloadBtn.setOnClickListener {
            downloadCourse(item.course, false)
        }

        holder.startContBtn.setOnClickListener {
            if (item.downloadedVersion == null) {
                downloadCourse(item.course, true)
            } else if (buttonIsStart) {
                openCourse(item.course)
            } else {
                continueCourse(item.course)
            }
        }

        holder.openBtn.setOnClickListener {
            openCourse(item.course)
        }
    }

    fun setList(entries: List<CourseCardData>) {
        courses.clear()
        courses.addAll(0, entries)
        notifyDataSetChanged()
    }

    override fun getItemCount() = courses.size
}
