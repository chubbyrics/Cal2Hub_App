package com.example.cal2hubapp.util

import androidx.navigation.NavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.AuthRepository

fun NavController.navigateWithAuthGuard(
    destinationId: Int,
    authRepository: AuthRepository,
    context: android.content.Context
) {
    if (isProtectedDestination(destinationId) && !authRepository.isLoggedIn()) {
        navigate(R.id.splashFragment)
    } else {
        navigate(destinationId)
    }
}

private fun isProtectedDestination(destinationId: Int): Boolean {
    return destinationId in listOf(
        R.id.mainFragment,
        R.id.competenciesFragment,
        R.id.lectureFragment,
        R.id.quizFragment,
        R.id.quizResultFragment,
        R.id.masterExamFragment,
        R.id.congratsFragment,
        R.id.myAccountFragment,
        R.id.myPerformanceFragment
    )
}
