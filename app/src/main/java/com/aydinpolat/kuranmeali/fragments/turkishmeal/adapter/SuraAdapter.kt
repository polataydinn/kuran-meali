package com.aydinpolat.kuranmeali.fragments.turkishmeal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.CustomSuraCardBinding

class SuraAdapter(val onClickLister:  (Suras, Int) -> Unit): RecyclerView.Adapter<SuraViewHolder>() {
    private var suraList = emptyList<Suras>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuraViewHolder {
        val binding = CustomSuraCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SuraViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuraViewHolder, position: Int) {
        holder.bind(suraList[position], onClickLister)
    }

    override fun getItemCount() = suraList.size

    fun setSuraList(suraList: List<Suras>){
        this.suraList = suraList
    }
}