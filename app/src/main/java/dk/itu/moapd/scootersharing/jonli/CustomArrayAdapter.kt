package dk.itu.moapd.scootersharing.jonli

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.jonli.databinding.ListRidesBinding

class CustomArrayAdapter(
    private val data: List<Scooter>,
) :
    RecyclerView.Adapter<CustomArrayAdapter.ViewHolder>() {

    override fun onCreateViewHolder
    (parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scooter = data[position]
        holder.bind(scooter)
    }

    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scooter: Scooter) {
            binding.title.text = scooter.name
            binding.description.text = scooter.toString()
        }
    }
}
