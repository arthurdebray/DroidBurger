package fr.isen.debray.arthurdroidburger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PastOrdersAdapter(private val pastOrders: List<PastOrder>) :
    RecyclerView.Adapter<PastOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_past_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = pastOrders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return pastOrders.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pastOrder: PastOrder) {
            itemView.findViewById<TextView>(R.id.tvBurger).text = "Burger: ${pastOrder.burger}"
            itemView.findViewById<TextView>(R.id.tvDeliveryTime).text = "Delivery Time: ${pastOrder.delivery_time}"

        }
    }
}
