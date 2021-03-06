package com.aydinpolat.kuranmeali.fragments.continuefragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
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
import com.aydinpolat.kuranmeali.fragments.mainfragment.MainFragment
import com.aydinpolat.kuranmeali.util.*
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.firebase.storage.FirebaseStorage
import java.util.regex.Matcher
import java.util.regex.Pattern


class ContinueFragment : Fragment() {
    private lateinit var _binding: FragmentContinueBinding
    private val binding get() = _binding
    var suras: Suras? = null
    var listOfSuras: List<Suras> = emptyList()
    val baseViewModel: BaseViewModel by viewModels()
    var suraPosition: Int? = null
    private var toggleCounter = 0
    var ayatCounter = 0
    private lateinit var bkzAdapter: BkzAdapter
    private var isAyatHasBkz: Boolean = false
    private var listOfBkz: MutableList<BkzAyat> = mutableListOf()
    private lateinit var adapter: BkzAdapter
    private lateinit var messageBoxInstanceOfBkz: AlertDialog
    private var listOfSuggestion: ArrayList<String> = arrayListOf()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var player: ExoPlayer
    private lateinit var suggestionAdapter: ArrayAdapter<String>
    private lateinit var runnable: Runnable
    val handler = Handler(Looper.getMainLooper())
    var isAutoPlaying = true
    private var isAyatNoteReading = false
    private var isSuraNoteReading = false
    var ayatNoteDialog: AlertDialog? = null
    var suraNoteDialog: AlertDialog? = null
    var isAyatNoteButtonClicked: Boolean = false
    var isSuraNoteButtonClicked: Boolean = false
    var isPaused = false
    var isSuraDismissed = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContinueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        player = ExoPlayer.Builder((activity as MainActivity)).build()
        setFirstRunViews()
        setBkzAdapter()
        setButtonsClickListeners()
        setSeekBarListeners()
    }

    private fun setFirebase(item: String) {
        val storageRef = storage.reference
        storageRef.child("${item}.mp3").downloadUrl.addOnSuccessListener {
            try {
                player.stop()
                player.release()
                player = ExoPlayer.Builder((activity as MainActivity)).build()
                val mediaItem = MediaItem.Builder()
                    .setUri(it)
                    .build()
                player.setMediaItem(mediaItem, 0)
                player.prepare()
                player.play()
                playerSetListeners()
                initializeCurrentState()
            } catch (e: Exception) {
            }
        }
    }

    private fun playerSetListeners() {
        player.addListener(object : Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (!isPlaying && isPaused) {
                    handler.removeCallbacks(runnable)
                } else if (!isPlaying && !isPaused) {
                    handler.removeCallbacks(runnable)
                    initializeCurrentState()
                    binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
                    binding.continueTotalLenght.text = player.milliSecondsToTimer(player.duration)
                } else {
                    binding.continuePlayButton.setImageResource(R.drawable.ic_stop_white)
                    binding.continueTotalLenght.text = player.milliSecondsToTimer(player.duration)
                }
            }


            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    if (!isAyatNoteButtonClicked && !isSuraNoteButtonClicked) {
                        val ayat = listOfSuras[suraPosition!!].ayets[ayatCounter]
                        val lastIndexOfAyat = listOfSuras[suraPosition!!].ayets.size - 1
                        if (isAutoPlaying) {
                            if (ayatCounter == lastIndexOfAyat && !isSuraNoteReading && isAyatNoteReading) {
                                ayatCounter++
                                languageChooser()
                                if (isAyatNoteReading) {
                                    ayatNoteDialog?.dismiss()
                                    isAyatNoteReading = false
                                }
                                showSuraExplanationBox()
                                isSuraNoteReading = true
                            } else if (isSuraNoteReading) {
                                isSuraNoteReading = false
                                suraNoteDialog?.dismiss()
                                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}")
                            } else if (ayat.ayatNote.isNotEmpty() && !isAyatNoteReading) {
                                showAyatExplanationBox()
                                isAyatNoteReading = true
                            } else if (isAutoPlaying && !isAyatNoteReading && !isSuraNoteReading) {
                                ayatCounter++
                                languageChooser()
                                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}")
                            } else if (isAyatNoteReading && ayat.ayatNote.isNotEmpty()) {
                                ayatNoteDialog?.dismiss()
                                ayatCounter++
                                languageChooser()
                                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}")
                                isAyatNoteReading = false
                            }
                            if (ayatCounter > 0 && isSuraDismissed) {
                                isSuraDismissed = false
                            }
                        }
                    }
                }
            }
        })
    }

    fun initializeCurrentState() {
        runnable = object : Runnable {
            override fun run() {
                binding.continueChantCurrentProgress.max = player.duration.toInt()
                binding.continueCurrentLenght.text =
                    player.milliSecondsToTimer(player.currentPosition)
                binding.continueChantCurrentProgress.progress = player.currentPosition.toInt()
                handler.postDelayed(this, 1000)
            }

        }
        handler.postDelayed(runnable, 0)
    }

    private fun setSeekBarListeners() {
        binding.continueBottomTextSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p1 > 35) {
                    val size = (p1 / 3).toFloat()
                    binding.continueTurkishAyat.textSize = size
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.continueTopTextSeek.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p1 > 35) {
                    val size = (p1 / 3).toFloat()
                    binding.continueArabicAyat.textSize = size
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.continueChantCurrentProgress.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, position: Int, p2: Boolean) {}

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (player != null && player.isPlaying()) {
                    player.seekTo(seekBar?.progress?.toLong()!!);
                }
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFirstRunViews() {
        baseViewModel.getAllSuras?.observeOnce(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                listOfSuras = it
                val getTurkishAyatId = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId
                val arabicAyatId =
                    listOfSuras[suraPosition!!].ayetsArabic.getItemPositionByName(getTurkishAyatId)
                binding.continueSuraName.text = listOfSuras[suraPosition!!].suraName
                binding.continueTurkishAyat.text =
                    listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText
                binding.continueCounterText.text =
                    (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
                checkForIfAyatHasNote()
                checkForIfAyatHasBkz()
                setLatestSuraAndAyat()
            }
        }

    }

    private fun setBkzAdapter() {
        adapter = BkzAdapter {
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
            binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
            ayatCounter = it.ayatId
            suraPosition = it.suraId
            messageBoxInstanceOfBkz.dismiss()
            setTopInformation()
            languageChooser()
        }
    }

    private fun setButtonsClickListeners() {

        binding.continueBkzButton.setOnClickListener {
            setListOfBkzAndShow()
        }

        binding.continueAutoPlayButton.setOnClickListener {
            if (isAutoPlaying) {
                binding.continueAutoPlayButton.setImageResource(R.drawable.ic_autoplay_stop_white)
            } else {
                binding.continueAutoPlayButton.setImageResource(R.drawable.ic_autoplay_white)
            }
            isAutoPlaying = !isAutoPlaying
        }

        binding.continuePlayButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
                isPaused = true
                binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
            } else {
                if (player.mediaItemCount > 0) {
                    player.play()
                    isPaused = false
                } else {
                    val isAyatHasNote =
                        listOfSuras[suraPosition!!].ayets[ayatCounter].ayatNote.isNullOrEmpty()
                    if (ayatCounter == 0 && !isSuraNoteReading && !isSuraDismissed) {
                        showSuraExplanationBox()
                        isSuraNoteReading = true
                    } else if (isAyatHasNote && isSuraNoteReading && !isAyatNoteReading) {
                        isSuraNoteReading = false
                        isAyatNoteReading = true
                        showAyatExplanationBox()
                    } else {
                        setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}")
                    }
                }
            }
        }

        binding.continueBottomTextSizeButton.setOnClickListener {
            val view = binding.continueBottomTextSeek
            view.postDelayed({ view.visibility = View.INVISIBLE }, 5000)
            view.visibility = View.VISIBLE
        }

        binding.continueTopTextSizeButton.setOnClickListener {
            val view = binding.continueTopTextSeek
            view.postDelayed({ view.visibility = View.INVISIBLE }, 5000)
            view.visibility = View.VISIBLE
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
            binding.continueBottomTextSeek.visibility = View.INVISIBLE
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
            binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
        }

        binding.continuePreviousAyat.setOnClickListener {
            if (!listOfSuras.isNullOrEmpty()) {
                goPrevious()
            }
            binding.continueBottomTextSeek.visibility = View.INVISIBLE
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
            binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
        }

        binding.continueSuraExplanation.setOnClickListener {
            isSuraNoteButtonClicked = true
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
                ?.replace(R.id.main_container_view, MainFragment())?.addToBackStack("")
                ?.commit()
            if (player.isPlaying) {
                player.stop()
                player.release()
                player = ExoPlayer.Builder((activity as MainActivity)).build()
            }
        }

        binding.continueAyatNote.setOnClickListener {
            isAyatNoteButtonClicked = true
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
    }

    @SuppressLint("SetTextI18n")
    private fun setTopInformation() {
        binding.continueSuraName.text = Constants.suraNames[suraPosition!!]
        binding.continueCounterText.text =
            (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"
    }

    private fun searchSuraAndAyat() {
        showSearchDialog()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    private fun showSearchDialog() {
        val messageBoxView = LayoutInflater.from((activity as MainActivity))
            .inflate(R.layout.custom_search_dialog, null)
        val messageBoxBuilder = AlertDialog.Builder(activity).setView(messageBoxView)
        val searchSuraEditText =
            messageBoxView.findViewById<AutoCompleteTextView>(R.id.dialog_search_sura_edit_text)
        val searchAyatSpinner =
            messageBoxView.findViewById<NumberPicker>(R.id.choose_number_picker_bottom_sheet)
        val searchAyatEditText =
            messageBoxView.findViewById<EditText>(R.id.dialog_search_ayat_edittext)
        val dialogCloseButton =
            messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_search)
        val goAyatButton =
            messageBoxView.findViewById<FrameLayout>(R.id.dialog_search_go_ayat_button)
        val ayatSizeText = messageBoxView.findViewById<TextView>(R.id.search_ayat_size)

        val messageBoxInstance = messageBoxBuilder.show()
        searchAyatEditText.showSoftInputOnFocus = false
        disableSoftInputFromAppearing(searchAyatEditText)

        messageBoxInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        searchSuraEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listOfSuggestion = arrayListOf()
                var suraName = searchSuraEditText.text.toString().uppercase()

                suraName = "%$suraName%"
                baseViewModel.searchDatabase(suraName)
                    .observeOnce(viewLifecycleOwner) { searchResponse ->
                        if (searchResponse.isNotEmpty()) {
                            searchResponse.forEach {
                                listOfSuggestion.add((it.suraId + 1).toString() + ". " + it.suraName.uppercase() + " S??RES??")
                            }
                            suggestionAdapter = ArrayAdapter<String>(
                                (activity as MainActivity),
                                android.R.layout.simple_list_item_1,
                                listOfSuggestion.distinct()
                            )
                            searchSuraEditText.setAdapter(suggestionAdapter)
                            searchSuraEditText.showDropDown()
                        }
                    }

                baseViewModel.searchDatabase(changedSuraName(suraName))
                    .observeOnce(viewLifecycleOwner) { searchResponse ->
                        if (searchResponse.isNotEmpty()) {
                            searchResponse.forEach {
                                listOfSuggestion.add((it.suraId + 1).toString() + ". " + it.suraName.uppercase() + " S??RES??")
                            }
                            suggestionAdapter = ArrayAdapter<String>(
                                (activity as MainActivity),
                                android.R.layout.simple_list_item_1,
                                listOfSuggestion.distinct()
                            )
                            searchSuraEditText.setAdapter(suggestionAdapter)
                            searchSuraEditText.showDropDown()
                        }
                    }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        searchAyatEditText.setOnTouchListener { view, motionEvent ->
            searchAyatSpinner.visibility = View.VISIBLE
            searchAyatEditText.text = searchAyatSpinner.displayedValues[0].toEditable()
            return@setOnTouchListener true
        }

        searchAyatSpinner.setOnValueChangedListener { numberPicker, i, i2 ->
            searchAyatEditText.text = numberPicker.displayedValues[i2].toEditable()
        }

        searchSuraEditText.setOnItemClickListener { adapterView, view, i, l ->

            val mSuraPosition = searchSuraEditText.text.toString()
            if (mSuraPosition.contains(".")){
                suraPosition = searchSuraEditText.text.toString().substringBefore(".").toInt() - 1
                val sura = listOfSuras[suraPosition!!]
                ayatSizeText.text = "(" + sura.ayets.size + ")"
                val listOfSpinner: Array<String?> = arrayOfNulls(sura.ayets.size)
                var counterArray = 0
                sura.ayets.forEach {
                    listOfSpinner[counterArray] = it.ayatId
                    counterArray++
                }
                searchAyatSpinner.minValue = 0
                searchAyatSpinner.maxValue = sura.ayets.size - 1
                searchAyatSpinner.displayedValues = listOfSpinner
            }

            var suraName = "%" + searchSuraEditText.text.toString().substringAfter(" ")
                .substringBefore(" ") + "%"
            if (suraName == "%V??KI???A%") {
                suraName = "%V??K?????A%"
            }
            baseViewModel.searchDatabase(suraName).observeOnce(viewLifecycleOwner) { search ->
                if (!search.isNullOrEmpty()) {

                }
            }
            (activity as MainActivity).hideSoftKeyboard(searchSuraEditText)
        }


        goAyatButton.setOnClickListener {
            val mSuraPosition = searchSuraEditText.text.toString()
            if (searchAyatEditText.text.toString() != "" && mSuraPosition.contains(".")){
                suraPosition = searchSuraEditText.text.toString().substringBefore(".").toInt() - 1
                val ayatId = searchAyatEditText.text.toString()
                ayatCounter = listOfSuras[suraPosition!!].ayets.getItemPositionByName(ayatId)
                languageChooser()
                setTopInformation()
                messageBoxInstance.dismiss()
            }
        }

        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
    }


    fun disableSoftInputFromAppearing(editText: EditText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
            editText.setTextIsSelectable(true)
        } else {
            editText.setRawInputType(InputType.TYPE_NULL)
            editText.isFocusable = true
        }
    }

    private fun changedSuraName(suraName: String): String {
        var tempSuraName = suraName
        if (suraName.uppercase().contains("A")) {
            tempSuraName = suraName.replace("A", "??")
        }
        if (suraName.uppercase().contains("U")) {
            tempSuraName = suraName.replace("U", "??")
        }
        if (suraName.uppercase().contains("E")) {
            tempSuraName = suraName.replace("E", "??")
        }
        return tempSuraName
    }

    private fun addNoteToDatabase() {
        showListOfNoteDialog()
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
        val addNoteButton = messageBoxView.findViewById<ImageView>(R.id.dialog_bkz_add_note)
        val listOfBkz = mutableListOf<BkzAyat>()
        val sharedPreferences = (activity as MainActivity).getSharedPreferences(
            "sharedPref",
            Context.MODE_PRIVATE
        )
        val getSelectedUser =
            sharedPreferences.getString("userMail", "creatikbilisim.com")

        addNoteButton.visibility = View.VISIBLE

        bkzAdapter = BkzAdapter {
            showSelectedUserNote(it, getSelectedUser)
        }
        baseViewModel.getAllNote?.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                listOfBkz.clear()
                val temptList = it.filter { user ->
                    user.userMail == getSelectedUser
                }
                temptList.forEach { userNote ->
                    listOfBkz.add(
                        BkzAyat(
                            userNote.suraId,
                            userNote.ayatId,
                            true,
                            userNote.userNote
                        )
                    )
                }
                customTitle.text = "Notlar??m"
                customDescription.text = temptList.size.toString() + " ayet i??in not olu??turdunuz"
                bkzAdapter.setList(listOfBkz)
                recyclerView.adapter = bkzAdapter
            }
        }

        addNoteButton.setOnClickListener {
            showNoteDialog()
        }

        val messageBoxInstance = messageBoxBuilder.show()
        messageBoxInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        val dialogDescription =
            messageBoxView.findViewById<TextView>(R.id.dialog_to_choose_sure_or_ayat_text)
        val dialogNote = messageBoxView.findViewById<TextView>(R.id.dialog_sura_explanation)
        dialogTitle.text = Constants.suraNames[bkzAyat.suraId] + " Suresi"
        dialogDescription.text = (bkzAyat.ayatId + 1).toString() + ". Ayet"

        dialogNote.text = bkzAyat.ayatNote

        val messageBoxInstance = messageBoxBuilder.show()
        messageBoxInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
        }
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
        messageBoxInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogAddNoteButton.setOnClickListener {
            val sharedPreferences =
                (activity as MainActivity).getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            val getSelectedUser = sharedPreferences.getString("userMail", "creatikbilisim.com")
            val note = noteEditText.text
            baseViewModel.insertNote(
                UserNote(
                    getSelectedUser!!,
                    note.toString(),
                    ayatCounter,
                    suraPosition!!
                )
            )
            Toast.makeText(requireContext(), "Not Eklendi", Toast.LENGTH_SHORT).show()
            messageBoxInstance.dismiss()
        }

        dialogCloseButton.setOnClickListener {
            messageBoxInstance.dismiss()
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
        }
    }

    @SuppressLint("SetTextI18n")
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
        val ayatPlayButton = messageBoxView.findViewById<ImageView>(R.id.dialog_play_button)
        var mIsPaused = false

        if (!isSuraNoteButtonClicked) {
            setFirebase("${suraPosition}/${suraPosition}-a")
            ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
        }

        ayatPlayButton.setOnClickListener {
            if (!player.isPlaying && !mIsPaused) {
                setFirebase("${suraPosition}/${suraPosition}-a")
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            } else if (player.isPlaying) {
                player.pause()
                mIsPaused = true
                isPaused = true
                ayatPlayButton.setImageResource(R.drawable.ic_play_black)
            } else if (mIsPaused) {
                player.play()
                mIsPaused = false
                isPaused = false
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            }
        }

        suraNameView.text = Constants.suraNames[suraPosition!!]
        suraExplanationView.text = listOfSuras[suraPosition!!].suraNote
        suraOrAyat.text = "Sure Hakk??nda A????klama"
        suraNoteDialog = messageBoxBuilder.show()
        suraNoteDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCloseButton.setOnClickListener {
            suraNoteDialog!!.dismiss()
        }
        suraNoteDialog!!.setOnDismissListener {
            isSuraDismissed = true
            isSuraNoteButtonClicked = false
            isSuraNoteReading = false
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
            binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun languageChooser() {
        binding.continueChantCurrentProgress.progress = 0
        binding.continueCurrentLenght.text = "0:00"
        binding.continueTotalLenght.text = "0:00"
        binding.continueBottomTextSeek.visibility = View.INVISIBLE
        isSuraDismissed = false
        when (toggleCounter) {
            0 -> {
                setBothTurkishAndArabic()
                setExtentedViewToNormal()
            }
            1 -> {
                setOnlyTurkish()
                extendAyatEditText()
            }
            2 -> {
                setOnlyArabic()
                extendAyatEditText()
            }
        }
        checkForIfAyatHasNote()
        checkForIfAyatHasBkz()
        setLatestSuraAndAyat()
    }

    private fun extendAyatEditText() {
        val params = binding.continueArabicScrollview.layoutParams
        params.height = resources.getDimension(R.dimen.edittext_height).toInt()
        binding.continueArabicScrollview.layoutParams = params
    }

    private fun setExtentedViewToNormal() {
        val params = binding.continueArabicScrollview.layoutParams
        params.height = resources.getDimension(R.dimen.edittext_normal_height).toInt()
        binding.continueArabicScrollview.layoutParams = params
    }

    private fun setLatestSuraAndAyat() {
        val sharedPreferences =
            (activity as MainActivity).getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putInt("SuraId", suraPosition!!)
            putInt("AyatId", ayatCounter)
        }.apply()
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

        if (isAyatHasBkz) {
            binding.continueBkzButton.visibility = View.VISIBLE
        } else {
            binding.continueBkzButton.visibility = View.INVISIBLE
        }
    }

    private fun setListOfBkzAndShow() {
        listOfBkz = mutableListOf()
        listOfSuras[suraPosition!!].ayets[ayatCounter].bkz.split(" ").forEach {
            val letter: Pattern = Pattern.compile("[a-zA-z]")
            val hasLetter: Matcher = letter.matcher(it)
            if (it != "" && !hasLetter.find()) {
                listOfBkz.add(
                    BkzAyat(
                        it.substringBefore("/").toInt() - 1,
                        it.substringAfter("/").toInt() - 1,
                        false,
                        ""
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
        val dialogAddNote = messageBoxView.findViewById<ImageView>(R.id.dialog_bkz_add_note)
        val dialogCloseButton = messageBoxView.findViewById<ImageView>(R.id.dialog_close_button_bkz)

        ayatCountertext.text = "Dinledi??iniz ayetle ilgili " + listOfBkz.size + " ayet daha bulundu"
        adapter.setList(listOfBkz)
        recyclerView.adapter = adapter
        dialogAddNote.visibility = View.INVISIBLE
        messageBoxInstanceOfBkz = messageBoxBuilder.show()
        messageBoxInstanceOfBkz.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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

        val ayatPlayButton = messageBoxView.findViewById<ImageView>(R.id.dialog_play_button)
        var mIsPaused = false

        val ayatObject = listOfSuras[suraPosition!!].ayets[ayatCounter]
        var ayatId = ayatObject.ayatId.substringBefore("-").toInt() - 1
        ayatPlayButton.setOnClickListener {
            if (!player.isPlaying && !mIsPaused) {
                if (suraPosition != 0 && ayatObject.ayatId.contains((ayatId + 1).toString() + "-" + (ayatId + 2).toString() + "-")) {
                    ayatId += 2
                    setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                    ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
                } else if (suraPosition != 0 && ayatObject.ayatId.contains((ayatId + 1).toString() + "-")) {
                    ayatId += 1
                    setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                    ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
                } else {
                    setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                    ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
                }
            } else if (player.isPlaying) {
                player.pause()
                mIsPaused = true
                isPaused = false
                ayatPlayButton.setImageResource(R.drawable.ic_play_black)
            } else if (mIsPaused) {
                player.play()
                mIsPaused = false
                isPaused = true
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            }
        }

        if (!isAyatNoteButtonClicked) {
            if (suraPosition != 0 && ayatObject.ayatId.contains((ayatId + 1).toString() + "-" + (ayatId + 2).toString() + "-")) {
                ayatId += 2
                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            } else if (suraPosition != 0 && ayatObject.ayatId.contains((ayatId + 1).toString() + "-")) {
                ayatId += 1
                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            } else {
                setFirebase("${suraPosition}/${suraPosition}-${ayatCounter}c")
                ayatPlayButton.setImageResource(R.drawable.ic_stop_black)
            }
        }

        ayatNameView.text =
            Constants.suraNames[suraPosition!!] + " " + listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". Ayet"
        ayatExplanationView.text = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatNote
        suraOrAyat.text = "Cemal K??l??nko??lu'nun yorumu"
        ayatNoteDialog = messageBoxBuilder.show()
        ayatNoteDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCloseButton.setOnClickListener {
            ayatNoteDialog?.dismiss()
        }

        ayatNoteDialog!!.setOnDismissListener {
            isAyatNoteButtonClicked = false
            isAyatNoteReading = false
            player.stop()
            player.release()
            player = ExoPlayer.Builder((activity as MainActivity)).build()
            binding.continuePlayButton.setImageResource(R.drawable.ic_play_white)
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
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". " +
                        listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText

            binding.continueTopLanguage.text = "T??rk??e"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            binding.continueArabicAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". " +
                        listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText

            binding.continueArabicAyat.gravity = Gravity.START
            binding.continueTopLanguage.text = "T??rk??e"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setArabicTextOnly() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            val getTurkishAyatId = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId
            val arabicAyatId =
                listOfSuras[suraPosition!!].ayetsArabic.getItemPositionByName(getTurkishAyatId)
            if (listOfSuras[suraPosition!!].ayetsArabic.size > arabicAyatId) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText
                binding.continueArabicAyat.gravity = Gravity.END
            }

            binding.continueTopLanguage.text = "Arap??a"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            val getTurkishAyatId = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId
            val arabicAyatId =
                listOfSuras[suraPosition!!].ayetsArabic.getItemPositionByName(getTurkishAyatId)
            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            if (listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText.isNotEmpty()) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText
            }

            binding.continueTopLanguage.text = "Arap??a"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBothTurkishAndArabicText() {
        if (listOfSuras[suraPosition!!].ayets.size > ayatCounter) {
            val getTurkishAyatId = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId
            val arabicAyatId =
                listOfSuras[suraPosition!!].ayetsArabic.getItemPositionByName(getTurkishAyatId)

            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". " +
                        listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText

            if (listOfSuras[suraPosition!!].ayetsArabic.size > arabicAyatId) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText
            }

            binding.continueTopLanguage.text = "Arap??a"
            binding.continueBottomLanguage.text = "T??rk??e"
        } else {
            suraPosition = suraPosition!! + 1
            ayatCounter = 0
            val getTurkishAyatId = listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId
            val arabicAyatId =
                listOfSuras[suraPosition!!].ayetsArabic.getItemPositionByName(getTurkishAyatId)

            binding.continueSuraName.text = Constants.suraNames[suraPosition!!]

            binding.continueCounterText.text =
                (listOfSuras[suraPosition!!].suraId.plus(1)).toString() + "/114"

            binding.continueTurkishAyat.text =
                listOfSuras[suraPosition!!].ayets[ayatCounter].ayatId + ". " +
                        listOfSuras[suraPosition!!].ayets[ayatCounter].ayatText
            if (listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText.isNotEmpty()) {
                binding.continueArabicAyat.text =
                    listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatId + ". " +
                            listOfSuras[suraPosition!!].ayetsArabic[arabicAyatId].ayatText
            }

            binding.continueTopLanguage.text = "Arap??a"
            binding.continueBottomLanguage.text = "T??rk??e"
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
        val sharetext = "ARAP??A\n" + sura.ayetsArabic[ayatCounter].ayatId + ". " + arabicAyat +
                "\nT??RK??E MEAL??\n" + sura.ayetsArabic[ayatCounter].ayatId + ". " + turkishAyat +
                "\n(" + sura.suraName + " " + sura.ayets[ayatCounter].ayatId + ". Ayet)" +
                "\ncemalkulunkoglu.net"
        intent.putExtra(Intent.EXTRA_TEXT, sharetext)
        startActivity(Intent.createChooser(intent, "Ayeti Payla??"))
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
        player = ExoPlayer.Builder((activity as MainActivity)).build()
    }
}
