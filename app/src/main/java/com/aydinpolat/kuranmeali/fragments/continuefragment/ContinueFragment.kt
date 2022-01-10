package com.aydinpolat.kuranmeali.fragments.continuefragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.FragmentContinueBinding
import com.aydinpolat.kuranmeali.fragments.turkishmeal.TurkishMealFragment
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class ContinueFragment() : Fragment() {
    private lateinit var _binding: FragmentContinueBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    var suras: Suras? = null
    var listOfSuras: List<Suras> = emptyList()
    var suraPosition: Int? = null
    private var toggleCounter = 0
    private var ayatCounter = 0

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
            toggleCounter++
            if (toggleCounter > 2) {
                toggleCounter = 0
            }
            languageChooser()
        }

        binding.continueNextAyat.setOnClickListener {
            if (!listOfSuras.isNullOrEmpty()) {
                ayatCounter++
                languageChooser()
            }
        }

        binding.continuePreviousAyat.setOnClickListener {

        }

        binding.continueBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, TurkishMealFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.continueSuraName.text = suras?.suraName
        binding.continueTurkishAyat.text = suras?.ayets?.get(0)?.ayatText
        binding.continueArabicAyat.text =
            listOfSuras[suraPosition!!].ayetsArabic.filter { it.ayatId == suras?.ayets?.get(0)?.ayatId }[ayatCounter].ayatText
        binding.continueCounterText.text =
            (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
    }

    private fun languageChooser() {
        when (toggleCounter) {
            0 -> {
                setBothTurkishAndArabic()
            }
            1 -> {
                setOnlyTurkish()
            }
            2 -> {
                setOnlyArabic()
            }
        }
    }

    private fun setOnlyTurkish() {
        binding.continueBottomCard.visibility = View.INVISIBLE
        binding.continueBottomScroll.visibility = View.INVISIBLE
        binding.continueBottomLanguage.visibility = View.INVISIBLE
        binding.continueBottomTextSeek.visibility = View.INVISIBLE
        binding.continueBottomTextSizeButton.visibility = View.INVISIBLE
        setTurkishTextOnly()
    }

    private fun setOnlyArabic() {
        setArabicTextOnly()
    }

    private fun setBothTurkishAndArabic() {
        binding.continueBottomCard.visibility = View.VISIBLE
        binding.continueBottomScroll.visibility = View.VISIBLE
        binding.continueBottomLanguage.visibility = View.VISIBLE
        binding.continueBottomTextSeek.visibility = View.VISIBLE
        binding.continueBottomTextSizeButton.visibility = View.VISIBLE
        setBothTurkishAndArabicText()
    }

    private fun setTurkishTextOnly() {
        binding.continueArabicAyat.text = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText
        binding.continueTopLanguage.text = "Türkçe"
    }

    private fun setArabicTextOnly() {
        binding.continueTopLanguage.text = "Arapça"

        binding.continueArabicAyat.text =
            listOfSuras[suraPosition!!].ayetsArabic.filter {
                it.ayatId == suras?.ayets?.get(
                    ayatCounter
                )?.ayatId
            }[0].ayatText
    }

    @SuppressLint("SetTextI18n")
    private fun setBothTurkishAndArabicText() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets.get(ayatCounter).ayatText

            if (!listOfSuras[suraPosition!!].ayets.isNullOrEmpty()) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic.filter {
                        it.ayatId == listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId || it.ayatId == listOfSuras[suraPosition!!].ayets[ayatCounter + 1].ayatId
                    }[0].ayatText
            }

            binding.continueTopLanguage.text = "Arapça"
            binding.continueBottomLanguage.text = "Türkçe"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText

            binding.continueArabicAyat.text =
                listOfSuras[suraPosition!!].ayetsArabic?.filter { it.ayatId == listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId }[0].ayatText

            binding.continueTopLanguage.text = "Arapça"
            binding.continueBottomLanguage.text = "Türkçe"
        }
    }
}