package com.nammaskill.app.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaskill.app.R
import com.nammaskill.app.data.model.Application
import com.nammaskill.app.databinding.ItemApplicationBinding

class ApplicationAdapter : ListAdapter<Application, ApplicationAdapter.AppViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemApplicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppViewHolder(private val b: ItemApplicationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(app: Application) {
            b.tvCourseName.text = app.courseName
            b.tvAppliedDate.text = "Applied: ${app.appliedDate}"
            b.tvStatus.text = app.status

            val ctx = b.root.context
            val (bgColor, textColor) = when (app.status) {
                "Submitted" -> Pair(R.color.status_submitted_bg, R.color.status_submitted_text)
                "Under Review" -> Pair(R.color.status_review_bg, R.color.status_review_text)
                "Approved" -> Pair(R.color.status_approved_bg, R.color.status_approved_text)
                "Batch Started" -> Pair(R.color.status_started_bg, R.color.status_started_text)
                else -> Pair(R.color.status_submitted_bg, R.color.status_submitted_text)
            }
            b.tvStatus.backgroundTintList = ContextCompat.getColorStateList(ctx, bgColor)
            b.tvStatus.setTextColor(ContextCompat.getColor(ctx, textColor))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Application>() {
        override fun areItemsTheSame(old: Application, new: Application) = old.id == new.id
        override fun areContentsTheSame(old: Application, new: Application) = old == new
    }
}
