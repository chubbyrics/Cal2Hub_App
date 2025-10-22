package com.example.cal2hubapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cal2hubapp.R
import com.example.cal2hubapp.data.AuthRepository
import com.example.cal2hubapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authRepository: AuthRepository
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        authRepository = AuthRepository(requireContext())
        
        // Check if user is already logged in
        if (authRepository.isLoggedIn()) {
            findNavController().navigate(R.id.mainFragment)
            return
        }
        
        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        
        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
