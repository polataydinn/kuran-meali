package com.aydinpolat.kuranmeali.fragments.turkishmeal.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.CustomSuraCardBinding

class SuraViewHolder(private val binding: CustomSuraCardBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(suras: Suras, onClickLister: (Suras, Int) -> Unit) {
        binding.suraCardNumber.text = (suras.suraId + 1).toString()
        binding.suraCardSuraName.text = suras.suraName.uppercase() + " SÛRESİ"
        if (suras.ayets.last().ayatId.contains("-")){
            val ayatId = suras.ayets.last().ayatId.substringAfter("-")
            binding.suraCardAyatSize.text = "($ayatId)"
        }else{
            binding.suraCardAyatSize.text = "(" + suras.ayets.last().ayatId + ")"
        }
        binding.root.setOnClickListener {
            onClickLister(suras, absoluteAdapterPosition)
        }
    }
}
