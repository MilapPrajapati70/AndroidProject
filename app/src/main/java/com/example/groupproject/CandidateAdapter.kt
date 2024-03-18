import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groupproject.Candidate
import com.example.groupproject.DetailActivity
import com.example.groupproject.R
import java.util.*

class CandidateAdapter(
    private val originalCandidatesList: List<Candidate>,
    private val nodata: TextView?
) :
    RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder>(), Filterable {

    private var candidatesList = originalCandidatesList.toMutableList()

    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder components declaration
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
        private val viewProfileButton: Button = itemView.findViewById(R.id.viewProfileButton)

        init {
            viewProfileButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val candidate = candidatesList[position]
                    // Start DetailActivity and pass candidate data
                    // Make sure to replace DetailActivity::class.java with your actual DetailActivity class
                    val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                        putExtra("name", candidate.name)
                        putExtra("title", candidate.title)
                        putExtra("photoUrl", candidate.photoUrl)
                        // Add more data as needed
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }

        fun bind(candidate: Candidate) {
            // Bind data to ViewHolder components
            nameTextView.text = candidate.name
            titleTextView.text = candidate.title

            // Load candidate photo with Glide
            Glide.with(itemView)
                .load(candidate.photoUrl)
                .placeholder(R.drawable.s2) // Placeholder image while loading
                .error(R.drawable.s1) // Error image if loading fails
                .into(photoImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_candidate, parent, false)
        return CandidateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = candidatesList[position]
        holder.bind(candidate)
    }

    override fun getItemCount(): Int = candidatesList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Candidate>()
                if (constraint.isNullOrBlank()) {
                    filteredList.addAll(originalCandidatesList)
                } else {
                    val filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim()
                    for (candidate in originalCandidatesList) {
                        if (candidate.name!!.toLowerCase(Locale.ROOT).contains(filterPattern) ||
                            candidate.title!!.toLowerCase(Locale.ROOT).contains(filterPattern)
                        ) {
                            filteredList.add(candidate)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                candidatesList.clear()
                candidatesList.addAll(results?.values as List<Candidate>)
                if (candidatesList.isEmpty()) {
                    nodata?.visibility = View.VISIBLE
                } else {
                    nodata?.visibility = View.GONE
                }
                notifyDataSetChanged()
            }
        }
    }
}
