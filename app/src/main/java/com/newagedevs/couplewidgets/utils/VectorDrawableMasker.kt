package com.newagedevs.couplewidgets.utils

import android.content.Context
import android.graphics.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.newagedevs.couplewidgets.R
import com.slaviboy.svgpath.SvgPath

object VectorDrawableMasker {

    fun maskImage(
        context: Context,
        image: Bitmap?,
        mask: Int,
        canvasSize: Int,
        borderSize: Int,
        borderColor: Int
    ): Bitmap {

        val scaledImage = try{
            Bitmap.createScaledBitmap(image!!, canvasSize, canvasSize, false)
        } catch (_:Exception) {
            val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_person)
            Bitmap.createScaledBitmap(bitmap, canvasSize, canvasSize, false)
        }

        val scaledMask = Bitmap.createScaledBitmap(
            ResourcesCompat.getDrawable(context.resources, mask, null)!!.toBitmap(),
            canvasSize, canvasSize, false
        )

        val maskedBitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
        val maskedCanvas = Canvas(maskedBitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        maskedCanvas.drawBitmap(scaledImage, 0f, 0f, null)
        maskedCanvas.drawBitmap(scaledMask, 0f, 0f, paint)
        paint.xfermode = null

        return drawBorderUsingVectorPath(context, maskedBitmap, mask, borderSize.toFloat(), borderColor)
    }

    private fun drawBorderUsingVectorPath(
        context: Context,
        bitmap: Bitmap,
        mask: Int,
        stroke: Float,
        strokePaintColor:Int
    ): Bitmap {
        val vectorDrawable = VectorDrawableParser.parsedVectorDrawable(context.resources, mask)

        if (vectorDrawable != null) {
            var strokeBitmap =
                Bitmap.createBitmap(
                    vectorDrawable.viewportWidth.toInt(),
                    vectorDrawable.viewportHeight.toInt(),
                    Bitmap.Config.ARGB_8888
                )

            val strokeCanvas = Canvas(strokeBitmap)
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            strokePaint.style = Paint.Style.STROKE
            strokePaint.strokeWidth = stroke

            strokePaint.color = strokePaintColor

            for (path in vectorDrawable.pathData) {
                val data = SvgPath(path!!)
                strokeCanvas.drawPath(data.generatePath(), strokePaint)
            }

            strokeBitmap =
                Bitmap.createScaledBitmap(strokeBitmap, bitmap.height, bitmap.width, false)

            val clipCanvas = Canvas(bitmap)
            val clipPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            clipCanvas.drawBitmap(bitmap, 0f, 0f, clipPaint)
            clipCanvas.drawBitmap(strokeBitmap, 0f, 0f, clipPaint)

            return bitmap
        }

        return bitmap
    }

}