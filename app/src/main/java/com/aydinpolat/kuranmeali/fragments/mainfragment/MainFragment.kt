package com.aydinpolat.kuranmeali.fragments.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.FragmentMainBinding
import com.aydinpolat.kuranmeali.fragments.biography.BiographyFragment
import com.aydinpolat.kuranmeali.fragments.continuefragment.ContinueFragment
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment
import com.aydinpolat.kuranmeali.fragments.searchword.SearchWordFragment
import com.aydinpolat.kuranmeali.fragments.turkishmeal.TurkishMealFragment

class MainFragment : Fragment() {
    private lateinit var _binding: FragmentMainBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.searchWordMain.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, SearchWordFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.profileButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()

        }

        binding.continueMain.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, ContinueFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.myAccountMain.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.turkishMealMain.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, TurkishMealFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.menuListenTurkishMeal.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, TurkishMealFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.menuSearchWord.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, SearchWordFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.menuBiography.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, BiographyFragment())?.addToBackStack("")
                ?.commit()
        }
    }
}