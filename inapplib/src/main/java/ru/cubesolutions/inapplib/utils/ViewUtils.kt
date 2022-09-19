package ru.cubesolutions.inapplib.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import org.jsoup.nodes.Element
import java.net.URL
import java.util.*


object ViewUtils {

    fun getImageView(
        context: Context,
        element: Element,
    ): ImageView {
        val imageView = ImageView(context)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        try {
            val margin = element.attr(ConstUtils.TAG_MARGIN)
            if (margin.isNotEmpty()) {
                val values = margin.split(" ").map { value ->
                    value.filter { it.isDigit() }.toInt()
                }
                val top = values.getOrNull(0) ?: 0
                val right = values.getOrNull(1) ?: top
                val bottom = values.getOrNull(2) ?: top
                val left = values.getOrNull(3) ?: right

                layoutParams.setMargins(
                    (left.toPx(context)),
                    (top.toPx(context)),
                    (right.toPx(context)),
                    (bottom.toPx(context))
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        layoutParams.gravity = Gravity.CENTER
        imageView.layoutParams = layoutParams
        try {
            val urlImage = element.attr("src")
            if (urlImage.isNotEmpty()) {
                val newUrl = URL(urlImage)
                val bitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream())
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.adjustViewBounds = true
                imageView.setImageBitmap(bitmap)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        try {
            val widthAttr = element.attr("width")
            if (widthAttr.isNotEmpty()) {
                val result = widthAttr.filter { it.isDigit() }.toInt()
                imageView.updateLayoutParams {
                    width = result.toPx(context)
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        imageView.tag = uuidFromElement

        return imageView
    }

    fun getTextView(
        context: Context,
        element: Element,
    ): TextView {
        val textView = TextView(context)
        try {
            val color = element.attr("color")
            if (color.isNotEmpty()) {
                val parseColor = Color.parseColor(color)
                textView.setTextColor(parseColor)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        try {
            val margin = element.attr(ConstUtils.TAG_MARGIN)
            if (margin.isNotEmpty()) {
                val values = margin.split(" ").map { value ->
                    value.filter { it.isDigit() }.toInt()
                }
                val top = values.getOrNull(0) ?: 0
                val right = values.getOrNull(1) ?: top
                val bottom = values.getOrNull(2) ?: top
                val left = values.getOrNull(3) ?: right

                layoutParams.setMargins(
                    (left.toPx(context)),
                    (top.toPx(context)),
                    (right.toPx(context)),
                    (bottom.toPx(context))
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        textView.layoutParams = layoutParams

        textView.gravity =  Gravity.CENTER

        try {
            val textData = element.childNode(0).toString()
            textView.text = textData
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        try {
            val fontLevel = element.attr(ConstUtils.TAG_FONT_LEVEL)
            if (fontLevel.isNotEmpty()) {
                val result = fontLevel.filter { it.isDigit() }.toInt()
                when (result) {
                    1 -> {
                        textView.textSize = 32.toFloat()
                    }
                    2 -> {
                        textView.textSize = 28.toFloat()
                    }
                    3 -> {
                        textView.textSize = 20.toFloat()
                    }
                    4 -> {
                        textView.textSize = 18.toFloat()
                    }
                    else -> {
                        textView.textSize = 16.toFloat()
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        textView.tag = uuidFromElement

        return textView
    }

    fun getLinearLayout(
        context: Context,
        element: Element,
    ): LinearLayout {
        // Создали view из тега re-body - это верхнеуровневый view
        val linLayout = LinearLayout(context)
        try {
            val backgroundColor = element.attr("background-color")
            if (backgroundColor.isNotEmpty()) {
                val color = Color.parseColor(backgroundColor)
                linLayout.setBackgroundColor(color)
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        try {
            val margin = element.attr(ConstUtils.TAG_MARGIN)
            if (margin.isNotEmpty()) {
                val values = margin.split(" ").map { value ->
                    value.filter { it.isDigit() }.toInt()
                }
                val top = values.getOrNull(0) ?: 0
                val right = values.getOrNull(1) ?: top
                val bottom = values.getOrNull(2) ?: top
                val left = values.getOrNull(3) ?: right

                layoutParams.setMargins(
                    (left.toPx(context)),
                    (top.toPx(context)),
                    (right.toPx(context)),
                    (bottom.toPx(context))
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        try {
            val padding = element.attr(ConstUtils.TAG_PADDING)
            if (padding.isNotEmpty()) {
                val values = padding.split(" ").map { value ->
                    value.filter { it.isDigit() }.toInt()
                }
                val top = values.getOrNull(0) ?: 0
                val right = values.getOrNull(1) ?: top
                val bottom = values.getOrNull(2) ?: top
                val left = values.getOrNull(3) ?: right

                linLayout.setPadding(
                    (left.toPx(context)),
                    (top.toPx(context)),
                    (right.toPx(context)),
                    (bottom.toPx(context))
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        linLayout.layoutParams = layoutParams

        linLayout.orientation = VERTICAL
        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        linLayout.tag = uuidFromElement

        return linLayout
    }

    fun getSuperLinearLayout(
        context: Context,
    ): LinearLayout {
        // Создали view из тега re-body - это верхнеуровневый view
        val linLayout = LinearLayout(context)
        linLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linLayout.orientation = VERTICAL
        return linLayout
    }
}