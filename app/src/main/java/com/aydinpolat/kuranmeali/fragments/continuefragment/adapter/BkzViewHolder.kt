package com.aydinpolat.kuranmeali.fragments.continuefragment.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.BkzAyat
import com.aydinpolat.kuranmeali.databinding.CustomBkzCardBinding

class BkzViewHolder(val binding: CustomBkzCardBinding) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(bkzAyat: BkzAyat, onClickListener: (BkzAyat) -> Unit) {
        if (bkzAyat.isNoteorBkz){
            binding.bkzCardGoAyatText.text = "Nota Git"

        }
        binding.cardBkzAyatName.text =
            Constants.suraNames[bkzAyat.suraId] + " Suresi " + (bkzAyat.ayatId + 1) + ". ayet"
        binding.cardBkzGoAyat.setOnClickListener {
            onClickListener(bkzAyat)
        }
    }
}
