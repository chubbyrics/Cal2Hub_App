package com.example.cal2hubapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Competency(
    val id: String,
    val chapterId: String,
    val title: String,
    val description: String
) : Parcelable
