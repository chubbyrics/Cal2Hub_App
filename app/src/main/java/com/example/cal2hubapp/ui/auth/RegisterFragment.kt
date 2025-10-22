package com.example.cal2hubapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authRepository = AuthRepository(requireContext())
        
        binding.registerButton.setOnClickListener {
            performRegister()
        }
        
        binding.loginLinkTextView.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
    
    private fun performRegister() {
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields")
            return
        }
        
        if (username.length < 3) {
            showError("Username must be at least 3 characters long")
            return
        }
        
        if (password.length < 6) {
            showError("Password must be at least 6 characters long")
            return
        }
        
        if (password != confirmPassword) {
            showError("Passwords do not match")
            return
        }
        
        if (authRepository.register(username, password)) {
            // Auto-login after successful registration
            if (authRepository.login(username, password)) {
                findNavController().navigate(R.id.mainFragment)
            }
        } else {
            showError("Username already exists")
        }
    }
    
    private fun showError(message: String) {
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
