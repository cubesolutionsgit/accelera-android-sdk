package ai.accelera.library.inapp.view.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import org.jsoup.nodes.Element
import ai.accelera.library.inapp.utils.ConstUtils.DEFAULT_BLUE_COLOR
import ai.accelera.library.inapp.utils.ConstUtils.DEFAULT_WHITE_COLOR
import ai.accelera.library.inapp.utils.ConstUtils.TAG_BACKGROUND_COLOR
import ai.accelera.library.inapp.utils.ConstUtils.TAG_COLOR
import ai.accelera.library.inapp.utils.ConstUtils.TAG_FONT_SIZE
import ai.accelera.library.inapp.utils.ConstUtils.TAG_HREF
import ai.accelera.library.inapp.utils.ConstUtils.TAG_MARGIN
import java.util.*

class InAppButton(
    context: Context,
    element: Element,
    action: (String) -> Unit,
) : InAppAbstractView(
    element = element,
    view = Button(context)
) {

    companion object {
        const val DEFAULT_BACKGROUND_COLOR = DEFAULT_BLUE_COLOR
        const val DEFAULT_TEXT_COLOR = DEFAULT_WHITE_COLOR
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
        setText()
        setTextColor()
        setFontSize()
        setRounderCorner()
        setOnClickListener()
    }

    private fun setLayoutParams() {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER

        try {
            val margin = element.attr(TAG_MARGIN)
            if (margin.isNotEmpty()) {
                val values = margin.split(" ").map { value ->
                    value.filter { it.isDigit() }.toInt()
                }
                val top = values.getOrNull(0) ?: 0
                val right = values.getOrNull(1) ?: top
                val bottom = values.getOrNull(2) ?: top
                val left = values.getOrNull(3) ?: right

                layoutParams.setMargins(
                    dpAsPixels(left),
                    dpAsPixels(top),
                    dpAsPixels(right),
                    dpAsPixels(bottom)
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

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
            getViewButton()?.setTextColor(getColor())
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

    private fun getColor(): Int {
        return try {
            val color = element.attr(TAG_COLOR)
            val parseColor = if (color.isNotEmpty()) {
                Color.parseColor(color)
            } else {
                Color.parseColor(DEFAULT_TEXT_COLOR)
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