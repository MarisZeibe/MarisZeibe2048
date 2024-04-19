package com.example.mariszeibe2048

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenuActivity : AppCompatActivity() {
    private var fieldSize = 4
    private var baseNumber = 2
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fieldSize = intent.getIntExtra("fieldSize", 4)
        baseNumber = intent.getIntExtra("baseNumber", 2)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main_menu)

        // Sets the padding so the app isn't behind the status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Sets the start button navigation to GameActivity with game parameters
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("fieldSize", fieldSize)
            intent.putExtra("baseNumber", baseNumber)
            startActivity(intent)
        }

        // Sets the field size value and text
        val fieldSizeInput = findViewById<SeekBar>(R.id.fieldSizeInput)
        val fieldSizeText = findViewById<TextView>(R.id.fieldSizeText)
        fieldSizeInput.progress = fieldSize - 2
        fieldSizeText.text = getString(R.string.field_size, fieldSize)

        fieldSizeInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                fieldSize = progress + 2
                fieldSizeText.text = getString(R.string.field_size, fieldSize)
            }
        })

        // Sets the starting number value and text
        val baseNumberInput = findViewById<SeekBar>(R.id.baseNumberInput)
        val baseNumberText = findViewById<TextView>(R.id.baseNumberText)
        baseNumberInput.progress = baseNumber - 2
        baseNumberText.text = getString(R.string.base_number, baseNumber)

        baseNumberInput.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                baseNumber = progress + 2
                baseNumberText.text = getString(R.string.base_number, baseNumber)
            }
        })
    }
}