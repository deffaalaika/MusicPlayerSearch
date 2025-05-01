package com.deffa.searchmodule

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AudioVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var waveform: ByteArray? = null
    private val paint = Paint().apply {
        strokeWidth = 2f
        isAntiAlias = true
        color = Color.BLACK
    }

    fun updateVisualizer(bytes: ByteArray) {
        waveform = bytes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val data = waveform ?: return
        val w = width.toFloat()
        val h = height.toFloat()
        val midY = h / 2


        val step = maxOf(1, data.size / width)


        var x = 0f
        for (i in data.indices step step) {
            val v = (data[i].toInt() and 0xFF) - 128
            val y = midY + (v / 128f) * (h / 2 * 0.8f)
            canvas.drawLine(x, midY, x, y, paint)
            x += w / (data.size / step)
        }
    }
}