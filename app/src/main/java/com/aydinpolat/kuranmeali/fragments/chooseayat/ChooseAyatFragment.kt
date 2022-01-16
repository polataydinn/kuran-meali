package com.aydinpolat.kuranmeali.fragments.chooseayat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.databinding.FragmentChooseAyatBinding
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel
import android.text.InputFilter
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.fragments.continuefragment.ContinueFragment
import com.test.InputFilterMinMax

class ChooseAyatFragment : Fragment() {
    private lateinit var _binding: FragmentChooseAyatBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    var suraId = 0
    var ayatSize = 0

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
        binding.chooseNumberPicker.maxValue = ayatSize
        binding.chooseNumberPicker.minValue = 1
        binding.chooseSuraName.text = "1. " + Constants.suraNames[suraId] +  " Suresi".uppercase()
        binding.chooseEditText.filters = arrayOf<InputFilter>(InputFilterMinMax(0, ayatSize))

        binding.chooseGoAyatButton.setOnClickListener {
            val continueFragment = ContinueFragment()
            if (binding.chooseEditText.text.toString() == ""){
                val ayatId =binding.chooseNumberPicker.value
                continueFragment.suraPosition = suraId
                continueFragment.ayatCounter = ayatId - 1
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_container_view, continueFragment)?.addToBackStack("")
                    ?.commit()
            }
        }
    }
}