package com.example.memoriesrecorder

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoriesrecorder.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.register.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            if (validateForm(email, password, confirmPassword)) {
                registerUser(email, username, password)
            }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun validateForm(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            binding.email.error = "Email is required"
            binding.email.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = "Please enter a valid email"
            binding.email.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.password.error = "Password is required"
            binding.password.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.password.error = "Password must be at least 6 characters"
            binding.password.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPassword.error = "Passwords do not match"
            binding.confirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun registerUser(email: String, username: String, password: String) {
        FirebaseUtils.registerUser(email, password) { isSuccess, errorMessage ->
            if (isSuccess) {
                val userId = FirebaseUtils.auth.currentUser?.uid ?: return@registerUser
                FirebaseUtils.saveUserData(userId, username, email) { success ->
                    if (success) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Profile::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Registration failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }
}
