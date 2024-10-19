package com.example.memoriesrecorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memoriesrecorder.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    // Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        // Save memory when floating action button is clicked
        binding.fabSaveMemory.setOnClickListener {
            saveMemory()
        }

        return binding.root
    }

    // Function to save memory to Firestore
    private fun saveMemory() {
        val title = binding.inputTitle.text.toString().trim()
        val description = binding.inputDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Snackbar.make(binding.root, "Please enter both title and description", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Get the current date
        val currentDate = getCurrentDate()

        // Create memory object
        val memory = hashMapOf(
            "title" to title,
            "description" to description,
            "timestamp" to System.currentTimeMillis(), // current time for sorting
            "date" to currentDate // formatted date
        )

        // Save to Firestore in the "memories" collection
        firestore.collection("memories")
            .add(memory)
            .addOnSuccessListener {
                Snackbar.make(binding.root, "Memory saved successfully", Snackbar.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Error saving memory: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
    }

    // Function to get the current date in a specific format (e.g., "MMM dd, yyyy")
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    // Function to clear input fields after saving
    private fun clearInputs() {
        binding.inputTitle.text?.clear()
        binding.inputDescription.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
