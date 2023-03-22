package dk.itu.moapd.scootersharing.jonli.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.jonli.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter

class CustomArrayAdapter(
    private val data: List<Scooter>,
    private val onClick: (Scooter) -> Boolean,
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
        holder.bind(scooter, onClick)
    }

    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(scooter: Scooter, onClick: (Scooter) -> Boolean) {
            binding.title.text = scooter.name
            binding.description.text = scooter.toString()
            binding.root.setOnLongClickListener { onClick(scooter) }
        }
    }
}
