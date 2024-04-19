package com.example.mariszeibe2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import androidx.core.graphics.toRectF
import kotlin.math.pow
import kotlin.random.Random

class GameView : ViewGroup {
    private val viewRect = Rect()
    private val tilePaint = Paint()
    private val innerCornerRadius = 20F
    private val cornerRadius = 30F
    private val padding = 0.15F
    private val pow2Rarity = 4
    private val debug = true

    private var gameField = emptyArray<Array<GameTile>>()
    private var baseNumber = 2
    private var tileSize = 0F
    private var score = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun getScore(): Int {
        return score
    }

    fun startGame(fieldSize: Int, baseNumber: Int) {
        this.baseNumber = baseNumber

        // Fills the field with empty tiles
        this.gameField = Array(fieldSize) {
            Array(fieldSize) {
                GameTile(context).apply {
                    power = 0
                    base = baseNumber
                }
            }
        }

        addRandomTile()
    }

    private fun addRandomTile() {

        // Finds all empty tiles
        val emptyTiles = mutableListOf<Pair<Int, Int>>()
        for (x in gameField.indices) {
            for (y in gameField.indices) {
                if (gameField[x][y].power == 0) {
                    emptyTiles.add(Pair(x, y))
                }
            }
        }

        // Changes the value of 1 random tile
        if (emptyTiles.size > 0) {
            val randomEmptyField = emptyTiles[Random.nextInt(0, emptyTiles.size)]
            gameField[randomEmptyField.first][randomEmptyField.second].animationNewTile = true
            gameField[randomEmptyField.first][randomEmptyField.second].power =
                (Random.nextInt(0, pow2Rarity) / (pow2Rarity - 1)) + 1
        }
    }

    fun makeMove(direction: Direction) {

        // Inverses the algorithm direction if necessary
        val xyRange = if (direction in setOf(Direction.LEFT, Direction.UP)) {
            gameField.indices
        } else {
            (gameField.size - 1) downTo 0
        }

        for (axis1 in xyRange) {
            for (axis2 in xyRange) {

                gameField[axis1][axis2].animationDirection = direction

                // Checks each tile next to the current tile in the correct direction
                val dRange = if (direction in setOf(Direction.LEFT, Direction.UP)) {
                    1 ..< (gameField.size - axis2)
                } else {
                    -1 downTo -axis2
                }

                for (distance in dRange) {

                    // If the direction is vertical, switches x and y
                    val x0: Int; val y0: Int; val x1: Int; val y1: Int
                    if (direction in setOf(Direction.UP, Direction.DOWN)) {
                        x0 = axis2; y0 = axis1; x1 = axis2+distance; y1 = axis1
                    } else {
                        x0 = axis1; y0 = axis2; x1 = axis1; y1 = axis2+distance
                    }

                    // If current tile is free, moves the found tile there
                    if (gameField[x0][y0].power == 0) {
                        gameField[x0][y0].power = gameField[x1][y1].power
                        gameField[x1][y1].power = 0
                        gameField[x1][y1].animationOffset++

                    // If the tiles have the same value, combines them and stops searching
                    } else if (gameField[x0][y0].power == gameField[x1][y1].power) {
                        gameField[x0][y0].power++
                        gameField[x1][y1].power = 0
                        score += (baseNumber * 2.0.pow(gameField[x0][y0].power - 1)).toInt()
                        gameField[x0][y0].animationCombinedTile = true
                        gameField[x1][y1].animationOffset++
                        break

                    // If the found tile isn't empty (and neither is current tile), stops searching
                    } else if (gameField[x1][y1].power != 0) {
                        break
                    }
                }
            }
        }

        // Adds random tile and refreshes the view
        addRandomTile()
        invalidate()

        // Prints the game progress in log
        if (debug) {
            Log.d("GAME", "Direction: $direction")
            for (row in gameField) {
                var text = ""
                for (tile in row) {
                    text += if (tile.power == 0) "-    " else "%-5d".format(baseNumber * 2.0.pow(tile.power).toInt())
                }
                Log.d("GAME", text)
            }
        }
    }

    // The game hasn't ended if any field is empty or any field is next to a field with the same value
    fun hasGameEnded(): Boolean {
        for (x in gameField.indices) {
            for (y in gameField.indices) {
                if (gameField[x][y].power == 0) {
                    return false
                }
                if (x < gameField.size - 1) {
                    if (gameField[x][y].power == gameField[x + 1][y].power) {
                        return false
                    }
                }
                if (y < gameField.size - 1) {
                    if (gameField[x][y].power == gameField[x][y + 1].power) {
                        return false
                    }
                }
            }
        }
        return true
    }

