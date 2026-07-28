
package com.ghost.facelandmarker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult

class FaceLandmarkerHelper(
    private val context: Context,
    private val listener: LandmarkerListener
) {
    interface LandmarkerListener {
        fun onResults(result: FaceLandmarkerResult, inputWidth: Int, inputHeight: Int, inferenceTime: Long)
        fun onError(error: String)
    }

    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupFaceLandmarker()
    }

    private fun setupFaceLandmarker() {
        try {
            val baseOptionsBuilder = BaseOptions.builder()
                .setModelAssetPath("face_landmarker.task")
                .setDelegate(Delegate.GPU) // Tenta usar GPU por padrão para melhor performance

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptionsBuilder.build())
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumFaces(1)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setResultListener { result, input ->
                    val finishTimeMs = SystemClock.uptimeMillis()
                    val inferenceTime = finishTimeMs - result.timestampMs()
                    listener.onResults(result, input.width, input.height, inferenceTime)
                }
                .setErrorListener { e ->
                    listener.onError(e.message ?: "Erro desconhecido")
                }
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
        } catch (e: Exception) {
            listener.onError("Falha ao criar FaceLandmarker: ${e.message}")
        }
    }

    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        val frameTime = SystemClock.uptimeMillis()
        
        // Converter ImageProxy (RGBA_8888) para Bitmap
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )
        
        imageProxy.use { 
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) 
        }

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            if (isFrontCamera) {
                postScale(-1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat())
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        faceLandmarker?.detectAsync(mpImage, frameTime)
    }

    fun clear() {
        faceLandmarker?.close()
        faceLandmarker = null
    }
}
