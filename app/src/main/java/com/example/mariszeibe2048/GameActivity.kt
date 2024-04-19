package com.example.mariszeibe2048

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import kotlin.math.PI
import kotlin.math.atan2

class GameActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    private lateinit var mDetector: GestureDetectorCompat
    private lateinit var gameOverScreen: LinearLayout
    private lateinit var gameOverScore: TextView
    private lateinit var scoreText: TextView
    private lateinit var game: GameView
    private var baseNumber = 0
    private var fieldSize = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        fieldSize = intent.getIntExtra("fieldSize", 4)
        baseNumber = intent.getIntExtra("baseNumber", 2)

        enableEdgeToEdge()

        setContentView(R.layout.activity_game)

        // The detector is used for detecting fling (swipe) actions
        mDetector = GestureDetectorCompat(this, this)

        // Sets the back button navigation to MainMenuActivity with game parameters for the input fields
        // The button appears when the game ends
        findViewById<Button>(R.id.backToMenuButton).setOnClickListener {
            val intent = Intent(this, MainMenuActivity::class.java)
            intent.putExtra("fieldSize", fieldSize)
            intent.putExtra("baseNumber", baseNumber)
            startActivity(intent)
        }

        // Sets the score text
        scoreText = findViewById(R.id.scoreText)
        scoreText.text = getString(R.string.score, score)

        // Starts the game by passing the game parameters
        game = findViewById(R.id.gameField)
        game.startGame(fieldSize, baseNumber)

        gameOverScreen = findViewById(R.id.gameOverScreen)
        gameOverScore = findViewById(R.id.gameOverScore)
    }

    // Unused touch actions
    override fun onLongPress(e: MotionEvent) { }
    override fun onShowPress(e: MotionEvent) { }
    override fun onDown(e: MotionEvent): Boolean { return false }
    override fun onSingleTapUp(e: MotionEvent): Boolean { return false }
    override fun onScroll( e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean { return false }

    // Binds the touch detector
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {

        if (gameOverScreen.visibility == VISIBLE) {
            return false
        }

        // Calculates the swipe direction
        val angle = 180 * atan2(velocityY, velocityX) / PI
        val direction = when (angle) {
            in -135.0..-45.0 -> Direction.UP
            in -45.0..45.0 -> Direction.RIGHT
            in 45.0..135.0 -> Direction.DOWN
            else -> Direction.LEFT
        }

        game.makeMove(direction)
        score = game.getScore()
        scoreText.text = getString(R.string.score, score)

        if (game.hasGameEnded()) {
            // Show the end screen with animation
            gameOverScore.text = getString(R.string.end_score, score)
            gameOverScreen.visibility = VISIBLE
            gameOverScreen.alpha = 0F
            gameOverScreen.animate().apply {
                startDelay = 1000
                duration = 1000
                alpha(1F)
            }
        }

        return true
    }
}