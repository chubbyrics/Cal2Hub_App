package com.example.cal2hubapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.cal2hubapp.data.models.User
import com.example.cal2hubapp.util.SecurityUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_USERS = "users"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun register(username: String, password: String, email: String? = null, mobile: String? = null): Boolean {
        val users = getAllUsers().toMutableList()
        
        // Check if user already exists
        if (users.any { it.username == username }) {
            return false
        }
        
        val salt = SecurityUtils.generateSalt()
        val passwordHash = SecurityUtils.hashPassword(password, salt)
        
        val newUser = User(
            username = username,
            passwordHash = passwordHash,
            salt = salt,
            email = email,
            mobile = mobile
        )
        
        users.add(newUser)
        saveUsers(users)
        return true
    }
    
    fun login(username: String, password: String): Boolean {
        val users = getAllUsers()
        val user = users.find { it.username == username }
        
        if (user != null) {
            val hashedPassword = SecurityUtils.hashPassword(password, user.salt)
            if (hashedPassword == user.passwordHash) {
                setCurrentUser(user)
                setLoggedIn(true)
                return true
            }
        }
        return false
    }
    
    fun logout() {
        setCurrentUser(null)
        setLoggedIn(false)
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getCurrentUser(): User? {
        val userJson = prefs.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }
    
    fun updateUser(updatedUser: User): Boolean {
        val users = getAllUsers().toMutableList()
        val index = users.indexOfFirst { it.username == updatedUser.username }
        
        if (index != -1) {
            users[index] = updatedUser
            saveUsers(users)
            setCurrentUser(updatedUser)
            return true
        }
        return false
    }
    
    private fun getAllUsers(): List<User> {
        val usersJson = prefs.getString(KEY_USERS, "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(usersJson, type)
    }
    
    private fun saveUsers(users: List<User>) {
        val usersJson = gson.toJson(users)
        prefs.edit().putString(KEY_USERS, usersJson).apply()
    }
    
    private fun setCurrentUser(user: User?) {
        val userJson = if (user != null) gson.toJson(user) else null
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply()
    }
    
    private fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
}
