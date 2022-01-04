package com.aydinpolat.kuranmeali.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aydinpolat.kuranmeali.R
import com.aydinpolat.kuranmeali.databinding.ActivityMainBinding
import com.aydinpolat.kuranmeali.fragments.searchword.SearchWordFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}