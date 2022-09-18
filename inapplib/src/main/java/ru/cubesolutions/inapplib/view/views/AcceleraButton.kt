package ru.cubesolutions.inapplib.view.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import org.jsoup.nodes.Element
import ru.cubesolutions.inapplib.utils.ConstUtils.DEFAULT_BLUE_COLOR
import ru.cubesolutions.inapplib.utils.ConstUtils.TAG_BACKGROUND_COLOR
import ru.cubesolutions.inapplib.utils.ConstUtils.TAG_COLOR
import ru.cubesolutions.inapplib.utils.ConstUtils.TAG_FONT_SIZE
import ru.cubesolutions.inapplib.utils.ConstUtils.TAG_HREF
import java.util.*

class AcceleraButton(
    context: Context,
    element: Element,
    action: (String) -> Unit,
) : AcceleraAbstractView(
    element = element,
    view = Button(context)
) {

    companion object {
        const val DEFAULT_BACKGROUND_COLOR = DEFAULT_BLUE_COLOR
    }

    private var onClickButton: (String) -> Unit = action
    private val density = context.resources.displayMetrics.density

    init {
        applyAttributes()
    }

    override fun applyAttributes() {
        if (getViewButton() == null) {
            return
        }
        setLayoutParams()
        setPadding()
        setUUID()
        setRounderCorner()
        setText()
        setTextColor()
        setFontSize()
        setOnClickListener()
    }

    private fun setLayoutParams() {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER

        getViewButton()?.layoutParams = layoutParams
    }

    private fun setUUID() {
        val uuidFromElement = UUID.nameUUIDFromBytes(element.toString().toByteArray())
        getViewButton()?.tag = uuidFromElement
    }

    private fun setText() {
        try {
            val textData = element.childNode(0).toString()
            val clearTextData = textData.replace("\n", "")
            getViewButton()?.text = clearTextData
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun setTextColor() {
        try {
            val color = element.attr(TAG_COLOR)
            if (color.isNotEmpty()) {
                val parseColor = Color.parseColor(color)
                getViewButton()?.setTextColor(parseColor)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun setFontSize() {
        try {
            val fontSize = element.attr(TAG_FONT_SIZE)
            if (fontSize.isNotEmpty()) {
                val result = fontSize.filter { it.isDigit() }.toInt()
                getViewButton()?.textSize = result.toFloat()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun createRounderCorner(height: Int?) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(getBackgroundColor())
        height?.let {
            shape.cornerRadius = if ((it / 2) > dpAsPixels(24)) {
                dpAsPixels(24).toFloat()
            } else {
                dpAsPixels((it / 2)).toFloat()
            }
        }
        getViewButton()?.let {
            ViewCompat.setBackground(it, shape)
        }
    }

    private fun setPadding() {
        getViewButton()?.setPadding(
            dpAsPixels(40), // left padding
            dpAsPixels(14), // top padding
            dpAsPixels(40), // right padding
            dpAsPixels(14), // bottom padding
        )
    }

    private fun dpAsPixels(sizeInDp: Int): Int {
        return ((sizeInDp * density) + 0.5).toInt()
    }

    private fun setRounderCorner() {
        getViewButton()?.post {
            createRounderCorner(
                height = getViewButton()?.height
            )
        }
    }

    private fun getBackgroundColor(): Int {
        return try {
            val backgroundColor = element.attr(TAG_BACKGROUND_COLOR)
            val parseColor = if (backgroundColor.isNotEmpty()) {
                Color.parseColor(backgroundColor)
            } else {
                Color.parseColor(DEFAULT_BACKGROUND_COLOR)
            }
            parseColor
        } catch (exception: Exception) {
            exception.printStackTrace()
            Color.parseColor(DEFAULT_BACKGROUND_COLOR)
        }
    }

    private fun setOnClickListener() {
        getViewButton()?.setOnClickListener {
            try {
                val href = element.attr(TAG_HREF)
                if (href.isNotEmpty()) {
                    onClickButton.invoke(href)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private fun getViewButton(): Button? {
        return this.view as? Button
    }
}