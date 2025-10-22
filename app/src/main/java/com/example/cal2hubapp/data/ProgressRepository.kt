package com.example.cal2hubapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cal2hubapp.data.models.QuizResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProgressRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("progress_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_QUIZ_RESULTS = "quiz_results"
    }
    
    fun saveQuizResult(result: QuizResult) {
        val results = getAllQuizResults().toMutableList()
        
        // Remove existing result for same user and chapter (keep only latest)
        results.removeAll { it.userId == result.userId && it.chapterId == result.chapterId && it.isMasterExam == result.isMasterExam }
        
        results.add(result)
        saveQuizResults(results)
    }
    
    fun getQuizResults(userId: String): List<QuizResult> {
        return getAllQuizResults().filter { it.userId == userId }
    }
    
    fun getQuizResultsForChapter(userId: String, chapterId: String): List<QuizResult> {
        return getQuizResults(userId).filter { it.chapterId == chapterId }
    }
    
    fun getBestScoreForChapter(userId: String, chapterId: String): Int {
        val chapterResults = getQuizResultsForChapter(userId, chapterId)
        return chapterResults.maxOfOrNull { it.score } ?: 0
    }
    
    fun getMasterExamResults(userId: String): List<QuizResult> {
        return getQuizResults(userId).filter { it.isMasterExam }
    }
    
    fun getBestMasterExamScore(userId: String): Int {
        val masterResults = getMasterExamResults(userId)
        return masterResults.maxOfOrNull { it.score } ?: 0
    }
    
    fun hasPassedChapter(userId: String, chapterId: String): Boolean {
        val chapterResults = getQuizResultsForChapter(userId, chapterId)
        return chapterResults.any { it.isPassed }
    }
    
    fun hasPassedMasterExam(userId: String): Boolean {
        val masterResults = getMasterExamResults(userId)
        return masterResults.any { it.isPassed }
    }
    
    private fun getAllQuizResults(): List<QuizResult> {
        val resultsJson = prefs.getString(KEY_QUIZ_RESULTS, "[]")
        val type = object : TypeToken<List<QuizResult>>() {}.type
        return gson.fromJson(resultsJson, type)
    }
    
    private fun saveQuizResults(results: List<QuizResult>) {
        val resultsJson = gson.toJson(results)
        prefs.edit().putString(KEY_QUIZ_RESULTS, resultsJson).apply()
    }
}
