package com.example.pgoexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.pgoexample.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the profile file path using the app's Context
        val profileFileName = "profile.pgo" // You can use any desired file name
        val profileFilePath = File(applicationContext.filesDir, profileFileName).absolutePath

        // Call the native method with the profile file path
        binding.sampleText.text = startProfiling(profileFilePath)
        stopProfiling()
    }

    /**
     * A native method that is implemented by the 'pgoexample' native library,
     * which is packaged with this application.
     */
    external fun startProfiling(profileFile: String): String

    external fun stopProfiling()

    companion object {
        // Used to load the 'pgoexample' library on application startup.
        init {
            System.loadLibrary("pgoexample")
        }
    }
}