package com.example.cal2hubapp.ui.performance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.data.ContentRepository
import com.example.cal2hubapp.data.ProgressRepository
import com.example.cal2hubapp.data.models.QuizResult
import com.example.cal2hubapp.databinding.FragmentMyPerformanceBinding
import java.text.SimpleDateFormat
import java.util.*

class MyPerformanceFragment : Fragment() {
    
    private var _binding: FragmentMyPerformanceBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authRepository: AuthRepository
    private lateinit var contentRepository: ContentRepository
    private lateinit var progressRepository: ProgressRepository
    private lateinit var performanceAdapter: PerformanceAdapter
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPerformanceBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authRepository = AuthRepository(requireContext())
        contentRepository = ContentRepository(requireContext())
        progressRepository = ProgressRepository(requireContext())
        
        setupRecyclerView()
        loadPerformanceData()
    }
    
    private fun setupRecyclerView() {
        performanceAdapter = PerformanceAdapter { result ->
            // Handle item click if needed
        }
        
        binding.performanceRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = performanceAdapter
        }
    }
    
    private fun loadPerformanceData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            val results = progressRepository.getQuizResults(currentUser.username)
            val performanceItems = results.map { result ->
                PerformanceItem(
                    chapterTitle = getChapterTitle(result.chapterId),
                    score = "${result.score}/${result.totalQuestions}",
                    date = dateFormat.format(Date(result.timestamp)),
                    status = if (result.isPassed) "Passed" else "Failed",
                    isPassed = result.isPassed,
                    isMasterExam = result.isMasterExam
                )
            }.sortedByDescending { it.date }
            
            performanceAdapter.submitList(performanceItems)
        }
    }
    
    private fun getChapterTitle(chapterId: String): String {
        return if (chapterId == "master_exam") {
            "Master Exam"
        } else {
            val chapters = contentRepository.getChapters()
            chapters.find { it.id == chapterId }?.title ?: "Unknown Chapter"
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    data class PerformanceItem(
        val chapterTitle: String,
        val score: String,
        val date: String,
        val status: String,
        val isPassed: Boolean,
        val isMasterExam: Boolean
    )
}
