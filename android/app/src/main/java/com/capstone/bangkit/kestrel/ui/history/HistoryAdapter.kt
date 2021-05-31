package com.capstone.bangkit.kestrel.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.bangkit.kestrel.databinding.ItemRowHistoryBinding

class HistoryAdapter(private val listHistory: ArrayList<History>) :
    RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding =
            ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int = listHistory.size

    inner class ListViewHolder(private val binding: ItemRowHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            with(binding) {
                tvItemName.text = history.alphabet
                tvItemDate.text = history.date
            }
        }
    }

}