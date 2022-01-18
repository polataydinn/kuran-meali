package com.aydinpolat.kuranmeali.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.constants.Constants
import com.aydinpolat.kuranmeali.data.models.Ayats
import com.aydinpolat.kuranmeali.data.models.Suras
import com.aydinpolat.kuranmeali.data.models.UserMail
import com.aydinpolat.kuranmeali.databinding.ActivityMainBinding
import com.aydinpolat.kuranmeali.viewmodels.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val listOfSuras = mutableListOf<Suras>()
    val baseViewModel: BaseViewModel by viewModels()
    var listOfAyatsArabic = mutableListOf<Ayats>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContentView(binding.root)
        blockTheDarkMode()
        setUserEmailIfEmpty()
        lifecycleScope.launch(Dispatchers.IO) {
            setAyetsToRoom()
        }
    }

    private fun setUserEmailIfEmpty() {
        baseViewModel.getAllUserMails.observe(this){
            if (it.isNullOrEmpty()){
                baseViewModel.insertUserMail(UserMail("creatikbilisim.com","creatik", "bilisim"))
            }
        }
    }

    private fun blockTheDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setAyetsToRoom() {
        if (baseViewModel.getAllSuras?.value == null) {
            var string: String? = ""
            val stringBuilder = StringBuilder()
            val inputStream: InputStream = this.resources.openRawResource(R.raw.turkce)
            val reader = BufferedReader(InputStreamReader(inputStream))
            while (true) {
                try {
                    if (reader.readLine().also { string = it } == null) break
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                stringBuilder.append(string).append("\n")
            }
            var suraCounter = 0
            var ayatsCounter = 0
            var listOfAyats = mutableListOf<Ayats>()
            stringBuilder.lines().forEach { sura ->
                if (sura == ",") {
                    listOfSuras.add(
                        suraCounter,
                        Suras(
                            Constants.suraNames[suraCounter],
                            suraCounter,
                            "",
                            listOfAyats,
                            listOfAyatsArabic
                        )
                    )
                    suraCounter++
                    listOfAyats = mutableListOf()
                    ayatsCounter = 0
                } else {

                    if (sura.startsWith("a") && sura.contains("Bkz")) {
                        val bkz = sura.substringAfter("Bkz.")
                        if (sura.substringBefore(".").contains("#")) {
                            val ayatsNumber =
                                sura.substringBefore(".").replace("#", "-").removePrefix("a")
                            listOfAyats.add(
                                ayatsCounter,
                                Ayats(
                                    sura.substringAfter("."),
                                    Constants.suraNames[suraCounter],
                                    ayatsNumber,
                                    bkz,
                                    ""
                                )
                            )
                        } else {
                            val ayatNumber = sura.substringBefore(".").removePrefix("a").toInt()
                            listOfAyats.add(
                                ayatsCounter,
                                Ayats(
                                    sura.substringAfter("."),
                                    Constants.suraNames[suraCounter],
                                    ayatNumber.toString(),
                                    bkz,
                                    ""
                                )
                            )
                        }
                        ayatsCounter++
                    } else if (sura.substringBefore(".").contains("#") && !sura.contains("Bkz")) {
                        val ayatsNumber =
                            sura.substringBefore(".").replace("#", "-").removePrefix("a")
                        listOfAyats.add(
                            ayatsCounter,
                            Ayats(
                                sura.substringAfter("."),
                                Constants.suraNames[suraCounter],
                                ayatsNumber,
                                "",
                                ""
                            )
                        )
                        ayatsCounter++
                    } else if (sura.startsWith("a")) {
                        val ayatNumber = sura.substringBefore(".").removePrefix("a")
                        listOfAyats.add(
                            ayatsCounter,
                            Ayats(
                                sura.substringAfter("."),
                                Constants.suraNames[suraCounter],
                                ayatNumber,
                                "",
                                ""
                            )
                        )
                        ayatsCounter++
                    }
                }
            }
            listOfSuras.add(
                suraCounter,
                Suras(
                    Constants.suraNames[suraCounter],
                    suraCounter,
                    "",
                    listOfAyats,
                    listOfAyatsArabic
                )
            )
            inputStream.close()

            var noteString: String? = ""
            val noteStringBuilder = StringBuilder()
            val noteInputStream: InputStream = this.resources.openRawResource(R.raw.sure_notlari)
            val noteReader = BufferedReader(InputStreamReader(noteInputStream))
            while (true) {
                try {
                    if (noteReader.readLine().also { noteString = it } == null) break
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                noteStringBuilder.append(noteString).append("\n")
            }

            var suraNoteCounter = 0
            noteStringBuilder.lines().forEach { suraNote ->
                if (suraNoteCounter < 114) {
                    val editedNote = suraNote.substringAfter(".")
                    listOfSuras[suraNoteCounter].suraNote = editedNote
                }
                suraNoteCounter++
            }
            noteInputStream.close()

            var noteAyatString: String? = ""
            val noteAyatStringBuilder = StringBuilder()
            val noteAyatInputStream: InputStream = this.resources.openRawResource(R.raw.notlar3)
            val noteAyatReader = BufferedReader(InputStreamReader(noteAyatInputStream))
            while (true) {
                try {
                    if (noteAyatReader.readLine().also { noteAyatString = it } == null) break
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                noteAyatStringBuilder.append(noteAyatString).append("\n")
            }

            noteAyatStringBuilder.lines().forEach { ayatNote ->
                if (ayatNote != "") {
                    val suraNumber =
                        ayatNote.substringBefore(" ").removePrefix("notlar").substringBefore("-")
                            .toInt() - 1
                    val ayatNumber =
                        ayatNote.substringBefore(" ").removePrefix("notlar").substringAfter("-")
                    try {
                        listOfSuras[suraNumber].ayets.filter {
                            it.ayatId == ayatNumber || it.ayatId.contains(
                                ayatNumber + "-"
                            ) || it.ayatId.contains("-" + ayatNumber)
                        }[0].ayatNote = ayatNote.substringAfter(" ")
                    } catch (i: IndexOutOfBoundsException) {
                        println("breakpoint")
                    }
                }
            }
            noteAyatInputStream.close()

            var arabicAyatString: String? = ""
            val arabicAyatStringBuilder = StringBuilder()
            val arabicAyatInputStream: InputStream = this.resources.openRawResource(R.raw.arapca)
            val arabicAyatReader = BufferedReader(InputStreamReader(arabicAyatInputStream))
            while (true) {
                try {
                    if (arabicAyatReader.readLine().also { arabicAyatString = it } == null) break
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                arabicAyatStringBuilder.append(arabicAyatString).append("\n")
            }

            suraCounter = 0
            arabicAyatStringBuilder.lines().forEach {
                println("breakpoint")
                if (it == ",") {
                    listOfSuras[suraCounter].ayetsArabic = listOfAyatsArabic
                    suraCounter++
                    listOfAyatsArabic = mutableListOf()
                } else if (it != "") {
                    if (it.contains("\t")) {
                        listOfAyatsArabic.add(
                            Ayats(
                                it.substringAfter("\t"),
                                Constants.suraNames[suraCounter],
                                it.substringBefore("\t"),
                                "",
                                ""
                            )
                        )
                    } else {
                        if (it.substringBefore(" ").contains(".")) {
                            val ayatNumber = it.substringBefore(".")
                            listOfAyatsArabic.add(
                                Ayats(
                                    it.substringAfter(" "),
                                    Constants.suraNames[suraCounter],
                                    ayatNumber,
                                    "",
                                    ""
                                )
                            )
                        } else {
                            listOfAyatsArabic.add(
                                Ayats(
                                    it.substringAfter(" "),
                                    Constants.suraNames[suraCounter],
                                    it.substringBefore(" "),
                                    "",
                                    ""
                                )
                            )
                        }
                    }
                }
            }
            listOfSuras[suraCounter].ayetsArabic = listOfAyatsArabic
            println("breakpoint")

            listOfSuras.forEach { sura ->
                baseViewModel.insertSura(sura)
            }
        }
    }
}