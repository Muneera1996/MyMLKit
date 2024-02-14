package com.example.mymlkit.menu.ocr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mymlkit.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.Locale

class OCRActivity : AppCompatActivity() {
    private var inputImage: InputImage? = null
    private var imageView2: ImageView? = null
    private var recognizer: TextRecognizer? = null
    private var textView: TextView? = null
    private var image: MaterialButton? = null
    private var speech: MaterialButton? = null
    private var t: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocractivity)
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        image = findViewById<MaterialButton>(R.id.image)
        imageView2 = findViewById<ImageView>(R.id.image_view)
        textView = findViewById<TextView>(R.id.text)
        speech = findViewById<MaterialButton>(R.id.speech)
        image?.setOnClickListener { openGallery() }
        t = TextToSpeech(applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                t?.setLanguage(Locale.US)
                //                    t.setLanguage(new Locale("bn_BD"));
            }
        }
        speech?.setOnClickListener {
            t?.speak(
                textView!!.text.toString(),
                TextToSpeech.QUEUE_FLUSH,
                null
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                val byteArray = ByteArray(0)
                val filePath: String? = null
                try {
                    inputImage = data.data?.let { InputImage.fromFilePath(this, it) }
                    val resultUri: Bitmap? = inputImage?.bitmapInternal
                    Glide.with(this@OCRActivity)
                        .load(resultUri)
                        .into(imageView2!!)
                    val result: Task<Text>? = inputImage?.let {
                        recognizer?.process(it)
                            ?.addOnSuccessListener { visionText -> processTextBlock(visionText) }
                            ?.addOnFailureListener {
                                // Task failed with an exception
                                // ...
                            }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openGallery() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.setType("image/")
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.setType("image/")
        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    private fun processTextBlock(result: Text) {
        // [START mlkit_process_text_block]
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            textView!!.append("\n")
            val blockCornerPoints: Array<Point>? = block.cornerPoints
            val blockFrame: Rect? = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                //  textView.append("\n");
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    textView!!.append(" ")
                    val elementText = element.text
                    textView!!.append(elementText)
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
    }

    override fun onPause() {
        if (!t!!.isSpeaking) {
            super.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        const val PICK_IMAGE = 123
    }
}