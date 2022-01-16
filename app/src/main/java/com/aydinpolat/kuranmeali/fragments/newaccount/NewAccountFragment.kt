package com.aydinpolat.kuranmeali.fragments.newaccount

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.activities.MainActivity
import com.aydinpolat.kuranmeali.data.models.UserMail
import com.aydinpolat.kuranmeali.databinding.FragmentNewAccountBinding
import com.aydinpolat.kuranmeali.fragments.myaccount.MyAccountFragment
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class NewAccountFragment : Fragment() {
    private lateinit var _binding: FragmentNewAccountBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newAccountRegisterButton.setOnClickListener {
            val email = binding.newAccountEmailEt.text.toString()
            baseViewModel.insertUserMail(UserMail(email))
            val sharedPreferences =
                (activity as MainActivity).getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.apply {
                putString("userMail", email)
            }.apply()

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MyAccountFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.newAccountOwnerNameMain.setOnClickListener {
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("http://www.cemalkulunkoglu.net/")
            )
            startActivity(viewIntent)
        }
    }


}