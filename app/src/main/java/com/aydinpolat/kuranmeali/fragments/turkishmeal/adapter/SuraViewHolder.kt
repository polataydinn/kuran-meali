package com.aydinpolat.kuranmeali.fragments.turkishmeal.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.CustomSuraCardBinding

class SuraViewHolder(private val binding: CustomSuraCardBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(suras: Suras, onClickLister: (Suras, Int) -> Unit) {
        binding.suraCardNumber.text = (suras.suraId.toInt() + 1).toString()
        binding.suraCardSuraName.text = suras.suraName
        binding.suraCardAyatSize.text = "(" + suras.ayets.size + ")"

        binding.root.setOnClickListener {
            onClickLister(suras, absoluteAdapterPosition)
        }
    }
}
