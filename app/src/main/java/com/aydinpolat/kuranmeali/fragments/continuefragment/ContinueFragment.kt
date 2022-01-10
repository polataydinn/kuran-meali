package com.aydinpolat.kuranmeali.fragments.continuefragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.FragmentContinueBinding
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.turkishmeal.TurkishMealFragment

class ContinueFragment() : Fragment() {
    private lateinit var _binding: FragmentContinueBinding
    private val binding get() = _binding
    var suras: Suras? = null
    private var toggleCounter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContinueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueToggleButton.setOnClickListener {
            when(toggleCounter){
                0 -> {
                    setOnlyTurkish()
                }
                1 -> {
                    setOnlyArabic()
                }
                2 -> {
                    setBothTurkishAndArabic()
                }
            }
        }
        binding.continueBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, TurkishMealFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.continueSuraName.text = suras?.suraName
        binding.continueTurkishAyat.text = suras?.ayets?.get(0)?.ayatText
        binding.continueArabicAyat.text =
            suras?.ayetsArabic?.filter { it.ayatId == suras?.ayets?.get(0)?.ayatId }?.get(0)?.ayatText
        binding.continueCounterText.text = (suras?.suraId?.plus(1)).toString() + "/114"
    }

    private fun setOnlyTurkish() {
        binding.continueBottomCard.visibility = View.INVISIBLE
        binding.continueBottomScroll.visibility = View.INVISIBLE
        binding.continueBottomLanguage.visibility = View.INVISIBLE
        binding.continueBottomTextSeek.visibility = View.INVISIBLE
        binding.continueBottomTextSizeButton.visibility = View.INVISIBLE
        binding.continueArabicAyat.text = suras?.ayets?.get(0)?.ayatText
        binding.continueTopLanguage.text = "Türkçe"
        toggleCounter++
    }

    private fun setOnlyArabic() {
        binding.continueTopLanguage.text = "Arapça"
        binding.continueArabicAyat.text =
            suras?.ayetsArabic?.filter { it.ayatId == suras?.ayets?.get(0)?.ayatId }?.get(0)?.ayatText
        toggleCounter++
    }

    private fun setBothTurkishAndArabic() {
        binding.continueBottomCard.visibility = View.VISIBLE
        binding.continueBottomScroll.visibility = View.VISIBLE
        binding.continueBottomLanguage.visibility = View.VISIBLE
        binding.continueBottomTextSeek.visibility = View.VISIBLE
        binding.continueBottomTextSizeButton.visibility = View.VISIBLE
        binding.continueTurkishAyat.text = suras?.ayets?.get(0)?.ayatText
        binding.continueArabicAyat.text =
            suras?.ayetsArabic?.filter { it.ayatId == suras?.ayets?.get(0)?.ayatId }?.get(0)?.ayatText
        binding.continueTopLanguage.text = "Arapça"
        binding.continueBottomLanguage.text = "Türkçe"
        toggleCounter = 0
    }
}