package dk.itu.moapd.scootersharing.jonli.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jonli.databinding.ListRidesBinding
import dk.itu.moapd.scootersharing.jonli.models.Scooter

class ScooterArrayAdapter(
    options: FirebaseRecyclerOptions<Scooter>,
    private val onClick: (String) -> Unit,
) :
    FirebaseRecyclerAdapter<Scooter, ScooterArrayAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder
    (parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRidesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = super.getItemCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int, scooter: Scooter) {
        val key = this.getRef(position).key
        holder.apply {
            bind(scooter, key, onClick)
        }
    }

    class ViewHolder(private val binding: ListRidesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scooter: Scooter, key: String?, onClick: (String) -> Unit) {
            binding.title.text = scooter.name
            binding.description.text = scooter.toString()
            binding.scooterLayout.setOnClickListener {
                key?.let {
                    onClick(it)
                }
            }

            // Get the public thumbnail URL.
            val storage = Firebase.storage(Companion.BUCKET_URL)
            val imageRef = storage.reference.child("${scooter.image}")

            // Clean the image UI component.
            binding.image.setImageResource(0)

            // Download and set an image into the ImageView.
            imageRef.downloadUrl.addOnSuccessListener {
                Glide.with(itemView.context)
                    .load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.image)
            }
        }

        companion object {
            const val BUCKET_URL = "gs://scooter-sharing-b2ed6.appspot.com"
        }
    }
}
