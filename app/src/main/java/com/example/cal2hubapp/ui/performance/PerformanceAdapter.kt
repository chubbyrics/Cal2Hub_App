package com.example.cal2hubapp.ui.performance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cal2hubapp.databinding.ItemPerformanceBinding

class PerformanceAdapter(
    private val onItemClick: (MyPerformanceFragment.PerformanceItem) -> Unit
) : ListAdapter<MyPerformanceFragment.PerformanceItem, PerformanceAdapter.PerformanceViewHolder>(PerformanceDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformanceViewHolder {
        val binding = ItemPerformanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PerformanceViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: PerformanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class PerformanceViewHolder(
        private val binding: ItemPerformanceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: MyPerformanceFragment.PerformanceItem) {
            binding.chapterTitleTextView.text = item.chapterTitle
            binding.scoreTextView.text = item.score
            binding.dateTextView.text = item.date
            binding.statusTextView.text = item.status
            
            // Set status color
            val statusColor = if (item.isPassed) {
                android.R.color.holo_green_dark
            } else {
                android.R.color.holo_red_dark
            }
            binding.statusTextView.setTextColor(
                binding.root.context.getColor(statusColor)
            )
            
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }
    
    class PerformanceDiffCallback : DiffUtil.ItemCallback<MyPerformanceFragment.PerformanceItem>() {
        override fun areItemsTheSame(
            oldItem: MyPerformanceFragment.PerformanceItem,
            newItem: MyPerformanceFragment.PerformanceItem
        ): Boolean {
            return oldItem.chapterTitle == newItem.chapterTitle && 
                   oldItem.date == newItem.date
        }
        
        override fun areContentsTheSame(
            oldItem: MyPerformanceFragment.PerformanceItem,
            newItem: MyPerformanceFragment.PerformanceItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
