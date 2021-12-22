package com.capstone.bangkit.kestrel.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.bangkit.kestrel.R
import com.capstone.bangkit.kestrel.databinding.ItemRowAlphaBinding

class AlphabetAdapter(private val listAlpha: ArrayList<Alphabet>) :
    RecyclerView.Adapter<AlphabetAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowAlphaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listAlpha[position])
    }

    override fun getItemCount(): Int = listAlpha.size

    inner class ListViewHolder(private val binding: ItemRowAlphaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(alphabet: Alphabet) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(alphabet.image)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error))
                    .into(imgItemAlpha)

                tvItemName.text = alphabet.name
            }
        }
    }
}