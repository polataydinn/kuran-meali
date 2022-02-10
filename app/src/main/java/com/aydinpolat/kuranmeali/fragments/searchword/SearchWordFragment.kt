package com.aydinpolat.kuranmeali.fragments.searchword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.activities.MainActivity
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.databinding.FragmentSearchWordBinding
import com.aydinpolat.kuranmeali.fragments.chooseayat.ChooseAyatFragment
import com.aydinpolat.kuranmeali.fragments.chooselistayat.ChooseListAyatFragment
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment
import com.aydinpolat.kuranmeali.util.observeOnce
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class SearchWordFragment : Fragment() {
    private lateinit var _binding: FragmentSearchWordBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    private var listOfSuggestion: ArrayList<String> = arrayListOf()
    private lateinit var suggestionAdapter: ArrayAdapter<String>
    var listOfSearchResponse = mutableListOf<Ayats>()
    var isSuggessEditTextClicked: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchWordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchSuraRadioButton.isChecked = true

        binding.searchBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.profileButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()
        }
        searchEditTextListeners()
        setSuraForwardFragment()

        binding.searchRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (binding.searchRadioGroup.checkedRadioButtonId) {
                R.id.search_sura_radio_button -> {
                    isSuggessEditTextClicked = true
                }
                R.id.search_ayat_radio_button -> {
                    isSuggessEditTextClicked = false
                }
            }
        }

    }

    private fun setSuraForwardFragment() {
        binding.searchSearchButton.setOnClickListener {
            when (binding.searchRadioGroup.checkedRadioButtonId) {
                R.id.search_sura_radio_button -> {
                    isSuggessEditTextClicked = true
                    searchSura()
                }
                R.id.search_ayat_radio_button -> {
                    isSuggessEditTextClicked = false
                    searchWholeDatabase()
                    listOfSearchResponse = mutableListOf()
                }
            }

        }
    }

    private fun searchWholeDatabase() {
        listOfSuggestion.clear()
        var searchGuess = binding.searchEt.text.toString()
        baseViewModel.getAllSuras?.observeOnce(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                it.forEach {
                    it.ayets.forEach { ayat ->
                        if (ayat.ayatText.uppercase().contains(searchGuess.uppercase())) {
                            listOfSearchResponse.add(ayat)
                        }
                    }

                }
                if (!listOfSearchResponse.isNullOrEmpty()) {
                    val chooseListAyatFragment = ChooseListAyatFragment()
                    chooseListAyatFragment.listOfAyats = listOfSearchResponse
                    chooseListAyatFragment.searchedWord = searchGuess
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.main_container_view, chooseListAyatFragment)
                        ?.addToBackStack("")
                        ?.commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aranana kelimeye ait sonuç bulunamadı",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun searchSura() {
        var suraName = binding.searchEt.text.toString()
        if (suraName.contains(" ") && suraName.contains(".")) {
            suraName = suraName.substringAfter(" ")
            suraName = suraName.substringBefore(" ")
        }
        suraName = "%$suraName%"
        baseViewModel.searchDatabase(suraName)
            .observeOnce(viewLifecycleOwner) { searchResponse ->
                if (!searchResponse.isNullOrEmpty()) {
                    val chooseAyatFragment = ChooseAyatFragment()
                    chooseAyatFragment.ayatSize = searchResponse[0].ayets.size
                    chooseAyatFragment.suraId = searchResponse[0].suraId
                    chooseAyatFragment.sura = searchResponse[0]
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.main_container_view, chooseAyatFragment)
                        ?.addToBackStack("")
                        ?.commit()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Aradığınız Sure Bulunamadı",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun searchEditTextListeners() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var suraName = binding.searchEt.text.toString().uppercase()
                suraName = "%$suraName%"
                if (isSuggessEditTextClicked) {
                    listOfSuggestion = arrayListOf()
                    baseViewModel.searchDatabase(suraName)
                        .observeOnce(viewLifecycleOwner) { searchResponse ->
                            if (searchResponse.isNotEmpty()) {
                                searchResponse.forEach {
                                    listOfSuggestion.add((it.suraId + 1).toString() + ". " + it.suraName.uppercase() + " SÛRESİ")
                                }
                                suggestionAdapter = ArrayAdapter<String>(
                                    (activity as MainActivity),
                                    android.R.layout.simple_list_item_1,
                                    listOfSuggestion.distinct()
                                )
                                binding.searchEt.setAdapter(suggestionAdapter)
                                binding.searchEt.showDropDown()
                            }
                        }

                    baseViewModel.searchDatabase(changedSuraName(suraName))
                        .observeOnce(viewLifecycleOwner) { searchResponse ->
                            if (searchResponse.isNotEmpty()) {
                                searchResponse.forEach {
                                    listOfSuggestion.add((it.suraId + 1).toString() + ". " + it.suraName.uppercase() + " SÛRESİ")
                                }
                                suggestionAdapter = ArrayAdapter<String>(
                                    (activity as MainActivity),
                                    android.R.layout.simple_list_item_1,
                                    listOfSuggestion.distinct()
                                )
                                binding.searchEt.setAdapter(suggestionAdapter)
                                binding.searchEt.showDropDown()
                            }
                        }
                }else{
                    binding.searchEt.setAdapter(null)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })


    }

    private fun changedSuraName(suraName: String): String {
        var tempSuraName = suraName
        if (suraName.uppercase().contains("A")) {
            tempSuraName = suraName.replace("A", "Â")
        }
        if (suraName.uppercase().contains("U")) {
            tempSuraName = suraName.replace("U", "Û")
        }
        if (suraName.uppercase().contains("E")) {
            tempSuraName = suraName.replace("E", "Ê")
        }
        return tempSuraName
    }
}