package com.example.cal2hubapp.ui.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.ContentRepository
import com.example.cal2hubapp.databinding.FragmentChaptersBinding

class ChaptersFragment : Fragment() {
    
    private var _binding: FragmentChaptersBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var contentRepository: ContentRepository
    private lateinit var chaptersAdapter: ChaptersAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        contentRepository = ContentRepository(requireContext())
        
        setupRecyclerView()
        loadChapters()
        
        binding.competenciesButton.setOnClickListener {
            // Navigate to first chapter's competencies
            val chapters = contentRepository.getChapters()
            if (chapters.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString("chapterId", chapters.first().id)
                }
                findNavController().navigate(R.id.competenciesFragment, bundle)
            }
        }
        
        binding.masterExamButton.setOnClickListener {
            findNavController().navigate(R.id.masterExamFragment)
        }
    }
    
    private fun setupRecyclerView() {
        chaptersAdapter = ChaptersAdapter { chapter ->
            val bundle = Bundle().apply {
                putString("chapterId", chapter.id)
            }
            findNavController().navigate(R.id.competenciesFragment, bundle)
        }
        
        binding.chaptersRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chaptersAdapter
        }
    }
    
    private fun loadChapters() {
        val chapters = contentRepository.getChapters()
        chaptersAdapter.submitList(chapters)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
