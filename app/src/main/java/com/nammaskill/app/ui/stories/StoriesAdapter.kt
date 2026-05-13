package com.nammaskill.app.ui.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaskill.app.data.model.SuccessStory
import com.nammaskill.app.databinding.ItemStoryBinding

class StoriesAdapter : ListAdapter<SuccessStory, StoriesAdapter.StoryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.alpha = 0f
        holder.itemView.animate().alpha(1f).setDuration(400).setStartDelay(position * 80L).start()
    }

    inner class StoryViewHolder(private val b: ItemStoryBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(story: SuccessStory) {
            b.tvName.text = "${story.name}, ${story.age}"
            b.tvVillage.text = "📍 ${story.village}, ${story.district}"
            b.tvTrade.text = story.trade
            b.tvBefore.text = "Before: ${story.beforeStory}"
            b.tvAfter.text = "After: ${story.afterStory}"
            b.tvSalary.text = "₹${story.currentSalary}/month"
            b.tvQuote.text = "\"${story.quote}\""
            b.tvCenterName.text = story.centerName

            // Avatar initials
            val initials = story.name.split(" ").take(2).joinToString("") { it.first().uppercase() }
            b.tvAvatar.text = initials
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SuccessStory>() {
        override fun areItemsTheSame(old: SuccessStory, new: SuccessStory) = old.id == new.id
        override fun areContentsTheSame(old: SuccessStory, new: SuccessStory) = old == new
    }
}
