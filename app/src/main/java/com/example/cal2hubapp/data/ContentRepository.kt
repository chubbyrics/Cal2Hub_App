package com.example.cal2hubapp.data

import android.content.Context
import com.example.cal2hubapp.data.models.Chapter
import com.example.cal2hubapp.data.models.Competency
import com.example.cal2hubapp.data.models.Lecture
import com.example.cal2hubapp.data.models.QuizQuestion
import com.example.cal2hubapp.util.readAssetJson
import com.google.gson.Gson

class ContentRepository(private val context: Context) {
    private val gson = Gson()

    // ✅ Static chapters provided by client, updated with ALL chapters 1-9
    private val staticChapters = listOf(
        Chapter(
            id = "1",
            title = "Chapter 1: Basic Integration",
            description = "Covers the fundamental concepts and techniques of integration.",
            icon = "ic_chapter1"
        ),
        Chapter(
            id = "2",
            title = "Chapter 2: Integration by Substitution",
            description = "Focuses on solving integrals using the substitution method.",
            icon = "ic_chapter2"
        ),
        Chapter(
            id = "3",
            title = "Chapter 3: Integration by Partial Fractions",
            description = "Covers integration techniques using partial fraction decomposition for rational functions.",
            icon = "ic_chapter3"
        ),
        Chapter(
            id = "4",
            title = "Chapter 4: Integration by Parts",
            description = "Integration by Parts is a special method of integration that is often useful when two functions are multiplied together, but is also helpful in other ways.",
            icon = "ic_chapter4"
        ),
        Chapter(
            id = "5",
            title = "Chapter 5: Trigonometric Substitutions",
            description = "Sometimes it helps to replace a subexpression of a function by a single variable. Occasionally it can help to replace the original variable by something more complicated.",
            icon = "ic_chapter5"
        ),
        Chapter(
            id = "6",
            title = "Chapter 6: Improper Integrals",
            description = "Covers integrals with infinite limits of integration or integrands with infinite discontinuities.",
            icon = "ic_chapter6"
        ),
        Chapter(
            id = "7",
            title = "Chapter 7: Definite Integral",
            description = "A definite integral is the area under a curve between two fixed limits.",
            icon = "ic_chapter7"
        ),
        Chapter(
            id = "8",
            title = "Chapter 8: Applications of Integration",
            description = "Covers practical applications of integration including area, volume, and other real-world problems.",
            icon = "ic_chapter8"
        ),
        Chapter(
            id = "9",
            title = "Chapter 9: Double Integral",
            description = "Introduces double integrals and their applications in multivariable calculus.",
            icon = "ic_chapter9"
        )
    )

    private var competencies: List<Competency>? = null
    private var lectures: List<Lecture>? = null
    private var quizQuestions: List<QuizQuestion>? = null

    fun getChapters(): List<Chapter> = staticChapters

    fun getCompetencies(chapterId: String): List<Competency> {
        if (competencies == null) {
            competencies = context.readAssetJson("competencies.json", gson)
        }
        return competencies?.filter { it.chapterId == chapterId } ?: emptyList()
    }

    fun getLecture(lectureId: String): Lecture? {
        if (lectures == null) {
            lectures = context.readAssetJson("lectures.json", gson)
        }
        return lectures?.find { it.id == lectureId }
    }

    fun getQuizQuestions(chapterId: String): List<QuizQuestion> {
        if (quizQuestions == null) {
            quizQuestions = context.readAssetJson("quizzes.json", gson)
        }
        return quizQuestions?.filter { it.chapterId == chapterId } ?: emptyList()
    }

    fun getAllQuizQuestions(): List<QuizQuestion> {
        if (quizQuestions == null) {
            quizQuestions = context.readAssetJson("quizzes.json", gson)
        }
        return quizQuestions ?: emptyList()
    }

    fun getRandomQuizQuestions(count: Int): List<QuizQuestion> {
        val allQuestions = getAllQuizQuestions()
        return allQuestions.shuffled().take(count)
    }
}