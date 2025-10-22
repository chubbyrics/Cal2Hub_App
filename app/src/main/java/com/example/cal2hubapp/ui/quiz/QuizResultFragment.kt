package com.example.cal2hubapp.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.databinding.FragmentQuizResultBinding

class QuizResultFragment : Fragment() {
    
    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    
    private var chapterId: String? = null
    private var score: Int = 0
    private var totalQuestions: Int = 0
    private var isPassed: Boolean = false
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get arguments
        chapterId = arguments?.getString("chapterId")
        score = arguments?.getInt("score") ?: 0
        totalQuestions = arguments?.getInt("totalQuestions") ?: 0
        isPassed = arguments?.getBoolean("isPassed") ?: false
        
        displayResults()
        setupClickListeners()
    }
    
    private fun displayResults() {
        binding.scoreTextView.text = "$score/$totalQuestions"
        binding.resultTextView.text = if (isPassed) "Passed!" else "Failed"
        binding.resultTextView.setTextColor(
            if (isPassed) 
                resources.getColor(android.R.color.holo_green_dark, null)
            else 
                resources.getColor(android.R.color.holo_red_dark, null)
        )
        
        // Show retake button if failed
        if (!isPassed) {
            binding.retakeButton.visibility = View.VISIBLE
        }
    }
    
    private fun setupClickListeners() {
        binding.retakeButton.setOnClickListener {
            chapterId?.let { id ->
                val bundle = Bundle().apply {
                    putString("chapterId", id)
                }
                findNavController().navigate(R.id.quizFragment, bundle)
            }
        }
        
        binding.reviewButton.setOnClickListener {
            // TODO: Implement review functionality
            // For now, just go back to chapters
            findNavController().navigate(R.id.mainFragment)
        }
        
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
