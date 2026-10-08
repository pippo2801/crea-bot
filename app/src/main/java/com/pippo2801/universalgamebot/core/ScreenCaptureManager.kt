package com.pippo2801.universalgamebot.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.nio.ByteBuffer

class ScreenCaptureManager(private val context: Context) {

    private var mediaProjectionManager: MediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    var width = 1080
    var height = 2340
    var density = 420

    fun createScreenCaptureIntent(): Intent {
        return mediaProjectionManager.createScreenCaptureIntent()
    }

    fun initProjection(resultCode: Int, data: Intent, screenWidth: Int, screenHeight: Int, screenDensity: Int) {
        width = screenWidth
        height = screenHeight
        density = screenDensity
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        setupVirtualDisplay()
        Log.i("ScreenCaptureManager", "MediaProjection initialized: ${width}x${height}@${density}")
    }

    private fun setupVirtualDisplay() {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "UniversalGameBotDisplay",
            width, height, density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )
    }

    fun captureLatestBitmap(): Bitmap? {
        val reader = imageReader ?: return null
        var image: Image? = null
        try {
            image = reader.acquireLatestImage() ?: reader.acquireNextImage()
            if (image != null) {
                val planes = image.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width

                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                return Bitmap.createBitmap(bitmap, 0, 0, width, height)
            }
        } catch (e: Exception) {
            Log.e("ScreenCaptureManager", "Error capturing screen bitmap", e)
        } finally {
            image?.close()
        }
        return null
    }

    fun release() {
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
        mediaProjection = null
        Log.i("ScreenCaptureManager", "ScreenCaptureManager released.")
    }
}
