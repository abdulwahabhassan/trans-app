package ng.gov.imostate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.databinding.ItemDailyRateBinding
import ng.gov.imostate.databinding.ItemUpdateBinding
import ng.gov.imostate.model.domain.Rate
import ng.gov.imostate.model.domain.Update
import timber.log.Timber
import java.util.*

class DailyRatesAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Rate) -> Unit
) : ListAdapter<Rate, DailyRatesAdapter.DailyRateVH>(object : DiffUtil.ItemCallback<Rate>() {

    override fun areItemsTheSame(oldItem: Rate, newItem: Rate): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Rate, newItem: Rate): Boolean {
        return oldItem == newItem
    }

}) {

    init {
        Timber.d("UpdateAdapter init")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRatesAdapter.DailyRateVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemDailyRateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        Timber.d("onCreate Viewholder")
        return DailyRateVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })
    }

    override fun onBindViewHolder(holder: DailyRatesAdapter.DailyRateVH, position: Int) {
        Timber.d("Onbind Viewholder")
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }

    override fun getItemCount(): Int = currentList.size

    inner class DailyRateVH(private val binding: ItemDailyRateBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            Timber.d("Viewholder init init")
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(rate: Rate) {
            Timber.d("$rate")
            with(binding) {
                binding.categoryTV.text = rate.category?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                    ?: ""
                binding.priceTV.text = rate.amount ?: ""
            }
        }

    }
}