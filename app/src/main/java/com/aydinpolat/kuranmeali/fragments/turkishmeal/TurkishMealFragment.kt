package com.aydinpolat.kuranmeali.fragments.turkishmeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.FragmentTurkishMealBinding
import com.aydinpolat.kuranmeali.fragments.continuefragment.ContinueFragment
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.turkishmeal.adapter.SuraAdapter
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class TurkishMealFragment : Fragment() {
    private lateinit var _binding: FragmentTurkishMealBinding
    private val binding get() = _binding
    private val baseViewModel : BaseViewModel by viewModels()
    private lateinit var suraAdapter: SuraAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTurkishMealBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val continueFragment = ContinueFragment()

        suraAdapter = SuraAdapter { sura, position ->
            continueFragment.suras = sura
            continueFragment.suraPosition = position
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, continueFragment)?.addToBackStack("")
                ?.commit()
        }
        binding.turkishMealLoading.visibility = View.VISIBLE
        baseViewModel.getAllSuras?.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                binding.turkishMealLoading.visibility = View.INVISIBLE
                suraAdapter.setSuraList(it)
                continueFragment.listOfSuras = it
                binding.turkishMealRecyclerview.adapter = suraAdapter
            }
        }

        binding.turkishMealBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
        }
    }
}