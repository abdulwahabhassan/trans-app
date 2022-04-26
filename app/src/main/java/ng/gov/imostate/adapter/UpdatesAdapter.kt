package ng.gov.imostate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ng.gov.imostate.databinding.ItemUpdateBinding
import ng.gov.imostate.model.Update
import timber.log.Timber

class UpdatesAdapter(
    private val onItemClicked: (position: Int, itemAtPosition: Update) -> Unit
) : ListAdapter<Update, UpdatesAdapter.UpdateVH>(object : DiffUtil.ItemCallback<Update>() {

    override fun areItemsTheSame(oldItem: Update, newItem: Update): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Update, newItem: Update): Boolean {
        return oldItem == newItem
    }

}) {

    init {
        Timber.d("UpdateAdapter init")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdatesAdapter.UpdateVH {
        //inflate the view to be used by the payment option view holder
        val binding = ItemUpdateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        Timber.d("onCreate Viewholder")
        return UpdateVH(binding, onItemClick = { position ->
            val itemAtPosition = currentList[position]
            this.onItemClicked(position, itemAtPosition)
        })
    }

    override fun onBindViewHolder(holder: UpdatesAdapter.UpdateVH, position: Int) {
        Timber.d("Onbind Viewholder")
        val itemAtPosition = currentList[position]
        holder.bind(itemAtPosition)
    }

    override fun getItemCount(): Int = currentList.size

    inner class UpdateVH(private val binding: ItemUpdateBinding, onItemClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            Timber.d("Viewholder init init")
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(update: Update) {
            Timber.d("$update")
            with(binding) {
                updateTitleTV.text = update.title
            }
        }

    }
}