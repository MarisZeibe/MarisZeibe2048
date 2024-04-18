package com.example.mariszeibe2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.toRectF
import kotlin.math.min
import kotlin.math.pow

const val PADDING = 0.15F
const val MAX_TEXT_SIZE = 100F
const val OUTER_RADIUS = 30F
const val INNER_RADIUS = 20F
val BACKGROUND_COLOR = rgb(187, 173, 160)
val LIGHT_TEXT = rgb(255, 255, 255)
val DARK_TEXT = rgb(0, 0, 0)

class GameView : View {
    var gameField = emptyArray<Array<Int>>()
    var baseNumber = 2.0
    private val viewRect = Rect()
    private val tilePaint = Paint()
    private val textBoundsRect = Rect()
    private val textPaint = TextPaint().apply { textSize = 100F; textAlign = Paint.Align.CENTER }
    private val tileColors = listOf(
        rgb(205, 193, 180),
        rgb(238, 228, 218),
        rgb(238, 228, 208),
        rgb(242, 177, 121),
        rgb(245, 149, 99),
        rgb(246, 124, 95),
        rgb(246, 95, 59),
        rgb(237, 207, 114),
        rgb(237, 204, 97),
        rgb(242, 196, 76),
        rgb(236, 188, 55),
        rgb(242, 190, 41),
        rgb(60, 59, 50)
    )

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.getClipBounds(viewRect)
        tilePaint.color = BACKGROUND_COLOR
        canvas.drawRoundRect(viewRect.toRectF(), OUTER_RADIUS, OUTER_RADIUS, tilePaint)
        val tileSize = viewRect.bottom / (gameField.size + gameField.size * PADDING + PADDING)

        for (x in gameField.indices) {
            for (y in gameField.indices) {
                val text = if (gameField[x][y] == 0) "" else (baseNumber * 2.0.pow(gameField[x][y] - 1)).toInt().toString()
                val tileColor = tileColors.getOrElse(gameField[x][y]) { tileColors.last() }
                tilePaint.color = tileColor
                textPaint.textSize = MAX_TEXT_SIZE
                textPaint.getTextBounds(text, 0, text.length, textBoundsRect)
                textPaint.textSize = min(MAX_TEXT_SIZE, textPaint.textSize * (tileSize * (1 - PADDING)) / textBoundsRect.width())
                textPaint.getTextBounds(text, 0, text.length, textBoundsRect)
                textPaint.color = if ((tileColor.red + tileColor.green + tileColor.blue) / 3 < 200) LIGHT_TEXT else DARK_TEXT
                canvas.drawRoundRect(
                    tileSize * (PADDING * (y + 1) + y),
                    tileSize * (PADDING * (x + 1) + x),
                    tileSize * ((PADDING + 1) * (y + 1)),
                    tileSize * ((PADDING + 1) * (x + 1)),
                    INNER_RADIUS,
                    INNER_RADIUS,
                    tilePaint
                )
                canvas.drawText(
                    text,
                    (tileSize * (PADDING * (y + 1) + y)) + tileSize / 2,
                    tileSize * (PADDING * (x + 1) + x) + (tileSize + textBoundsRect.height()) / 2,
                    textPaint
                )
            }
        }
    }
}