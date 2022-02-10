package com.aydinpolat.kuranmeali.fragments.chooseayat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.databinding.FragmentChooseAyatBinding
import com.aydinpolat.kuranmeali.fragments.continuefragment.ContinueFragment
import com.aydinpolat.kuranmeali.util.getItemPositionByName
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class ChooseAyatFragment : Fragment() {
    private lateinit var _binding: FragmentChooseAyatBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    var suraId = 0
    var ayatSize = 0
    var sura: Suras? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseAyatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listOfSpinner: Array<String?> = arrayOfNulls(sura?.ayets?.size!!)
        var counterArray = 0
        sura?.ayets?.forEach {
            listOfSpinner[counterArray] = it.ayatId
            counterArray++
        }
        binding.chooseNumberPicker.minValue = 0
        binding.chooseNumberPicker.maxValue = sura?.ayets?.size!! - 1
        binding.chooseNumberPicker.displayedValues = listOfSpinner

        binding.chooseSuraName.text = (suraId + 1).toString()+ ". " + Constants.suraNames[suraId].uppercase() +  " SÛRESİ"

        binding.chooseGoAyatButton.setOnClickListener {
            val continueFragment = ContinueFragment()
            val ayatId = sura!!.ayets.getItemPositionByName(binding.chooseNumberPicker.displayedValues[binding.chooseNumberPicker.value])
            continueFragment.suraPosition = suraId
            continueFragment.ayatCounter = ayatId
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, continueFragment)?.addToBackStack("")
                ?.commit()

        }

        binding.chooseAyatOwnerNameBiography.setOnClickListener {
            val viewIntent = Intent("android.intent.action.VIEW",
                Uri.parse("http://www.cemalkulunkoglu.net/"))
            startActivity(viewIntent)
        }
    }
}