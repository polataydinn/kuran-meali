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
    private val listOfSuggestion: ArrayList<String> = arrayListOf()
    private lateinit var suggestionAdapter: ArrayAdapter<String>
    var listOfSearchResponse = mutableListOf<Ayats>()

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

    }

    private fun setSuraForwardFragment() {
        binding.searchSearchButton.setOnClickListener {
            when (binding.searchRadioGroup.checkedRadioButtonId) {
                0 -> {
                    searchSura()
                }
                1 -> {
                    searchWholeDatabase()
                }
            }

        }
    }

    private fun searchWholeDatabase() {
        var searchGuess = binding.searchEt.text.toString()
        baseViewModel.getAllSuras?.observeOnce(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                it.forEach {
                    it.ayets.forEach { ayat ->
                        if (ayat.ayatText.uppercase().contains(searchGuess.uppercase())){
                            listOfSearchResponse.add(ayat)
                        }
                    }

                }
                if (!listOfSearchResponse.isNullOrEmpty()){
                    val chooseListAyatFragment = ChooseListAyatFragment()
                    chooseListAyatFragment.listOfAyats = listOfSearchResponse
                    chooseListAyatFragment.searchedWord = searchGuess
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.main_container_view, chooseListAyatFragment)
                        ?.addToBackStack("")
                        ?.commit()
                }else{
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
        suraName = "%$suraName%"
        baseViewModel.searchDatabase(suraName)
            .observeOnce(viewLifecycleOwner) { searchResponse ->
                if (suraName.isNotEmpty()) {
                    val chooseAyatFragment = ChooseAyatFragment()
                    chooseAyatFragment.ayatSize = searchResponse[0].ayets.size
                    chooseAyatFragment.suraId = searchResponse[0].suraId
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.main_container_view, chooseAyatFragment)
                        ?.addToBackStack("")
                        ?.commit()
                }
            }
    }

    private fun searchEditTextListeners() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var suraName = binding.searchEt.text.toString()
                suraName = "%$suraName%"
                baseViewModel.searchDatabase(suraName)
                    .observeOnce(viewLifecycleOwner) { searchResponse ->
                        if (searchResponse.isNotEmpty()) {
                            listOfSuggestion.clear()
                            searchResponse.forEach {
                                listOfSuggestion.add(it.suraName)
                            }
                            suggestionAdapter = ArrayAdapter<String>(
                                (activity as MainActivity),
                                android.R.layout.simple_list_item_1,
                                listOfSuggestion
                            )
                            binding.searchEt.setAdapter(suggestionAdapter)
                        }
                    }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onPause() {
        super.onPause()
        fragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }
}