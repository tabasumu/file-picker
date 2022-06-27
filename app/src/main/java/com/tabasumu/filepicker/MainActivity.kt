package com.tabasumu.filepicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tabasumu.filepicker.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.pickBtn.setOnClickListener {

            FilePicker.Builder(this).pick {
                it.forEach { (_, file) ->
                    Timber.i("${file.name} exists: ${file.exists()}")
                }
            }

        }


    }
}