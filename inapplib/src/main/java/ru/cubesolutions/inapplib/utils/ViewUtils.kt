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
        layoutParams.gravity = Gravity.CENTER
        imageView.layoutParams = layoutParams
        try {
            val urlImage = element.attr("src")
            if (urlImage.isNotEmpty()) {
                val newUrl = URL(urlImage)
                val bitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream())
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
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
        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        try {
            val textData = element.childNode(0).toString()
            textView.text = textData
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        textView.tag = uuidFromElement

        return textView
    }

    fun getButton(
        context: Context,
        element: Element,
    ): Button {
        val button = Button(context)
        try {
            val color = element.attr("color")
            if (color.isNotEmpty()) {
                val parseColor = Color.parseColor(color)
                button.setTextColor(parseColor)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        button.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        try {
            val textData = element.childNode(0).toString()
            button.text = textData
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        button.tag = uuidFromElement

        return button
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
        linLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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