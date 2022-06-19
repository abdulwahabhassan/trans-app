package ng.gov.imostate.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.R
import ng.gov.imostate.databinding.ItemRouteBinding
import ng.gov.imostate.model.domain.Route
import timber.log.Timber
import java.util.*

class RoutesAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Route) -> Unit
) : ListAdapter<Route, RoutesAdapter.RouteVH>(object :
    DiffUtil.ItemCallback<Route>() {

    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: RouteVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class RouteVH(private val binding: ItemRouteBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(route: Route) {
            Timber.d("$route")
            with(binding) {
                routeNameTV.text = route.from?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                } + " to " + route.to?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                if (route.status.equals("active", true)) {
                    routeStatusTV.setTextColor(root.context.resources.getColor(R.color.green))
                } else {
                    routeStatusTV.setTextColor(root.context.resources.getColor(R.color.red))
                }
                routeStatusTV.text = route.status
                when (route.selected) {
                    true -> {
                        selectedIV.setImageResource(R.drawable.ic_check)
                    }
                    false -> {
                        selectedIV.setImageResource(R.drawable.ic_unchecked)
                    }
                    else -> {
                        selectedIV.setImageResource(R.drawable.ic_location)
                    }
                }
            }
        }
    }
}