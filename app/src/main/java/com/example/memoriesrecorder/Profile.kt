package com.example.memoriesrecorder

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memoriesrecorder.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = auth.currentUser!!
        val docRef = db.collection("users").document(user.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("username")
                    val email = document.getString("email")
                    binding.username.text = username
                    binding.uEmail.text = email
                    Log.d("TAG", "Username: $username")
                    Log.d("TAG", "Email: $email")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }


        // Logout Button Logic
        binding.logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            intent = android.content.Intent(this, Login::class.java)
            startActivity(intent)
            finish()

        }

        // Update Username Button Logic
        binding.uName.setOnClickListener {
            showUpdateDialog("username")
        }

        // Update Password Button Logic
        binding.password.setOnClickListener {
            showUpdateDialog("password")
        }

    }

    // Method to show update dialog
    private fun showUpdateDialog(field: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val input = EditText(this)

        when (field) {
            "username" -> {
                dialogBuilder.setTitle("Update Username")
                input.hint = "Enter new username"
                input.inputType = InputType.TYPE_CLASS_TEXT
            }

            "password" -> {
                dialogBuilder.setTitle("Update Password")
                input.hint = "Enter new password"
                input.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        dialogBuilder.setView(input)

        dialogBuilder.setPositiveButton("Update") { dialog, _ ->
            val newValue = input.text.toString()
            if (newValue.isNotEmpty()) {
                when (field) {
                    "username" -> updateUsername(newValue)
                    "password" -> updatePassword(newValue)
                }
            } else {
                Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    // Method to update username
    private fun updateUsername(newUsername: String) {
        val userId = user.uid
        val userRef = firestore.collection("users").document(userId)

        userRef.update("username", newUsername)
            .addOnSuccessListener {
                Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
                binding.username.text = newUsername // Update UI
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating username: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    // Method to update password
    private fun updatePassword(newPassword: String) {
        user.updatePassword(newPassword)
            .addOnSuccessListener {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating password: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
