package dk.itu.moapd.scootersharing.jonli.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.jonli.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.jonli.models.Ride
import dk.itu.moapd.scootersharing.jonli.utils.Utils.asPrice
import dk.itu.moapd.scootersharing.jonli.utils.Utils.formatDate

class RideArrayAdapter(
    options: FirebaseRecyclerOptions<Ride>,
    private val onClick: (String, Ride) -> Unit = { _, _ -> },
) :
    FirebaseRecyclerAdapter<Ride, RideArrayAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder
    (parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = super.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ride: Ride) {
        val key = this.getRef(position).key
        holder.apply {
            bind(ride, key, onClick)
        }
    }

    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ride: Ride, key: String?, onClick: (String, Ride) -> Unit) {
            binding.scooterName.text = "Scooter ride"
            binding.startTime.text = ride.startTime?.formatDate() ?: "N/A"
            binding.endTime.text = ride.endTime?.formatDate() ?: "N/A"
            binding.price.text = ride.price?.asPrice() ?: "0.0 DKK"
            binding.status.text = ride.status.name
            binding.rideLayout.setOnClickListener {
                key?.let {
                    onClick(it, ride)
                }
            }
        }
    }
}
