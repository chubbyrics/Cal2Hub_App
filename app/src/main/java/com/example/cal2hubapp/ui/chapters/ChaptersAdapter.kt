package com.example.cal2hubapp.ui.chapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cal2hubapp.data.models.Chapter
import com.example.cal2hubapp.databinding.ItemChapterBinding

class ChaptersAdapter(
    private val onChapterClick: (Chapter) -> Unit
) : ListAdapter<Chapter, ChaptersAdapter.ChapterViewHolder>(ChapterDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChapterViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ChapterViewHolder(
        private val binding: ItemChapterBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(chapter: Chapter) {
            binding.chapterTitleTextView.text = chapter.title
            binding.chapterDescriptionTextView.text = chapter.description
            
            binding.root.setOnClickListener {
                onChapterClick(chapter)
            }
        }
    }
    
    class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
            return oldItem == newItem
        }
    }
}
