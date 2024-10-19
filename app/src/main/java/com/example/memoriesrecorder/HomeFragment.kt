package com.example.memoriesrecorder

import MemoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memoriesrecorder.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var memoryAdapter: MemoryAdapter
    private val memoryList = mutableListOf<Memory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()

        // Fetch data from Firestore
        fetchMemories()

        return binding.root
    }

    private fun setupRecyclerView() {
        memoryAdapter = MemoryAdapter(memoryList) { memory ->
            openMemoryDetail(memory)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memoryAdapter
        }

        // Set up ItemTouchHelper for swipe-to-delete (already in place)
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No moving items, just swipe to delete
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteMemory(position) // Call deleteMemory() to handle deletion
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun openMemoryDetail(memory: Memory) {
        val fragment = MemoryDetailFragment.newInstance(memory)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun fetchMemories() {
        firestore.collection("memories")
            .get()
            .addOnSuccessListener { documents ->
                memoryList.clear()
                for (document in documents) {
                    val memory = document.toObject<Memory>()
                    memory.id = document.id // Set the Firestore document ID in memory object
                    memoryList.add(memory)
                }
                memoryAdapter.notifyDataSetChanged()
                toggleEmptyView()
            }
            .addOnFailureListener {
                toggleEmptyView()
            }
    }

    private fun deleteMemory(position: Int) {
        // Get the memory to be deleted
        val memory = memoryList[position]
        val memoryId = memory.id  // Firestore document ID

        // Temporarily remove the memory from the list and notify the adapter
        memoryList.removeAt(position)
        memoryAdapter.notifyItemRemoved(position)
        toggleEmptyView() // Check if the empty view should be displayed

        // Show Snackbar with Undo option
        val snackbar = Snackbar.make(binding.root, "Memory deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            // If undo is clicked, re-add the memory to the list and notify the adapter
            memoryList.add(position, memory)
            memoryAdapter.notifyItemInserted(position)
            toggleEmptyView()
        }

        // Dismiss listener of the Snackbar - delete from Firestore only if not undone
        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // If the event was not due to the "Undo" button
                if (event != DISMISS_EVENT_ACTION) {
                    // Remove the memory from Firestore
                    firestore.collection("memories").document(memoryId)
                        .delete()
                        .addOnSuccessListener {
                            memoryAdapter.notifyDataSetChanged() // Refresh to update memory numbers

                        }
                        .addOnFailureListener { e ->
                            // Handle the failure
                            e.printStackTrace()
                            // You can show an error message to the user if needed
                        }
                }
            }
        })

        snackbar.show()
    }


    private fun toggleEmptyView() {
        if (memoryList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyMemoriesText.visibility = View.VISIBLE
            binding.emptyMemoriesImage.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyMemoriesText.visibility = View.GONE
            binding.emptyMemoriesImage.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
