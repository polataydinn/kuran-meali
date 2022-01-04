package com.aydinpolat.kuranmeali.fragments.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.FragmentMainBinding
import com.aydinpolat.kuranmeali.fragments.searchword.SearchWordFragment

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

        binding.searchWordMain.setOnClickListener {
            val searchFragment = SearchWordFragment()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, searchFragment)?.addToBackStack("")?.commit()
        }
    }
}