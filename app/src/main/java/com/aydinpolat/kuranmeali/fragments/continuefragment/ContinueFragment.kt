package com.aydinpolat.kuranmeali.fragments.continuefragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.activities.MainActivity
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.BkzAyat
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.models.UserNote
import com.aydinpolat.kuranmeali.databinding.FragmentContinueBinding
import com.aydinpolat.kuranmeali.fragments.continuefragment.adapter.BkzAdapter
import com.aydinpolat.kuranmeali.fragments.turkishmeal.TurkishMealFragment
import com.aydinpolat.kuranmeali.util.observeOnce
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel

class ContinueFragment : Fragment() {
    private lateinit var _binding: FragmentContinueBinding
    private val binding get() = _binding
    var suras: Suras? = null
    var listOfSuras: List<Suras> = emptyList()
    val baseViewModel: BaseViewModel by viewModels()
    var suraPosition: Int? = null
    private var toggleCounter = 0
    private var ayatCounter = 0
    private var isAyatHasBkz: Boolean = false
    private var listOfBkz: MutableList<BkzAyat> = mutableListOf()
    private lateinit var adapter: BkzAdapter
    private lateinit var messageBoxInstanceOfBkz: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContinueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BkzAdapter {
            ayatCounter = it.ayatId
            suraPosition = it.suraId
            messageBoxInstanceOfBkz.dismiss()
            setTopInformation()
            languageChooser()
        }

        binding.continueToggleButton.setOnClickListener {
            toggleCounter++
            if (toggleCounter > 2) {
                toggleCounter = 0
            }
            languageChooser()
        }

        binding.continueNextAyat.setOnClickListener {
            if (!listOfSuras.isNullOrEmpty()) {
                ayatCounter++
                languageChooser()
            }
        }

        binding.continuePreviousAyat.setOnClickListener {
            if (!listOfSuras.isNullOrEmpty()) {
                goPrevious()
            }
        }

        binding.continueSuraExplanation.setOnClickListener {
            showSuraExplanationBox()
        }

        binding.continueSearchSuraAndAyat.setOnClickListener {
            searchSuraAndAyat()
        }

        binding.continueAddNote.setOnClickListener {
            addNoteToDatabase()
        }

