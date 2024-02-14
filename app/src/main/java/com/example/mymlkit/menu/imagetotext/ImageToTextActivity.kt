package com.example.mymlkit.menu.imagetotext

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.mymlkit.R

class ImageToTextActivity : AppCompatActivity() {
    private var captureButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_to_text)
        captureButton = findViewById(R.id.idBtnCapture)
        captureButton!!.setOnClickListener {
            startActivity(
                Intent(
                    this@ImageToTextActivity,
                    ScannerActivity::class.java
                )
            )
        }
    }
}