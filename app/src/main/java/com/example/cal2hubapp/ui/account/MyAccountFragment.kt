package com.example.cal2hubapp.ui.account

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.data.models.User
import com.example.cal2hubapp.databinding.FragmentMyAccountBinding
import com.example.cal2hubapp.util.SecurityUtils

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository(requireContext())
        currentUser = authRepository.getCurrentUser()

        loadUserData()
        setupClickListeners()

        // Center the error/success text
        binding.errorTextView.gravity = Gravity.CENTER
    }

    private fun loadUserData() {
        currentUser?.let { user ->
            binding.usernameEditText.setText(user.username)
            binding.emailEditText.setText(user.email ?: "")
            binding.mobileEditText.setText(user.mobile ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.updateButton.setOnClickListener {
            updateAccount()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun updateAccount() {
        val email = binding.emailEditText.text.toString().trim()
        val mobile = binding.mobileEditText.text.toString().trim()
        val currentPassword = binding.currentPasswordEditText.text.toString().trim()
        val newPassword = binding.newPasswordEditText.text.toString().trim()
        val confirmNewPassword = binding.confirmNewPasswordEditText.text.toString().trim()

        currentUser?.let { user ->
            // Check if password change is requested
            if (currentPassword.isNotEmpty() || newPassword.isNotEmpty() || confirmNewPassword.isNotEmpty()) {
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    showError("Please fill in all password fields", isError = true)
                    return
                }

                if (newPassword.length < 6) {
                    showError("New password must be at least 6 characters long", isError = true)
                    return
                }

                if (newPassword != confirmNewPassword) {
                    showError("New passwords do not match", isError = true)
                    return
                }

                // Verify current password
                val hashedCurrentPassword = SecurityUtils.hashPassword(currentPassword, user.salt)
                if (hashedCurrentPassword != user.passwordHash) {
                    showError("Current password is incorrect", isError = true)
                    return
                }

                // Update password
                val newSalt = SecurityUtils.generateSalt()
                val newPasswordHash = SecurityUtils.hashPassword(newPassword, newSalt)

                val updatedUser = user.copy(
                    email = email.ifEmpty { null },
                    mobile = mobile.ifEmpty { null },
                    passwordHash = newPasswordHash,
                    salt = newSalt
                )

                if (authRepository.updateUser(updatedUser)) {
                    showError("Account updated successfully", isError = false)
                    clearPasswordFields()
                } else {
                    showError("Failed to update account", isError = true)
                }
            } else {
                // Update only email and mobile
                val updatedUser = user.copy(
                    email = email.ifEmpty { null },
                    mobile = mobile.ifEmpty { null }
                )

                if (authRepository.updateUser(updatedUser)) {
                    showError("Account updated successfully", isError = false)
                } else {
                    showError("Failed to update account", isError = true)
                }
            }
        }
    }

    private fun logout() {
        authRepository.logout()
        findNavController().navigate(R.id.splashFragment)
    }

    private fun clearPasswordFields() {
        binding.currentPasswordEditText.setText("")
        binding.newPasswordEditText.setText("")
        binding.confirmNewPasswordEditText.setText("")
    }

    private fun showError(message: String, isError: Boolean) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE

        // Set color based on whether it's an error or success
        if (isError) {
            // Red color for errors
            binding.errorTextView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
        } else {
            // Green color for success
            binding.errorTextView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}