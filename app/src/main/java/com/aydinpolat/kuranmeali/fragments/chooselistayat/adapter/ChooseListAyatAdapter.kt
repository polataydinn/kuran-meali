package com.aydinpolat.kuranmeali.fragments.chooselistayat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.databinding.CustomBkzCardBinding

class ChooseListAyatAdapter(val onItemClickListener: (Int) -> Unit) : RecyclerView.Adapter<ChooseListAyatViewHolder>() {
    var listOfAyats = emptyList<Ayats>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseListAyatViewHolder {
        val binding =
            CustomBkzCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChooseListAyatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseListAyatViewHolder, position: Int) {
        holder.bind(listOfAyats[position], onItemClickListener)
    }

    override fun getItemCount() = listOfAyats.size

    fun setList(listOfAyats: List<Ayats>){
        this.listOfAyats = listOfAyats
    }
}