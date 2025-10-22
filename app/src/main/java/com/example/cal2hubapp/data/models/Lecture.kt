package com.example.cal2hubapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lecture(
    val id: String,
    val competencyId: String,
    val title: String,
    val content: String,
    val examples: List<String> = emptyList()
) : Parcelable
