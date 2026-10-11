package com.pippo2801.universalgamebot.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.nio.ByteBuffer

class ScreenCaptureManager(private val context: Context) {
    private val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            virtualDisplay?.release()
            virtualDisplay = null
            imageReader?.close()
            imageReader = null
            mediaProjection = null
            Log.i("ScreenCaptureManager", "Screen capture permission revoked; resources released")
        }
    }

    var width = 1080
        private set
    var height = 2340
        private set
    var density = 420
        private set

    fun createScreenCaptureIntent(): Intent = mediaProjectionManager.createScreenCaptureIntent()

    fun initProjection(resultCode: Int, data: Intent, screenWidth: Int, screenHeight: Int, screenDensity: Int) {
        release()
        require(screenWidth > 0 && screenHeight > 0 && screenDensity > 0) {
            "Invalid screen dimensions or density"
        }
        width = screenWidth
        height = screenHeight
        density = screenDensity
        val projection = mediaProjectionManager.getMediaProjection(resultCode, data)
            ?: throw IllegalStateException("MediaProjection unavailable")
        mediaProjection = projection
        projection.registerCallback(projectionCallback, Handler(Looper.getMainLooper()))
        setupVirtualDisplay()
        Log.i("ScreenCaptureManager", "MediaProjection initialized: ${width}x${height}@${density}")
    }

    private fun setupVirtualDisplay() {
        val projection = mediaProjection ?: throw IllegalStateException("MediaProjection not initialized")
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 3)
        virtualDisplay = projection.createVirtualDisplay(
            "UniversalGameBotDisplay", width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, Handler(Looper.getMainLooper())
        ) ?: throw IllegalStateException("Unable to create virtual display")
    }

    fun captureLatestBitmap(): Bitmap? {
        val reader = imageReader ?: return null
        var image: Image? = null
        try {
            image = reader.acquireLatestImage() ?: return null
            val plane = image.planes.firstOrNull() ?: return null
            val buffer: ByteBuffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            if (pixelStride <= 0 || rowStride <= 0) return null
            val imageWidth = image.width
            val imageHeight = image.height
            val paddedWidth = imageWidth + (rowStride - pixelStride * imageWidth) / pixelStride
            val paddedBitmap = Bitmap.createBitmap(paddedWidth, imageHeight, Bitmap.Config.ARGB_8888)
            buffer.rewind()
            paddedBitmap.copyPixelsFromBuffer(buffer)
            val cropped = Bitmap.createBitmap(paddedBitmap, 0, 0, imageWidth, imageHeight)
            if (cropped !== paddedBitmap) paddedBitmap.recycle()
            return cropped
        } catch (e: Exception) {
            Log.e("ScreenCaptureManager", "Error capturing screen bitmap", e)
            return null
        } finally {
            image?.close()
        }
    }

    fun release() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.close()
        imageReader = null
        mediaProjection?.unregisterCallback(projectionCallback)
        mediaProjection?.stop()
        mediaProjection = null
        Log.i("ScreenCaptureManager", "ScreenCaptureManager released")
    }
}
