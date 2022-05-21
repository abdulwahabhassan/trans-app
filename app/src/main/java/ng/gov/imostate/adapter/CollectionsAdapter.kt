package ng.gov.imostate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.R
import ng.gov.imostate.databinding.ItemCollectionBinding
import ng.gov.imostate.model.domain.Transaction
import ng.gov.imostate.databinding.ItemTransactionBinding
import ng.gov.imostate.model.domain.Collection
import timber.log.Timber
import java.lang.StringBuilder

class CollectionsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Collection) -> Unit
) : ListAdapter<Collection, CollectionsAdapter.CollectionVH>(object :
    DiffUtil.ItemCallback<Collection>() {

    override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CollectionVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class CollectionVH(private val binding: ItemCollectionBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }


        fun bind(transaction: Collection) {
            Timber.d("$transaction")
            with(binding) {
                transactionTitleTV.text = transaction.internalReference
                transactionAmountTV.text = StringBuilder("₦").append(
                    transaction.amount?.let {
                        AppUtils.formatCurrency(it)
                    }
                )
                transactionDateTV.text = transaction.date?.let { AppUtils.formatDateToFullDate(it) }
                transactionIV.setImageResource(R.drawable.ic_transaction_credit)
            }
        }

    }
}