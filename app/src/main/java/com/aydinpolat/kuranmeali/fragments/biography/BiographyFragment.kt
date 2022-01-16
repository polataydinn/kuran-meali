package com.aydinpolat.kuranmeali.fragments.biography

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.FragmentBiographyBinding
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment
import android.content.Intent
import android.net.Uri


class BiographyFragment : Fragment() {
    private lateinit var _binding: FragmentBiographyBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBiographyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.biographyCemalKulunkoglu.text = getString(R.string.cemal_kulunkoglu_biography)
        binding.biographyCemalKulunkoglu.textSize = 14f

        binding.biograpyhBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.biograpyProfile.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.biographyOwnerNameBiography.setOnClickListener {
            val viewIntent = Intent("android.intent.action.VIEW",
                Uri.parse("http://www.cemalkulunkoglu.net/"))
            startActivity(viewIntent)
        }
    }
}