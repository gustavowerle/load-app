package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var buttonText: String
    private var buttonTextColor: Int = 0
    private var buttonBackgroundColor: Int = 0
    private var buttonProgress: Float = 0f

    private var valueAnimator = ValueAnimator()
    private val textRect = Rect()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55f
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        buttonProgress = animatedValue as Float
                        invalidate()
                    }
                    duration = 3000
                    start()
                }
                setButtonShouldBeEnabled(false)
            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_download)
                valueAnimator.cancel()
                buttonProgress = 0f
                setButtonShouldBeEnabled(true)
            }

            ButtonState.Clicked -> {
            }
        }
        invalidate()
    }

    init {
        buttonText = resources.getString(R.string.button_download)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cornerRadius = 10.0f
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        canvas.drawColor(buttonBackgroundColor)
        paint.getTextBounds(buttonText, 0, buttonText.length, textRect)

        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRoundRect(
            0f,
            0f,
            width,
            height,
            cornerRadius,
            cornerRadius,
            paint
        )

        if (buttonState == ButtonState.Loading) {
            var progressValue = buttonProgress * measuredWidth.toFloat()
            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas.drawRoundRect(
                0f,
                0f,
                progressValue,
                height,
                cornerRadius,
                cornerRadius,
                paint
            )

            val arcDiameter = cornerRadius * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressValue = buttonProgress * 360f

            paint.color = Color.YELLOW
            canvas.drawArc(
                paddingStart + arcDiameter,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize,
                arcRectSize,
                0f,
                progressValue,
                true,
                paint
            )
        }
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - textRect.centerY()

        paint.color = Color.WHITE
        canvas.drawText(buttonText, centerX, centerY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        setMeasuredDimension(width, height)
    }

    private fun setButtonShouldBeEnabled(should: Boolean) {
        isEnabled = should
    }

    fun setNewButtonState(state: ButtonState) {
        buttonState = state
    }
}