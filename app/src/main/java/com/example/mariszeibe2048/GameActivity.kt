package com.example.mariszeibe2048

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

val FIELD_SIZE = 4

class GameActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var mDetector: GestureDetectorCompat
    private var gameField = Array(FIELD_SIZE) { Array(FIELD_SIZE) { } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetector = GestureDetectorCompat(this, this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }

    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        return
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        return
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val angle = 180 * atan2(velocityY, velocityX) / PI
        val direction = when (angle) {
            in -135.0..-45.0 -> Direction.UP
            in -45.0..45.0 -> Direction.RIGHT
            in 45.0..135.0 -> Direction.DOWN
            else -> Direction.LEFT
        }
        Log.d("DEBUG", direction.toString())
        for (row in gameField) {
            var text = ""
            for (col in row) {
                text += "$col "
            }
            Log.d("DEBUG", text)
        }
        return true
    }
}