        binding.continueBackPress.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_container_view, TurkishMealFragment())?.addToBackStack("")
                ?.commit()
        }

        binding.continueAyatNote.setOnClickListener {
            showAyatExplanationBox()
        }

        binding.continueTurkishAyat.setOnClickListener {
            if (isAyatHasBkz) {
                setListOfBkzAndShow()
            }
        }

        binding.continueShareAyat.setOnClickListener {
            startShareIntent(listOfSuras[suraPosition!!])
        }

        binding.continueSuraName.text = suras?.suraName
        binding.continueTurkishAyat.text = suras?.ayets?.get(0)?.ayatText
        binding.continueArabicAyat.text =
            listOfSuras[suraPosition!!].ayetsArabic[ayatCounter].ayatText
        binding.continueCounterText.text =
            (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
    }

    private fun setTopInformation() {
        binding.continueSuraName.text = Constants.suraNames[suraPosition!!]
        binding.continueCounterText.text =
            (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
    }

    private fun searchSuraAndAyat() {
        showSearchDialog()
    }

    private fun showSearchDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_search_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val searchSuraEditText =
            messageBoxView.findViewById<EditText>(R.id.dialog_search_sura_edit_text)
        val searchAyatEditText =
            messageBoxView.findViewById<EditText>(R.id.dialog_search_ayat_edittext)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_bkz)
        val goAyatButton =
            messageBoxView.findViewById<FrameLayout>(R.id.dialog_search_go_ayat_button)

        val messageBoxInstance = messageBoxBuilder.show()


        goAyatButton.setOnClickListener {
            var suraName = searchSuraEditText.text.toString()
            suraName = "%$suraName%"
            var ayatId = searchAyatEditText.text.toString()
            if (ayatId == ""){
                Toast.makeText(
                    requireContext(),
                    "Lütfen Ayet Numarasını Giriniz",
                    Toast.LENGTH_SHORT
                ).show()
            }else if (!ayatId.contains(" ")){
                baseViewModel.searchDatabase(suraName).observeOnce(viewLifecycleOwner){ searchResponse ->
                    if (searchResponse.isNotEmpty()){
                        suraPosition = searchResponse[0].suraId
                        if (searchResponse[0].ayets.size > (ayatId.toInt() - 1) && ((ayatId.toInt() - 1)  >= 0) ){
                            ayatCounter = ayatId.toInt() - 1
                            languageChooser()
                            setTopInformation()
                            messageBoxInstance.dismiss()
                        }else{
                            Toast.makeText(requireContext(), "Ayet Bulunamadı", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }else{
                        Toast.makeText(requireContext(), "Sure Bulunamadı", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Yanlış Karakter Girdiniz", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialogCloseButton.setOnClickListener {
            messageBoxInstanceOfBkz.dismiss()
        }
    }

    private fun addNoteToDatabase() {
        showNoteDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun showNoteDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_add_note_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val suraNameView =
            messageBoxView.findViewById<TextView>(R.id.dialog_add_note_sura_and_ayat_name)
        val noteEditText = messageBoxView.findViewById<EditText>(R.id.dialog_add_note_edit_text)
        val dialogCloseButton =
            messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_add_note)
        val dialogAddNoteButton =
            messageBoxView.findViewById<FrameLayout>(R.id.dialog_add_note_button)

        suraNameView.text =
            Constants.suraNames[suraPosition!!] + " Suresi " + (ayatCounter + 1) + ". Ayet"
        val messageBoxInstance = messageBoxBuilder.show()

        dialogAddNoteButton.setOnClickListener {
            val note = noteEditText.text
            baseViewModel.insertNote(UserNote("", note.toString(), ayatCounter, suraPosition!!))
            Toast.makeText(requireContext(), "Not Eklendi", Toast.LENGTH_SHORT).show()
            messageBoxInstance.dismiss()
        }

        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }

    private fun showSuraExplanationBox() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_sura_explanation_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val suraNameView = messageBoxView.findViewById<TextView>(R.id.dialog_sura_name)
        val suraExplanationView =
            messageBoxView.findViewById<TextView>(R.id.dialog_sura_explanation)
        val suraOrAyat =
            messageBoxView.findViewById<TextView>(R.id.dialog_to_choose_sure_or_ayat_text)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button)

        suraNameView.text = Constants.suraNames[suraPosition!!]
        suraExplanationView.text = listOfSuras[suraPosition!!].suraNote
        suraOrAyat.text = "Sure Hakkında Açıklama"
        val messageBoxInstance = messageBoxBuilder.show()
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }


    private fun languageChooser() {
        when (toggleCounter) {
            0 -> {
                setBothTurkishAndArabic()
            }
            1 -> {
                setOnlyTurkish()
            }
            2 -> {
                setOnlyArabic()
            }
        }
        checkForIfAyatHasNote()
        checkForIfAyatHasBkz()
    }

    private fun checkForIfAyatHasNote() {
        if (listOfSuras[suraPosition!!].ayets[ayatCounter].ayatNote == "") {
            binding.continueAyatNote.visibility = View.INVISIBLE
        } else {
            binding.continueAyatNote.visibility = View.VISIBLE
        }
    }

    private fun checkForIfAyatHasBkz() {
        val ayat = listOfSuras[suraPosition!!].ayets[ayatCounter]
        isAyatHasBkz = ayat.bkz != "" || ayat.ayatText.contains("Bkz")
    }

    private fun setListOfBkzAndShow() {
        listOfBkz = mutableListOf()
        listOfSuras[suraPosition!!].ayets[ayatCounter].bkz.split(" ").forEach {
            if (it != "") {
                listOfBkz.add(
                    BkzAyat(
                        it.substringBefore("/").toInt() - 1,
                        it.substringAfter("/").toInt() - 1
                    )
                )
            }
        }

        showBkzDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun showBkzDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_bkz_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val ayatCountertext =
            messageBoxView.findViewById<TextView>(R.id.dialog_to_show_how_many_ayat_found)
        val recyclerView = messageBoxView.findViewById<RecyclerView>(R.id.dialog_bkz_recyclerView)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_bkz)

        ayatCountertext.text = "Dinlediğiniz ayetle ilgili " + listOfBkz.size + " ayet daha bulundu"
        adapter.setList(listOfBkz)
        recyclerView.adapter = adapter

        messageBoxInstanceOfBkz = messageBoxBuilder.show()
        dialogCloseButton.setOnClickListener {
            messageBoxInstanceOfBkz.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAyatExplanationBox() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_sura_explanation_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val ayatNameView = messageBoxView.findViewById<TextView>(R.id.dialog_sura_name)
        val ayatExplanationView =
            messageBoxView.findViewById<TextView>(R.id.dialog_sura_explanation)
        val suraOrAyat =
            messageBoxView.findViewById<TextView>(R.id.dialog_to_choose_sure_or_ayat_text)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button)

        ayatNameView.text = Constants.suraNames[suraPosition!!] + (ayatCounter + 1) + " " + ". Ayet"
        ayatExplanationView.text = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatNote
        suraOrAyat.text = "Cemal Külünkoğlu'nun yorumu"
        val messageBoxInstance = messageBoxBuilder.show()
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }

    private fun setOnlyTurkish() {
        binding.continueBottomCard.visibility = View.INVISIBLE
        binding.continueBottomScroll.visibility = View.INVISIBLE
        binding.continueBottomLanguage.visibility = View.INVISIBLE
        binding.continueBottomTextSeek.visibility = View.INVISIBLE
        binding.continueBottomTextSizeButton.visibility = View.INVISIBLE
        setTurkishTextOnly()
    }

    private fun setOnlyArabic() {
        setArabicTextOnly()
    }

    private fun setBothTurkishAndArabic() {
        binding.continueBottomCard.visibility = View.VISIBLE
        binding.continueBottomScroll.visibility = View.VISIBLE
        binding.continueBottomLanguage.visibility = View.VISIBLE
        binding.continueBottomTextSeek.visibility = View.VISIBLE
        binding.continueBottomTextSizeButton.visibility = View.VISIBLE
        setBothTurkishAndArabicText()
    }

    @SuppressLint("SetTextI18n")
    private fun setTurkishTextOnly() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            binding.continueArabicAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText

            binding.continueTopLanguage.text = "Türkçe"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            binding.continueArabicAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText
            binding.continueTopLanguage.text = "Türkçe"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setArabicTextOnly() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            if (listOfSuras[suraPosition!!].ayetsArabic.size > ayatCounter) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[ayatCounter].ayatText
            }

            binding.continueTopLanguage.text = "Arapça"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            if (listOfSuras[suraPosition!!].ayetsArabic[ayatCounter].ayatText.isNotEmpty()) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic.filter { it.ayatId == listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId }[0].ayatText
            }

            binding.continueTopLanguage.text = "Arapça"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBothTurkishAndArabicText() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets.get(ayatCounter).ayatText

            if (listOfSuras[suraPosition!!].ayetsArabic.size > ayatCounter) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[ayatCounter].ayatText
            }

            binding.continueTopLanguage.text = "Arapça"
            binding.continueBottomLanguage.text = "Türkçe"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText
            if (listOfSuras[suraPosition!!].ayetsArabic[ayatCounter].ayatText.isNotEmpty()) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic.filter { it.ayatId == listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId }[0].ayatText
            }

            binding.continueTopLanguage.text = "Arapça"
            binding.continueBottomLanguage.text = "Türkçe"
        }
    }

    @SuppressLint("SetTextI18n")
    fun goPrevious() {
        if (!(suraPosition == 0 && ayatCounter == 0)) {
            if (ayatCounter == 0) {
                suraPosition = suraPosition!! - 1
                ayatCounter = listOfSuras[suraPosition!!].ayets.size - 1
            } else {
                ayatCounter -= 1
            }
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]
            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
            languageChooser()
        }
    }

    private fun startShareIntent(sura: Suras) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val arabicAyat = sura.ayetsArabic[ayatCounter].ayatText
        val turkishAyat = sura.ayets[ayatCounter].ayatText
        val sharetext = "ARAPÇA\n" + sura.ayetsArabic[ayatCounter].ayatId + ". " + arabicAyat +
                "\nTÜRKÇE MEALİ\n" + sura.ayetsArabic[ayatCounter].ayatId + ". " + turkishAyat +
                "\n(" + sura.suraName + " " + sura.ayets[ayatCounter].ayatId + ". Ayet)" +
                "\ncemalkulunkoglu.net"
        intent.putExtra(Intent.EXTRA_TEXT, sharetext)
        startActivity(Intent.createChooser(intent, "Ayeti Paylaş"))
    }
}
