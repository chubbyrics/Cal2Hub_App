package com.example.cal2hubapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizResult(
    val userId: String,
    val chapterId: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long,
    val isPassed: Boolean,
    val isMasterExam: Boolean = false
) : Parcelable
