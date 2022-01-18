package com.aydinpolat.kuranmeali.fragments.myaccount

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.activities.MainActivity
import com.aydinpolat.kuranmeali.data.models.UserMail
import com.aydinpolat.kuranmeali.databinding.FragmentMyAccountBinding
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class MyAccountFragment : Fragment() {
    private lateinit var _binding: FragmentMyAccountBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    var listOfUsers = mutableListOf<String>()
    var listOfUsersObject = mutableListOf<UserMail>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            (activity as MainActivity).getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val getSelectedUser = sharedPreferences.getString("userMail", "creatikbilisim.com")

        binding.myAccountAddNewUser.setOnClickListener {
            addNewUserDialog()
        }

        baseViewModel.getAllUserMails.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val getSelectedUserTemp = sharedPreferences.getString("userMail", "creatikbilisim.com")

                listOfUsersObject.clear()
                listOfUsers.clear()
                it.forEach { user ->
                    listOfUsers.add(user.userEmail)
                    listOfUsersObject.add(user)
                }

                val user = it.filter { user ->
                    user.userEmail == getSelectedUserTemp
                }
                if (!user.isNullOrEmpty()) {
                    binding.myAccountNameEt.setText(user[0].userName)
                    binding.myAccountSurnameEt.setText(user[0].userSurname)
                }
            }
        }

        binding.myAccountBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.myAccountChangeInformationButton.setOnClickListener {
            val getUser = sharedPreferences.getString("userMail", "creatikbilisim.com")
            val name = binding.myAccountNameEt.text.toString()
            val surName = binding.myAccountSurnameEt.text.toString()
            val user = UserMail(getUser!!, name, surName)
            baseViewModel.insertUserMail(user)
            Toast.makeText(requireContext(), "Başarıyla Güncellendi", Toast.LENGTH_SHORT).show()
        }

        binding.myAccountChangeUserButton.setOnClickListener {
            val popUp = PopupMenu((activity as MainActivity), it)
            listOfUsers.forEach { user ->
                popUp.menu.add(user)
            }
            popUp.setOnMenuItemClickListener {
                val editor = sharedPreferences.edit()
                editor.apply {
                    putString("userMail", it.title.toString())
                }.apply()
                val tempUser = listOfUsersObject.filter { user ->
                    user.userEmail == it.title
                }
                if (!tempUser.isNullOrEmpty()){
                    binding.myAccountNameEt.setText(tempUser[0].userName)
                    binding.myAccountSurnameEt.setText(tempUser[0].userSurname)
                }
                true
            }
            popUp.show()
        }
    }

    private fun addNewUserDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_add_new_user_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.new_user_close_button)
        val nameText = messageBoxView.findViewById<EditText>(R.id.new_user_name_et)
        val surnameText =
            messageBoxView.findViewById<EditText>(R.id.new_user_surname_et)
        val emailText =
            messageBoxView.findViewById<EditText>(R.id.new_user_email_et)
        val signUpButton = messageBoxView.findViewById<FrameLayout>(R.id.new_user_button)
        val messageBoxInstance = messageBoxBuilder.show()

        signUpButton.setOnClickListener {
            if (nameText.text.toString() != "" && surnameText.text.toString() != "" && emailText.text.toString() != "") {
                baseViewModel.insertUserMail(
                    UserMail(
                        emailText.text.toString(),
                        nameText.text.toString(),
                        surnameText.text.toString()
                    )
                )
                binding.myAccountSurnameEt.setText(surnameText.text.toString())
                binding.myAccountNameEt.setText(nameText.text.toString())

                Toast.makeText(
                    requireContext(),
                    "Kullanıcı Başarıyla Kaydedildi",
                    Toast.LENGTH_SHORT
                ).show()

                val sharedPreferences =
                    (activity as MainActivity).getSharedPreferences(
                        "sharedPref",
                        Context.MODE_PRIVATE
                    )
                val editor = sharedPreferences.edit()
                editor.apply {
                    putString("userMail", emailText.text.toString())
                }.apply()

                messageBoxInstance.dismiss()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Lütfen Boş Alanları Doldurunuz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        messageBoxInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }


}