package com.example.mariszeibe2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.min
import kotlin.math.pow

class GameTile: View {
    private val textPaint = TextPaint().apply { textSize = maxTextSize; textAlign = Paint.Align.CENTER }
    private val color1 = context.getColor(R.color.start_tile)
    private val color2 = context.getColor(R.color.end_tile)
    private val textBoundsRect = Rect()
    private val tilePaint = Paint()
    private val cornerRadius = 20F
    private val maxTextSize = 100F
    private val padding = 0.15F

    var animationDirection = Direction.DOWN
    var animationCombinedTile = false
    var animationNewTile = false
    var animationOffset = 0F
    var power = 0
    var base = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw the tile if it's empty
        if (power == 0) { return }

        // Gets the tile value/text
        val text = (base * 2.0.pow(power - 1)).toInt().toString()

        // Gets the tile color as a gradient from 2 colors based of the tile value
        val tileColor = if (power > 11) { color2 } else {
            Color.rgb(
                color1.red + (color2.red - color1.red) / 10 * (power - 1),
                color1.green + (color2.green - color1.green) / 10 * (power - 1),
                color1.blue + (color2.blue - color1.blue) / 10 * (power - 1)
            )
        }

        tilePaint.color = tileColor

        // Gets and calculates the text dimensions so it fits in the tile
        textPaint.textSize = min(maxTextSize, width * (1 - padding))
        textPaint.getTextBounds(text, 0, text.length, textBoundsRect)
        textPaint.textSize = min(textPaint.textSize, textPaint.textSize * (width * (1 - padding)) / textBoundsRect.width())
        textPaint.getTextBounds(text, 0, text.length, textBoundsRect)

        // Sets the text color to either black or white based on the tile color
        textPaint.color = if ((tileColor.red + tileColor.green + tileColor.blue) / 3 < 200) {
            context.getColor(R.color.white)
        } else {
            context.getColor(R.color.black)
        }

        // Draws the tile
        canvas.drawRoundRect(
            0F,
            0F,
            width.toFloat(),
            height.toFloat(),
            cornerRadius,
            cornerRadius,
            tilePaint
        )

        // Draws the centered text in the tile
        canvas.drawText(
            text,
            width / 2F,
            (width + textBoundsRect.height()) / 2F,
            textPaint
        )

    }

}