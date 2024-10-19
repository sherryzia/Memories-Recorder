package com.example.memoriesrecorder

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoriesrecorder.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Redirect to Register Activity if user clicks on "Register Now"
        binding.register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        // Handle Login
        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (validateForm(email, password)) {
                loginUser(email, password)
            }
        }
    }

    // Function to validate email and password inputs
    private fun validateForm(email: String, password: String): Boolean {
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

        return true
    }

    // Function to login user using FirebaseUtils
    private fun loginUser(email: String, password: String) {
        FirebaseUtils.loginUser(email, password) { isSuccess, errorMessage ->
            if (isSuccess) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                // Navigate to main activity or dashboard
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }
}
