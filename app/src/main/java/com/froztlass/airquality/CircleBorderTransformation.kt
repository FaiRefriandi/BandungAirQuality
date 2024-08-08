package com.froztlass.airquality

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class CircleBorderTransformation(private val borderSize: Float, private val borderColor: Int) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + borderSize + borderColor).toByteArray(CHARSET))
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val transformed = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight)

        val output = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawBitmap(transformed, 0f, 0f, paint)

        val borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderSize
        borderPaint.color = borderColor

        val borderRadius = (Math.min(outWidth, outHeight) - borderSize) / 2f
        canvas.drawCircle(outWidth / 2f, outHeight / 2f, borderRadius, borderPaint)

        return output
    }

    override fun equals(other: Any?): Boolean {
        return other is CircleBorderTransformation && other.borderSize == borderSize && other.borderColor == borderColor
    }

    override fun hashCode(): Int {
        return ID.hashCode() + borderSize.toInt() * 1000 + borderColor
    }

    companion object {
        private const val ID = "com.froztlass.airquality.util.CircleBorderTransformation"
    }
}
