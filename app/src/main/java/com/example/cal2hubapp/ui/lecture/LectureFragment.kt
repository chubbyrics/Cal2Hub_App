package com.example.cal2hubapp.ui.lecture

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.ContentRepository
import com.example.cal2hubapp.databinding.FragmentLectureBinding

class LectureFragment : Fragment() {

    private var _binding: FragmentLectureBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentRepository: ContentRepository
    private var lectureId: String? = null
    private var isShowingFocusing = true // Track current image state

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLectureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentRepository = ContentRepository(requireContext())
        lectureId = arguments?.getString("lectureId")

        // Set up image transparency
        binding.imageView.setBackgroundColor(Color.TRANSPARENT)

        // Set up click listener to switch images
        binding.imageView.setOnClickListener {
            switchImage()
        }

        loadLecture()
    }

    private fun loadLecture() {
        val id = lectureId
        if (id.isNullOrEmpty()) {
            binding.lectureTitleTextView.text = "Lecture ID missing"
            binding.lectureContentTextView.text = ""
            binding.examplesTitleTextView.visibility = View.GONE
            binding.examplesContainer.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            return
        }

        val lecture = contentRepository.getLecture(id)

        if (lecture == null) {
            binding.lectureTitleTextView.text = "Lecture not found"
            binding.lectureContentTextView.text = ""
            binding.examplesTitleTextView.visibility = View.GONE
            binding.examplesContainer.visibility = View.GONE
            binding.imageView.visibility = View.GONE
            return
        }

        // Set title and content safely
        binding.lectureTitleTextView.text = lecture.title ?: "No title"
        binding.lectureContentTextView.text = lecture.content ?: "No content"

        // Set alternating image based on lecture ID
        setAlternatingImage()

        // Handle examples safely
        val examples = lecture.examples ?: emptyList()
        if (examples.isNotEmpty()) {
            binding.examplesTitleTextView.visibility = View.VISIBLE
            binding.examplesContainer.visibility = View.VISIBLE

            // Clear existing examples
            binding.examplesContainer.removeAllViews()

            // Add examples
            examples.forEachIndexed { index, example ->
                val exampleView = android.widget.TextView(requireContext()).apply {
                    text = "${index + 1}. $example"
                    textSize = 16f
                    setPadding(0, 8, 0, 8)
                }
                binding.examplesContainer.addView(exampleView)
            }
        } else {
            binding.examplesTitleTextView.visibility = View.GONE
            binding.examplesContainer.visibility = View.GONE
        }
    }

    private fun setAlternatingImage() {
        // Make image visible
        binding.imageView.visibility = View.VISIBLE

        // Simple alternating logic based on lecture ID
        val lectureId = this.lectureId ?: ""

        // Use the lecture ID to decide which image to show
        // This will consistently show the same image for the same lecture ID
        val useFocusing = lectureId.hashCode() % 2 == 0

        if (useFocusing) {
            binding.imageView.setImageResource(R.drawable.brainfocusing)
            isShowingFocusing = true
        } else {
            binding.imageView.setImageResource(R.drawable.brainwondering)
            isShowingFocusing = false
        }

        // Ensure transparency is maintained
        binding.imageView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun switchImage() {
        // Toggle between the two images
        if (isShowingFocusing) {
            binding.imageView.setImageResource(R.drawable.brainwondering)
            isShowingFocusing = false
        } else {
            binding.imageView.setImageResource(R.drawable.brainfocusing)
            isShowingFocusing = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}