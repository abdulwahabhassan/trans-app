package ng.gov.imostate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.databinding.ItemTransactionBinding
import java.lang.StringBuilder

class TransactionsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Transaction) -> Unit
) : ListAdapter<Transaction, TransactionsAdapter.TransactionHistoryVH>(object :
    DiffUtil.ItemCallback<Transaction>() {

    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHistoryVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionHistoryVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: TransactionHistoryVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class TransactionHistoryVH(private val binding: ItemTransactionBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }


        fun bind(transaction: Transaction) {
            with(binding) {
                transactionTitleTV.text = transaction.title
                transactionAmountTV.text = StringBuilder("₦").append(AppUtils.formatCurrency(transaction.amount))
                transactionDateTV.text = transaction.date
                transactionIV.setImageResource(listOf(R.drawable.ic_transaction_credit, R.drawable.ic_transaction_vehicle).random())
            }
        }

    }


}