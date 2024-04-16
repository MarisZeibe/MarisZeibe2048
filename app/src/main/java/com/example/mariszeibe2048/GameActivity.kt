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
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.random.Random

const val FIELD_SIZE = 4
const val POW_2_RARITY = 4

enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

class GameActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    private lateinit var mDetector: GestureDetectorCompat
    private var gameField = Array(FIELD_SIZE) { Array(FIELD_SIZE) { 0 } }

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
        makeMove(direction)
        printField()
        return true
    }

    private fun addRandomTile() {
        val emptyFields = mutableListOf<Pair<Int, Int>>()
        for (row in 0..< FIELD_SIZE) {
            for (col in 0..< FIELD_SIZE) {
                if (gameField[row][col] == 0) {
                    emptyFields.add(Pair(row, col))
                }
            }
        }
        if (emptyFields.size > 0) {
            val randomEmptyField = emptyFields[Random.nextInt(0, emptyFields.size)]
            gameField[randomEmptyField.first][randomEmptyField.second] =
                (Random.nextInt(0, POW_2_RARITY) / (POW_2_RARITY - 1)) + 1
        }
    }

    private fun makeMove(direction: Direction) {
        val xyRange = if (direction in setOf(Direction.LEFT, Direction.UP)) {
            0 ..< FIELD_SIZE
        } else {
            (FIELD_SIZE-1) downTo 0
        }

        for (a in xyRange) {
            for (b in xyRange) {
                val dRange = if (direction in setOf(Direction.LEFT, Direction.UP)) {
                    1..< (FIELD_SIZE - b)
                } else {
                    -1 downTo -b
                }
                for (d in dRange) {
                    val x0: Int; val y0: Int; val x1: Int; val y1: Int
                    if (direction in setOf(Direction.UP, Direction.DOWN)) {
                        x0 = b; y0 = a; x1 = b+d; y1 = a
                    } else {
                        x0 = a; y0 = b; x1 = a; y1 = b+d
                    }
                    if (gameField[x0][y0] == 0) {
                        gameField[x0][y0] = gameField[x1][y1]
                        gameField[x1][y1] = 0
                    } else if (gameField[x0][y0] == gameField[x1][y1]) {
                        gameField[x0][y0]++
                        gameField[x1][y1] = 0
                        break
                    } else if (gameField[x1][y1] != 0) {
                        break
                    }
                }
            }
        }
        addRandomTile()
    }

    private fun printField() {
        for (row in gameField) {
            var text = ""
            for (col in row) {
                text += if (col == 0) "-    " else "%-5d".format(2.0.pow(col).toInt())
            }
            Log.d("DEBUG", text)
        }
    }
}