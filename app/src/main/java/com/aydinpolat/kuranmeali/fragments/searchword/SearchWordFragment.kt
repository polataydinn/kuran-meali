package com.aydinpolat.kuranmeali.fragments.searchword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.FragmentSearchWordBinding
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment

class SearchWordFragment : Fragment() {
    private lateinit var _binding: FragmentSearchWordBinding
    private  val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchWordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }
}