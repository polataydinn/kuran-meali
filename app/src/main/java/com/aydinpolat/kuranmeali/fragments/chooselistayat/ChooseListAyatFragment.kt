package com.aydinpolat.kuranmeali.fragments.chooselistayat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.databinding.FragmentChooseListAyatBinding
import com.aydinpolat.kuranmeali.fragments.chooselistayat.adapter.ChooseListAyatAdapter
import com.aydinpolat.kuranmeali.fragments.continuefragment.ContinueFragment
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment
import com.aydinpolat.kuranmeali.fragments.searchword.SearchWordFragment

class ChooseListAyatFragment : Fragment() {
    private lateinit var _binding: FragmentChooseListAyatBinding
    private val binding get() = _binding
    var listOfAyats = mutableListOf<Ayats>()
    private lateinit var adapter: ChooseListAyatAdapter
    var searchedWord = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseListAyatBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chooseListDescription.text = "\"$searchedWord\"" + " Kelimesine ait " + (listOfAyats.size + 1) + " sonuç bulundu"

        adapter = ChooseListAyatAdapter{ ayatId, suraId ->
            val continueFragment = ContinueFragment()
            continueFragment.suraPosition = suraId
            continueFragment.ayatCounter = ayatId
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, continueFragment)?.addToBackStack("")
                ?.commit()
        }
        binding.chooseListBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, SearchWordFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.chooseListProfile.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()
        }
        adapter.setList(listOfAyats)

        binding.chooseListRecyclerView.adapter = adapter
    }
}