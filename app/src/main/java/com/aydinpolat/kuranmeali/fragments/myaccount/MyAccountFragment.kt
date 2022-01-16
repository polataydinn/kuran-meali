package com.aydinpolat.kuranmeali.fragments.myaccount

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.activities.MainActivity
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.BkzAyat
import com.aydinpolat.kuranmeali.databinding.FragmentMyAccountBinding
import com.aydinpolat.kuranmeali.fragments.continuefragment.adapter.BkzAdapter
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.fragments.newaccount.NewAccountFragment
import com.aydinpolat.kuranmeali.util.observeOnce
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class MyAccountFragment : Fragment() {
    private lateinit var _binding: FragmentMyAccountBinding
    private val binding get() = _binding
    private val baseViewModel: BaseViewModel by viewModels()
    var listOfUsers = mutableListOf<String>()
    private lateinit var bkzAdapter: BkzAdapter
    var isFirstlaunched = 0
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

        binding.myAccountMail.text = getSelectedUser

        binding.createNewAccount.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, NewAccountFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.myAccountsOwnerNameMain.setOnClickListener {
            val viewIntent = Intent("android.intent.action.VIEW",
                Uri.parse("http://www.cemalkulunkoglu.net/"))
            startActivity(viewIntent)
        }

        baseViewModel.getAllUserMails.observeOnce(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                it.forEach { user ->
                    listOfUsers.add(user.userEmail)
                }
                val adapter = ArrayAdapter(
                    (activity as MainActivity),
                    android.R.layout.simple_spinner_item, listOfUsers
                )
                binding.myAccountMailChooserSpinner.adapter = adapter
            }
        }

        binding.myAccountMailChooserSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (isFirstlaunched > 0) {
                        val email = listOfUsers[position]
                        val sharedPreferences = (activity as MainActivity).getSharedPreferences(
                            "sharedPref",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPreferences.edit()
                        editor.apply {
                            putString("userMail", email)
                        }.apply()

                        val getSelectedUser =
                            sharedPreferences.getString("userMail", "creatikbilisim.com")
                        binding.myAccountMail.text = getSelectedUser
                    }
                    isFirstlaunched++
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

        binding.myAccountBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.myAccountShowListButton.setOnClickListener {
            showListOfNoteDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showListOfNoteDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_bkz_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val customTitle = messageBoxView.findViewById<TextView>(R.id.title_of_custom_bkz)
        val customDescription =
            messageBoxView.findViewById<TextView>(R.id.dialog_to_show_how_many_ayat_found)
        val recyclerView = messageBoxView.findViewById<RecyclerView>(R.id.dialog_bkz_recyclerView)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_bkz)
        val listOfBkz = mutableListOf<BkzAyat>()
        val sharedPreferences = (activity as MainActivity).getSharedPreferences(
            "sharedPref",
            Context.MODE_PRIVATE
        )
        val getSelectedUser =
            sharedPreferences.getString("userMail", "creatikbilisim.com")
        binding.myAccountMail.text = getSelectedUser

        bkzAdapter = BkzAdapter {
            showSelectedUserNote(it, getSelectedUser)
        }
        baseViewModel.getAllNote?.observeOnce(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                it.filter { user ->
                    user.userMail == getSelectedUser
                }.forEach { userNote ->
                    listOfBkz.add(BkzAyat(userNote.suraId, userNote.ayatId, true))
                }
                customTitle.text = "Notlarım"
                customDescription.text = it.size.toString() + " ayet için not oluşturdunuz"
                bkzAdapter.setList(listOfBkz)
                recyclerView.adapter = bkzAdapter
            }
        }

        val messageBoxInstance = messageBoxBuilder.show()
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSelectedUserNote(bkzAyat: BkzAyat, getSelectedUser: String?) {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_sura_explanation_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button)
        val dialogTitle = messageBoxView.findViewById<TextView>(R.id.dialog_sura_name)
        val dialogDescription = messageBoxView.findViewById<TextView>(R.id.dialog_to_choose_sure_or_ayat_text)
        val dialogNote = messageBoxView.findViewById<TextView>(R.id.dialog_sura_explanation)
        dialogTitle.text = Constants.suraNames[bkzAyat.suraId] + " Suresi"
        dialogDescription.text = (bkzAyat.ayatId + 1).toString() + ". Ayet"

        baseViewModel.getAllNote?.observeOnce(viewLifecycleOwner){
            if (!it.isNullOrEmpty()){
                val selectedNote = it.filter { user ->
                    user.userMail == getSelectedUser && user.ayatId == bkzAyat.ayatId && user.suraId == bkzAyat.suraId
                }

                dialogNote.text = selectedNote[0].userNote
            }
        }

        val messageBoxInstance = messageBoxBuilder.show()
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }
}