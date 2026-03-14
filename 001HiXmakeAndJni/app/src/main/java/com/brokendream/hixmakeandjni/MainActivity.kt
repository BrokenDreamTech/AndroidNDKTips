package com.brokendream.hixmakeandjni

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.brokendream.hixmakeandjni.databinding.ActivityMainBinding
import com.brokendream.xmake.Native

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            sampleText.text = stringFromJNI()
            tvXmake.text = Native.hiString()
        }
    }

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("hixmakeandjni")
        }
    }
}