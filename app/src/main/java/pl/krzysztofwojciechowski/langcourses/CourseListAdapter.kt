package pl.krzysztofwojciechowski.langcourses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

fun bytesToString(bytes: Int): String {
    var doubleBytes: Double = bytes.toDouble()
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

class CourseListAdapter(private val openCourse: (CourseCardData) -> Unit, private val courses: MutableList<CourseCardData> = mutableListOf()) :
    RecyclerView.Adapter<CourseListAdapter.CourseCardViewHolder>() {
    class CourseCardViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.rv_main_courses_card)
        val coverImage: ImageView = itemView.findViewById(R.id.rv_main_courses_cover_img)
        val title: TextView = itemView.findViewById(R.id.rv_main_courses_title_lbl)
        val version: TextView = itemView.findViewById(R.id.rv_main_courses_version_lbl)
        val progress: TextView = itemView.findViewById(R.id.rv_main_courses_progress_lbl)
        val startContBtn: TextView = itemView.findViewById(R.id.rv_main_courses_startcont_btn)
        val openBtn: TextView = itemView.findViewById(R.id.rv_main_courses_open_btn)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseCardViewHolder {
        return CourseCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_courselist, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: CourseCardViewHolder, position: Int) {
        val item = courses[position]
        holder.title.text = item.name
        if (item.downloadedVersion == null) {
            holder.version.text = holder.context.getString(
                R.string.rv_main_courses_version_awaitdownload,
                item.latestVersion,
                bytesToString(item.fileSize)
            )
        } else if (item.downloadedVersion != item.latestVersion) {
            holder.version.text = holder.context.getString(
                R.string.rv_main_courses_version_outdated,
                item.downloadedVersion,
                item.latestVersion,
                bytesToString(item.fileSize)
            )
        } else {
            holder.version.text = holder.context.getString(
                R.string.rv_main_courses_version,
                item.downloadedVersion
            )
        }

        holder.startContBtn.setText(R.string.rv_main_courses_continue)
        holder.openBtn.visibility = View.VISIBLE

        if (item.currentProgress == null || item.currentProgress == 0.0) {
            holder.progress.setText(R.string.rv_main_courses_progress_zero)
            holder.startContBtn.setText(R.string.rv_main_courses_start)
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

        holder.coverImage.setImageDrawable(item.coverImage)

        holder.card.setCardBackgroundColor(item.coverBgColor)
        holder.title.setTextColor(item.coverFgColor)
        holder.version.setTextColor(item.coverFgColor)
        holder.progress.setTextColor(item.coverFgColor)
        holder.startContBtn.setTextColor(item.coverFgColor)
        holder.openBtn.setTextColor(item.coverFgColor)
        holder.itemView.setOnClickListener {
            openCourse(item)
        }
    }

    fun setList(entries: List<CourseCardData>) {
        courses.clear()
        courses.addAll(0, entries)
        notifyDataSetChanged()
    }

    fun updateItem(entry: CourseCardData) {
        val position = courses.indexOf(entry)
        if (position == -1) {
            Log.wtf("CourseCardAdapter", "Update item received but item not found, updating all")
            notifyItemRangeChanged(0, courses.size)
        } else {
            notifyItemChanged(position)
        }
    }


    override fun getItemCount() = courses.size
}
