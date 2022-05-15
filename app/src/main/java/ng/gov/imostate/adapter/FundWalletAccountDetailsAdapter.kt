package ng.gov.imostate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.R
import ng.gov.imostate.databinding.ItemAccountDetailsBinding
import ng.gov.imostate.model.domain.AccountDetails
import timber.log.Timber
import java.lang.StringBuilder

class FundWalletAccountDetailsAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: AccountDetails) -> Unit
) : ListAdapter<AccountDetails, FundWalletAccountDetailsAdapter.FundWalletAccountDetailsVH>(object :
    DiffUtil.ItemCallback<AccountDetails>() {

    override fun areItemsTheSame(oldItem: AccountDetails, newItem: AccountDetails): Boolean {
        return oldItem.accountNumber == newItem.accountNumber
    }

    override fun areContentsTheSame(oldItem: AccountDetails, newItem: AccountDetails): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundWalletAccountDetailsVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemAccountDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FundWalletAccountDetailsVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })

    }

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: FundWalletAccountDetailsVH, position: Int) {
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }


    inner class FundWalletAccountDetailsVH(private val binding: ItemAccountDetailsBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }


        fun bind(accountDetails: AccountDetails) {
            Timber.d("$accountDetails")
            with(binding) {
                accountNumberAndBankTV
                    .text = "${accountDetails.accountNumber} | ${accountDetails.bankName}"
                accountNameTV.text = accountDetails.accountName
            }
        }

    }
}