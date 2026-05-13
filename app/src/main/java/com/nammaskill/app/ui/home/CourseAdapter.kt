package com.nammaskill.app.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaskill.app.R
import com.nammaskill.app.data.model.Course
import com.nammaskill.app.databinding.ItemCourseBinding

class CourseAdapter(
    private val onApply: (Course) -> Unit,
    private val onInterested: (Course) -> Unit,
    private val onCardClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
        // Staggered animation
        holder.itemView.alpha = 0f
        holder.itemView.animate()
            .alpha(1f)
            .translationYBy(30f)
            .setDuration(300)
            .setStartDelay((position * 50L).coerceAtMost(500L))
            .start()
    }

    inner class CourseViewHolder(private val binding: ItemCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.title
            binding.tvCenterName.text = course.centerName
            binding.tvDistrict.text = "📍 ${course.district}"
            binding.tvStartDate.text = "🗓 ${course.startDate}"
            binding.tvSeatsLeft.text = "${course.seatsAvailable} seats left"
            binding.tvDuration.text = course.durationLabel

            // Stipend
            if (course.stipend > 0) {
                binding.tvStipend.visibility = View.VISIBLE
                binding.tvStipend.text = "₹${course.stipend}/month stipend"
            } else {
                binding.tvStipend.visibility = View.GONE
            }

            // Job Guaranteed chip
            if (course.jobGuaranteed) {
                binding.chipJobGuaranteed.visibility = View.VISIBLE
            } else {
                binding.chipJobGuaranteed.visibility = View.GONE
            }

            // Trade icon
            binding.ivTradeIcon.setImageResource(getTradeIcon(course.trade))

            // Duration badge color
            val badgeColor = when (course.durationType) {
                "short" -> R.color.success
                "long" -> R.color.secondary
                "advanced" -> R.color.primary
                else -> R.color.secondary
            }
            binding.tvDuration.setBackgroundColor(binding.root.context.getColor(badgeColor))

            binding.root.setOnClickListener { onCardClick(course) }
            binding.btnApplyNow.setOnClickListener { onApply(course) }
            binding.btnInterested.setOnClickListener { onInterested(course) }
        }

        private fun getTradeIcon(trade: String): Int = when (trade) {
            "Electrician" -> R.drawable.ic_electrician
            "Welding" -> R.drawable.ic_welding
            "Sewing" -> R.drawable.ic_sewing
            "Mobile Repair" -> R.drawable.ic_mobile_repair
            "Coding" -> R.drawable.ic_coding
            "Plumbing" -> R.drawable.ic_plumbing
            "Carpentry" -> R.drawable.ic_carpentry
            else -> R.drawable.ic_trade_default
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(old: Course, new: Course) = old.id == new.id
        override fun areContentsTheSame(old: Course, new: Course) = old == new
    }
}
