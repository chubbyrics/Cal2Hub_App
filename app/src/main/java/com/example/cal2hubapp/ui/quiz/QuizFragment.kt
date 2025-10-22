package com.example.cal2hubapp.ui.quiz

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.data.ContentRepository
import com.example.cal2hubapp.data.ProgressRepository
import com.example.cal2hubapp.data.models.QuizQuestion
import com.example.cal2hubapp.data.models.QuizResult
import com.example.cal2hubapp.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private lateinit var contentRepository: ContentRepository
    private lateinit var progressRepository: ProgressRepository
    private lateinit var authRepository: AuthRepository

    private var chapterId: String? = null
    private var questions: List<QuizQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var score = 0
    private var userAnswers = mutableListOf<Int>()
    private var answeredQuestions = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentRepository = ContentRepository(requireContext())
        progressRepository = ProgressRepository(requireContext())
        authRepository = AuthRepository(requireContext())

        chapterId = arguments?.getString("chapterId")

        loadQuestions()
        setupClickListeners()
        displayCurrentQuestion()
    }

    private fun loadQuestions() {
        chapterId?.let { id ->
            questions = contentRepository.getQuizQuestions(id).take(20)
            userAnswers = MutableList(questions.size) { -1 }
        }
    }

    private fun setupClickListeners() {
        binding.choice1Button.setOnClickListener { selectAnswer(0) }
        binding.choice2Button.setOnClickListener { selectAnswer(1) }
        binding.choice3Button.setOnClickListener { selectAnswer(2) }
        binding.choice4Button.setOnClickListener { selectAnswer(3) }

        binding.nextButton.setOnClickListener { nextQuestion() }
        binding.submitButton.setOnClickListener { submitQuiz() }
    }

    private fun selectAnswer(selectedIndex: Int) {
        if (currentQuestionIndex >= questions.size) return
        val question = questions[currentQuestionIndex]

        if (!answeredQuestions.contains(currentQuestionIndex)) {
            userAnswers[currentQuestionIndex] = selectedIndex
            if (selectedIndex == question.correctAnswer) score++
            answeredQuestions.add(currentQuestionIndex)
        }

        showAnswerFeedback(selectedIndex, question.correctAnswer, question.feedback)
        updateScore()
        disableChoiceButtons()

        if (currentQuestionIndex == questions.size - 1) {
            binding.submitButton.visibility = View.VISIBLE
        } else {
            binding.nextButton.visibility = View.VISIBLE
        }
    }

    private fun showAnswerFeedback(selectedIndex: Int, correctIndex: Int, feedback: String) {
        val green = ContextCompat.getColor(requireContext(), android.R.color.holo_green_light)
        val red = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        val white = ContextCompat.getColor(requireContext(), android.R.color.white)
        val orange = ContextCompat.getColor(requireContext(), R.color.orange)

        resetChoiceButtons()

        getChoiceButton(correctIndex)?.apply {
            setBackgroundTintList(ColorStateList.valueOf(green))
            setTextColor(white)
        }

        if (selectedIndex != correctIndex) {
            getChoiceButton(selectedIndex)?.apply {
                setBackgroundTintList(ColorStateList.valueOf(red))
                setTextColor(white)
            }

            val correctAnswerText = questions[currentQuestionIndex].options[correctIndex]
            binding.feedbackTextView.text =
                "Wrong! The correct answer is: $correctAnswerText\n$feedback"
            binding.feedbackTextView.setTextColor(white)
        } else {
            binding.feedbackTextView.text = "Correct!\n$feedback"
            binding.feedbackTextView.setTextColor(white)
        }

        binding.feedbackTextView.visibility = View.VISIBLE
    }

    private fun getChoiceButton(index: Int) = when (index) {
        0 -> binding.choice1Button
        1 -> binding.choice2Button
        2 -> binding.choice3Button
        3 -> binding.choice4Button
        else -> null
    }

    private fun resetChoiceButtons() {
        val orange = ContextCompat.getColor(requireContext(), R.color.orange)
        val white = ContextCompat.getColor(requireContext(), android.R.color.white)

        listOf(binding.choice1Button, binding.choice2Button, binding.choice3Button, binding.choice4Button).forEach { button ->
            button.isEnabled = true
            button.setBackgroundTintList(ColorStateList.valueOf(orange))
            button.setTextColor(white)
        }
    }

    private fun disableChoiceButtons() {
        listOf(binding.choice1Button, binding.choice2Button, binding.choice3Button, binding.choice4Button)
            .forEach { it.isEnabled = false }
    }

    private fun enableChoiceButtons() {
        listOf(binding.choice1Button, binding.choice2Button, binding.choice3Button, binding.choice4Button)
            .forEach { it.isEnabled = true }
    }

    private fun updateScore() {
        binding.scoreTextView.text = "$score/${questions.size}"
    }

    private fun displayCurrentQuestion() {
        if (currentQuestionIndex >= questions.size) return

        val question = questions[currentQuestionIndex]

        binding.questionNumberTextView.text =
            "Question ${currentQuestionIndex + 1} of ${questions.size}"
        binding.questionTextView.text = question.question

        binding.choice1Button.text = question.options[0]
        binding.choice2Button.text = question.options[1]
        binding.choice3Button.text = question.options[2]
        binding.choice4Button.text = question.options[3]

        resetChoiceButtons()
        enableChoiceButtons()
        binding.feedbackTextView.visibility = View.GONE
        binding.nextButton.visibility = View.GONE
        binding.submitButton.visibility = View.GONE
    }

    private fun nextQuestion() {
        currentQuestionIndex++
        displayCurrentQuestion()
    }

    private fun submitQuiz() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null && chapterId != null) {
            val isPassed = score >= 14
            val result = QuizResult(
                userId = currentUser.username,
                chapterId = chapterId!!,
                score = score,
                totalQuestions = questions.size,
                timestamp = System.currentTimeMillis(),
                isPassed = isPassed
            )

            progressRepository.saveQuizResult(result)

            if (isPassed) {
                findNavController().navigate(R.id.congratsFragment)
            } else {
                val bundle = Bundle().apply {
                    putString("chapterId", chapterId)
                    putInt("score", score)
                    putInt("totalQuestions", questions.size)
                    putBoolean("isPassed", isPassed)
                }
                findNavController().navigate(R.id.quizResultFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
