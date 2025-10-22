package com.example.cal2hubapp.ui.competencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cal2hubapp.data.models.Competency
import com.example.cal2hubapp.databinding.ItemCompetencyBinding

class CompetenciesAdapter(
    private val onLearningClick: (String) -> Unit,  // Takes competency ID for learning
    private val onQuizClick: (String) -> Unit       // Takes competency ID for quiz (optional)
) : ListAdapter<Competency, CompetenciesAdapter.CompetencyViewHolder>(CompetencyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetencyViewHolder {
        val binding = ItemCompetencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CompetencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompetencyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CompetencyViewHolder(
        private val binding: ItemCompetencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(competency: Competency) {
            binding.competencyTitleTextView.text = competency.title
            binding.competencyDescriptionTextView.text = competency.description

            // Learning button click
            binding.learningButton.setOnClickListener {
                val lectureId = mapCompetencyToLectureId(competency.id)
                onLearningClick(lectureId)
            }

            // Remove root click listener so nothing happens on item click
            binding.root.setOnClickListener(null)
        }

        private fun mapCompetencyToLectureId(competencyId: String): String {
            return when (competencyId) {
                "1.1", "1.2" -> "1"
                "2.1", "2.2" -> "2"
                "3.1", "3.2" -> "3"
                "4.1", "4.2" -> "4"
                "5.1", "5.2", "5.3", "5.4", "5.5", "5.6" -> "5"
                "6.1", "6.2" -> "6"
                "7.1", "7.2", "7.3", "7.4", "7.5", "7.6" -> "7"
                "8.1", "8.2", "8.3", "8.4", "8.5", "8.6", "8.7", "8.8", "8.9" -> "8"
                "9.1", "9.2", "9.3" -> "9"
                else -> competencyId.split(".")[0]
            }
        }
    }

    class CompetencyDiffCallback : DiffUtil.ItemCallback<Competency>() {
        override fun areItemsTheSame(oldItem: Competency, newItem: Competency): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Competency, newItem: Competency): Boolean {
            return oldItem == newItem
        }
    }
}