    // Animates tile changes (new tiles and combined tiles but not movement) and redraws the tiles to update the values
    private fun refreshTiles() {
        for (x in gameField.indices) {
            for (y in gameField.indices) {

                // New tiles
                if (gameField[x][y].animationNewTile) {
                    gameField[x][y].scaleX = 0F
                    gameField[x][y].scaleY = 0F
                    gameField[x][y].invalidate()
                    gameField[x][y].animate().apply {
                        duration = 50
                        scaleX(1F)
                        scaleY(1F)
                    }
                }

                // Tiles that have changed value
                else if (gameField[x][y].animationCombinedTile) {
                    gameField[x][y].invalidate()
                    gameField[x][y].animate().apply {
                        duration = 50
                        scaleX(1.15F)
                        scaleY(1.15F)
                    }.withEndAction {
                        gameField[x][y].animate().apply {
                            duration = 50
                            scaleX(1F)
                            scaleY(1F)
                        }
                    }

                // Unchanged tiles
                } else {
                    gameField[x][y].invalidate()
                }

                gameField[x][y].animationCombinedTile = false
                gameField[x][y].animationNewTile = false
            }
        }
    }

    // Used to get the game field size, calculate tile sizes and add them to this view as children
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        super.onSizeChanged(w, h, oldw, oldh)

        tileSize = w / (gameField.size + gameField.size * padding + padding)

        for (x in gameField.indices) {
            for (y in gameField.indices) {

                // Adds the tile only if it hasn't been added already
                if (gameField[x][y].parent != this) {
                    this.addView(gameField[x][y], 200, 200)
                }

                // Redraws the tile
                gameField[x][y].invalidate()
            }
        }
    }

    // Draws game field and animates tile movement
    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        // Gets the view size and draws the empty game field
        canvas.getClipBounds(viewRect)
        tilePaint.color = context.getColor(R.color.field1)
        canvas.drawRoundRect(viewRect.toRectF(), cornerRadius, cornerRadius, tilePaint)
        tilePaint.color = context.getColor(R.color.field2)

        // Count of animations in progress
        var animationsInProgress = 0

        for (x in gameField.indices) {
            for (y in gameField.indices) {

                // Draws an empty tile
                canvas.drawRoundRect(
                    tileSize * (padding * (y + 1) + y),
                    tileSize * (padding * (x + 1) + x),
                    tileSize * (padding + 1) * (y + 1),
                    tileSize * (padding + 1) * (x + 1),
                    innerCornerRadius,
                    innerCornerRadius,
                    tilePaint
                )

                // Add the count of active animations
                animationsInProgress++

                // The animation move distance is calculated from the tile move count, tile size and padding
                val distance = gameField[x][y].animationOffset * tileSize * (1 + padding)

                gameField[x][y].animate().apply {
                    duration = 100

                    // Applies the animation in the correct direction
                    when (gameField[x][y].animationDirection) {
                        Direction.LEFT -> translationX(-1 * distance)
                        Direction.UP -> translationY(-1 * distance)
                        Direction.RIGHT -> translationX(distance)
                        Direction.DOWN -> translationY(distance)
                    }
                }.withEndAction {

                    // Resets tile position after the animation
                    gameField[x][y].translationX = 0F
                    gameField[x][y].translationY = 0F

                    animationsInProgress--

                    // If all tiles have finished the animation, plays additional animations and refreshed tile values
                    if (animationsInProgress == 0) {
                        refreshTiles()
                    }
                }.start()

                gameField[x][y].animationOffset = 0F
            }
        }
    }

    // Positions the tiles inside the game field
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        // Enables drawing functionality for this view
        setWillNotDraw(false)


        for (x in gameField.indices) {
            for (y in gameField.indices) {

                // Sets new tile scale to 0 for animations
                if (gameField[x][y].animationNewTile) {
                    gameField[x][y].scaleX = 0F
                    gameField[x][y].scaleY = 0F
                }

                // Positions the tile
                gameField[x][y].layout(
                    (tileSize * (padding * (y + 1) + y)).toInt(),
                    (tileSize * (padding * (x + 1) + x)).toInt(),
                    (tileSize * (padding + 1) * (y + 1)).toInt(),
                    (tileSize * (padding + 1) * (x + 1)).toInt(),
                )

            }
        }

    }
}