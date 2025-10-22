package com.example.cal2hubapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizQuestion(
    val id: String,
    val chapterId: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val feedback: String
) : Parcelable

@Parcelize
data class QuizOption(
    val text: String,
    val isCorrect: Boolean
) : Parcelable
