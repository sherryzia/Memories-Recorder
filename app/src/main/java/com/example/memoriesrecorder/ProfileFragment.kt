package com.example.memoriesrecorder

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.memoriesrecorder.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var user: FirebaseUser
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        user = auth.currentUser!!

        loadUserData()

        // Logout Button Logic
        binding.logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate back to LoginActivity
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear stack
            startActivity(intent)
            requireActivity().finish() // Optional: Finish the ProfileFragment activity
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

    private fun loadUserData() {
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
    }

    private fun showUpdateDialog(field: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())

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
                Toast.makeText(requireContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        dialogBuilder.create().show()
    }

    private fun updateUsername(newUsername: String) {
        val userId = user.uid
        val userRef = firestore.collection("users").document(userId)

        userRef.update("username", newUsername)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()
                binding.username.text = newUsername // Update UI
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePassword(newPassword: String) {
        user.updatePassword(newPassword)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating password: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }
}
