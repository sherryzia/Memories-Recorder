package com.example.memoriesrecorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.memoriesrecorder.databinding.FragmentMemoryDetailBinding

class MemoryDetailFragment : DialogFragment() {

    private var _binding: FragmentMemoryDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_MEMORY = "memory"

        fun newInstance(memory: Memory): MemoryDetailFragment {
            val args = Bundle()
            args.putParcelable(ARG_MEMORY, memory)  // Assuming Memory is Parcelable
            val fragment = MemoryDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemoryDetailBinding.inflate(inflater, container, false)

        val memory = arguments?.getParcelable<Memory>(ARG_MEMORY)

        memory?.let {
            binding.memoryTitle.text = it.title
            binding.memoryDescription.text = it.description
            binding.memoryDate.text = it.date
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
