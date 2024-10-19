import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memoriesrecorder.Memory
import com.example.memoriesrecorder.R

class MemoryAdapter(private val memories: List<Memory>, private val onMemoryClick: (Memory) -> Unit) :
    RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.memory, parent, false)
        return MemoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val memory = memories[position]

        // Display the count (position + 1)
        if(position < 9) {
            holder.memoryNumberTextView.text = "0${position + 1}"
        } else {
            holder.memoryNumberTextView.text = (position + 1).toString()        }

        // Bind the memory data
        holder.bind(memory)

        // Set onClickListener to open details when the memory is clicked
        holder.itemView.setOnClickListener {
            onMemoryClick(memory)  // Trigger the click event passed from fragment
        }
    }


    override fun getItemCount(): Int = memories.size

    class MemoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoryNumberTextView: TextView = itemView.findViewById(R.id.memory_number)
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description)

        fun bind(memory: Memory) {
            titleTextView.text = memory.title
            dateTextView.text = memory.date
            descriptionTextView.text = memory.description
        }
    }

}
