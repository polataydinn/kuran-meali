package com.aydinpolat.kuranmeali.fragments.chooselistayat.adapter

import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.databinding.CustomBkzCardBinding

class ChooseListAyatViewHolder(val binding: CustomBkzCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(ayats: Ayats, onItemClickListener: (Int,Int) -> Unit) {
        binding.cardBkzAyatName.text = ayats.suraName + " Suresi " + ayats.ayatId + " Ayet "

        binding.cardBkzGoAyat.setOnClickListener {
            var counter = 0
            Constants.suraNames.forEach {
                if (it == ayats.suraName){
                    onItemClickListener((ayats.ayatId.toInt() - 1), counter)
                }
                counter++
            }
        }
    }

}
