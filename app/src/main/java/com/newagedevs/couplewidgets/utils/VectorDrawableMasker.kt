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
        rawMask: Int,
        canvasSize: Int,
        borderSize: Int,
        borderColor: Int
    ): Bitmap {

        // A persisted resource ID may no longer refer to a real shape (see
        // DecoratorCatalog); fall back to the default rather than masking with
        // whatever unrelated drawable now owns that ID.
        val mask = DecoratorCatalog.safeShape(rawMask)

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
        strokePaintColor: Int
    ): Bitmap {
        val vectorDrawable =
            VectorDrawableParser.parsedVectorDrawable(context.resources, mask) ?: return bitmap
        if (vectorDrawable.viewportWidth <= 0f || vectorDrawable.viewportHeight <= 0f) return bitmap

        // Draw the outline straight onto the full-resolution bitmap by scaling each
        // path with a matrix. The previous version rendered the stroke on a tiny
        // (viewport-sized, e.g. 24x24) bitmap and then upscaled it ~10x, which is
        // what made the border look thick, blurry and jagged.
        val matrix = Matrix().apply {
            setScale(
                bitmap.width / vectorDrawable.viewportWidth,
                bitmap.height / vectorDrawable.viewportHeight
            )
        }

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            color = strokePaintColor
        }

        val canvas = Canvas(bitmap)
        for (pathData in vectorDrawable.pathData) {
            pathData ?: continue
            val path = SvgPath(pathData).generatePath()
            path.transform(matrix)
            canvas.drawPath(path, strokePaint)
        }

        return bitmap
    }

}