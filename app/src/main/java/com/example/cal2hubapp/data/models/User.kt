package com.example.cal2hubapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val passwordHash: String,
    val salt: String,
    val email: String? = null,
    val mobile: String? = null
) : Parcelable
