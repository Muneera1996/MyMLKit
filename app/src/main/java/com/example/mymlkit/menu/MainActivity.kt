package com.example.mymlkit.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymlkit.R
import com.example.mymlkit.databinding.ActivityMainBinding
import com.example.mymlkit.menu.imagetotext.ImageToTextActivity
import com.example.mymlkit.menu.imagetotext.ScannerActivity
import com.example.mymlkit.menu.ocr.OCRActivity
import com.example.mymlkit.menu.translate_text.TranslateLanguageActivity


class MainActivity : AppCompatActivity(), MenuAdapter.RecyclerViewEvent {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MenuAdapter
    private val menuList = ArrayList<Menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_main)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        menuList.clear()
        menuList.add(Menu("Translate Text","This is a simple Language Translation application which can translate text from one language to another"))
        menuList.add(Menu("This is a simple OCR application which can capture image from phone's camera and convert the text in image into text view and read those text. I use firebase ML Kit for image to text recognition."))
        menuList.add(Menu("OCR","This is a simple OCR application which can take photos from your local storage and convert the text in image into text view and read those text. I use firebase ML Kit for image to text recognition."))

        // Adapter
        mAdapter = MenuAdapter(menuList,this)

        mainBinding.rvMenu.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }

    }

    override fun onItemClick(position: Int) {


        if (position==0){
            val intent = Intent(this, TranslateLanguageActivity::class.java)
            startActivity(intent)
        }
        else if (position==1){
            val intent = Intent(this, ImageToTextActivity::class.java)
            startActivity(intent)
        }
        else if (position==2){
            val intent = Intent(this, OCRActivity::class.java)
            startActivity(intent)
        }

    }
}