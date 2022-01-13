package com.aydinpolat.kuranmeali.fragments.continuefragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.data.models.BkzAyat
import com.aydinpolat.kuranmeali.databinding.CustomBkzCardBinding

class BkzAdapter(val onClickListener: (BkzAyat) -> Unit) : RecyclerView.Adapter<BkzViewHolder>() {
    var listOfBkz = emptyList<BkzAyat>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BkzViewHolder {
        val binding = CustomBkzCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BkzViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BkzViewHolder, position: Int) {
        holder.bind(listOfBkz[position], onClickListener)
    }

    override fun getItemCount() = listOfBkz.size

    fun setList(listOfBkz: List<BkzAyat>){
        this.listOfBkz = listOfBkz
    }
}