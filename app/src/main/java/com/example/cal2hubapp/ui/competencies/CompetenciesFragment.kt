package com.example.cal2hubapp.ui.competencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.ContentRepository
import com.example.cal2hubapp.databinding.FragmentCompetenciesBinding

class CompetenciesFragment : Fragment() {

    private var _binding: FragmentCompetenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentRepository: ContentRepository
    private lateinit var competenciesAdapter: CompetenciesAdapter
    private var chapterId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompetenciesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentRepository = ContentRepository(requireContext())
        chapterId = arguments?.getString("chapterId")

        setupRecyclerView()
        loadCompetencies()

        // Set up the chapter quiz button
        binding.quizButton.setOnClickListener {
            chapterId?.let { id ->
                val bundle = Bundle().apply {
                    putString("chapterId", id)
                }
                try {
                    findNavController().navigate(R.id.quizFragment, bundle)
                } catch (e: Exception) {
                    Log.e("CompetenciesFragment", "Chapter quiz navigation failed: ${e.message}")
                }
            }
        }
    }

    private fun setupRecyclerView() {
        competenciesAdapter = CompetenciesAdapter(
            onLearningClick = { lectureId ->
                // Navigate to LectureFragment with the lecture ID
                Log.d("CompetenciesFragment", "Navigating to lecture: $lectureId")
                val bundle = Bundle().apply {
                    putString("lectureId", lectureId)
                }
                try {
                    findNavController().navigate(R.id.lectureFragment, bundle)
                } catch (e: Exception) {
                    Log.e("CompetenciesFragment", "Lecture navigation failed: ${e.message}")
                }
            },
            onQuizClick = { competencyId ->
                // Navigate to QuizFragment with the competency ID
                Log.d("CompetenciesFragment", "Navigating to quiz for competency: $competencyId")
                val bundle = Bundle().apply {
                    putString("competencyId", competencyId)
                }
                try {
                    findNavController().navigate(R.id.quizFragment, bundle)
                } catch (e: Exception) {
                    Log.e("CompetenciesFragment", "Competency quiz navigation failed: ${e.message}")
                }
            }
        )

        binding.competenciesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = competenciesAdapter
        }
    }

    private fun loadCompetencies() {
        chapterId?.let { id ->
            Log.d("CompetenciesFragment", "Loading competencies for chapter: $id")
            val competencies = contentRepository.getCompetencies(id)
            Log.d("CompetenciesFragment", "Found ${competencies.size} competencies")
            competenciesAdapter.submitList(competencies)

            // Update chapter title
            val chapters = contentRepository.getChapters()
            val chapter = chapters.find { it.id == id }
            chapter?.let {
                binding.chapterTitleTextView.text = it.title
                Log.d("CompetenciesFragment", "Set chapter title: ${it.title}")
            } ?: run {
                Log.e("CompetenciesFragment", "Chapter not found for id: $id")
                binding.chapterTitleTextView.text = "Chapter $id"
            }
        } ?: run {
            Log.e("CompetenciesFragment", "Chapter ID is null")
            binding.chapterTitleTextView.text = "Unknown Chapter"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}