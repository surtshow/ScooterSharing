package dk.itu.moapd.scootersharing.jonli

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomArrayAdapter(
    context: Context,
    private var resource: Int,
    data: List<Scooter>,
) :
    ArrayAdapter<Scooter>(context, R.layout.list_rides, data) {

    private class ViewHolder(view: View) {
        val title: TextView = view.findViewById(R.id.title)
        val description: TextView = view.findViewById(R.id.description)
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
    ): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val dummy = getItem(position)
        viewHolder.title.text = dummy?.name
        viewHolder.description.text = dummy.toString()

        view?.tag = viewHolder
        return view!!
    }
}
