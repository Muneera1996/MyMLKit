package com.example.mymlkit.menu.translate_text

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mymlkit.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class TranslateLanguageActivity : AppCompatActivity() {

    private var fromSpinner: Spinner? = null
    private var toSpinner: Spinner? = null
    private var sourceEdt: TextInputEditText? = null
    private var micIV: ImageView? = null
    private var translateBtn: MaterialButton? = null
    private var translatedTV: TextView? = null

    var fromLanguages = arrayOf(
        "from",
        "English",
        "German",
        "Arabic",
        "Hindi",
        "Urdu"
    )

    var toLanguages = arrayOf(
        "to",
        "English",
        "German",
        "Arabic",
        "Hindi",
        "Urdu"
    )

    private val REQUEST_PERMISSION_CODE = 1
    var languageCode = 0
    var fromLanguageCode: String = ""
    var toLanguageCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate_language)

        fromSpinner = findViewById(R.id.idFromSpinner)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceEdt = findViewById(R.id.idEdtSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslate)
        translatedTV = findViewById(R.id.idTvTranslatedTV)

        fromSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                fromLanguageCode = getLanguageCode(fromLanguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val fromAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, fromLanguages)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner?.adapter = fromAdapter

        toSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                toLanguageCode = getLanguageCode(toLanguages[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val toAdapter: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, R.layout.spinner_item, toLanguages)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner!!.adapter = toAdapter

        translateBtn!!.setOnClickListener(View.OnClickListener {
            translatedTV!!.text = ""
            if (sourceEdt!!.text.toString().isEmpty()) {
                Toast.makeText(
                    this@TranslateLanguageActivity,
                    "Please enter your text to translate",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fromLanguageCode == "") {
                Toast.makeText(
                    this@TranslateLanguageActivity,
                    "Please select source language",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (toLanguageCode == "") {
                Toast.makeText(
                    this@TranslateLanguageActivity,
                    "Please language to translate",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                translateText(fromLanguageCode, toLanguageCode, sourceEdt!!.text.toString())
            }
        })

        micIV!!.setOnClickListener(View.OnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text")
            try {
                // startActivityForResult(i,REQUEST_PERMISSION_CODE)
                //above code is deprecated
                startForResult.launch(i)

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TranslateLanguageActivity, "" + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })

    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == REQUEST_PERMISSION_CODE || result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val result =
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    sourceEdt!!.setText(result!![0])
                }
            }
        }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == MainActivity.REQUEST_PERMISSION_CODE) {
//            if (resultCode == RESULT_OK && data != null) {
//                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
//                sourceEdt!!.setText(result!![0])
//            }
//        }
//    }

    private fun translateText(fromLanguageCode: String, toLanguageCode: String, src: String) {
        translatedTV!!.text = "Downloading Modal..."
        val options = TranslatorOptions.Builder().setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode).build()

        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                translatedTV!!.text = "Translating..."
                translator.translate(src)
                    .addOnSuccessListener(OnSuccessListener<String?> { s ->
                        translatedTV!!.text = s
                    })
                    .addOnFailureListener(
                        OnFailureListener { e ->
                            Toast.makeText(
                                this@TranslateLanguageActivity,
                                "Fail to translate: " + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        })
            }
            .addOnFailureListener { e ->
                // Model couldn’t be downloaded or other internal error.
                Toast.makeText(
                    this@TranslateLanguageActivity,
                    "Fail to download the language: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun getLanguageCode(language: String): String {
        return when (language) {
            "English" -> TranslateLanguage.ENGLISH
            "German" -> TranslateLanguage.GERMAN
            "Afrikaans" -> TranslateLanguage.AFRIKAANS
            "Arabic" -> TranslateLanguage.ARABIC
            "Belarusian" -> TranslateLanguage.BELARUSIAN
            "Bengali" -> TranslateLanguage.BENGALI
            "Catalan" -> TranslateLanguage.CATALAN
            "Czech" -> TranslateLanguage.CZECH
            "Welsh" -> TranslateLanguage.WELSH
            "Hindi" -> TranslateLanguage.HINDI
            "Urdu" -> TranslateLanguage.URDU
            else -> ""
        }
    }


}