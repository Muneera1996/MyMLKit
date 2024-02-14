package com.example.mymlkit.menu.imagetotext

import android.Manifest
import android.Manifest.permission_group
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mymlkit.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ScannerActivity : AppCompatActivity() {
    private var captureIV: ImageView? = null
    private var resultTV: TextView? = null
    private var snapbtn: Button? = null
    private var detectBtn: Button? = null
    private var imageBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        captureIV = findViewById(R.id.idIVCaptureImage)
        resultTV = findViewById(R.id.idTVDetectedText)
        snapbtn = findViewById(R.id.idButtonSnap)
        detectBtn = findViewById(R.id.idButtonDetect)
        detectBtn!!.setOnClickListener { detectText() }
        snapbtn!!.setOnClickListener {
            if (checkPermission()) {
                captureImage()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val cameraPermission =
            ContextCompat.checkSelfPermission(applicationContext, permission_group.CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val PERMISSION_CODE = 200
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_CODE
        )
    }

    private fun captureImage() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePicture.resolveActivity(packageManager) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (cameraPermission) {
                Toast.makeText(this, "Permission Granted ", Toast.LENGTH_SHORT).show()
                captureImage()
            } else {
                Toast.makeText(applicationContext, "Permission denied !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val extras = data!!.extras
            imageBitmap = extras!!["data"] as Bitmap?
            captureIV!!.setImageBitmap(imageBitmap)
        }
    }

    private fun detectText() {
        val image = InputImage.fromBitmap(imageBitmap!!, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val result = recognizer.process(image).addOnSuccessListener { text ->
            val result = StringBuilder()
            for (block in text.textBlocks) {
                result.append("\n")
                val blockText = block.text
                val blockCornerPoint = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    val lineText = line.text
                    val lineCornerPoint = line.cornerPoints
                    val lineRect = line.boundingBox
                    for (element in line.elements) {
                        result.append(" ")
                        val elementText = element.text
                        result.append(elementText)
                        Log.e("result",result.toString())
                    }

                }
            }

            resultTV!!.text = result.toString()
        }.addOnFailureListener { e ->
            Toast.makeText(
                applicationContext,
                "Failed to detect text From image ....!" + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}