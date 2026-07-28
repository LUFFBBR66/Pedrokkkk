
package com.ghost.facelandmarker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.max

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var results: FaceLandmarkerResult? = null
    private var imageWidth = 1
    private var imageHeight = 1

    private val pointPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
        strokeWidth = 2f
    }
    
    private val linePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 1f
        alpha = 180
    }

    fun setResults(result: FaceLandmarkerResult, width: Int, height: Int) {
        this.results = result
        this.imageWidth = width
        this.imageHeight = height
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val result = results ?: return
        if (result.faceLandmarks().isEmpty()) return

        // Calcula escala para preencher a tela (Aspect Fill)
        val scaleFactor = max(width / imageWidth.toFloat(), height / imageHeight.toFloat())
        val offsetX = (width - imageWidth * scaleFactor) / 2f
        val offsetY = (height - imageHeight * scaleFactor) / 2f

        for (landmarks in result.faceLandmarks()) {
            // 1. Desenha as conexões (linhas)
            FaceLandmarker.FACE_LANDMARKS_CONNECTORS.forEach { connector ->
                val start = landmarks[connector.start()]
                val end = landmarks[connector.end()]
                
                canvas.drawLine(
                    start.x() * imageWidth * scaleFactor + offsetX,
                    start.y() * imageHeight * scaleFactor + offsetY,
                    end.x() * imageWidth * scaleFactor + offsetX,
                    end.y() * imageHeight * scaleFactor + offsetY,
                    linePaint
                )
            }

            // 2. Desenha os pontos (opcional, mas bom para debug)
            for (point in landmarks) {
                val x = point.x() * imageWidth * scaleFactor + offsetX
                val y = point.y() * imageHeight * scaleFactor + offsetY
                canvas.drawCircle(x, y, 2f, pointPaint)
            }
        }
    }
}
