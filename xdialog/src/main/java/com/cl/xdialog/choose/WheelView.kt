package com.cl.xdialog.choose

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.cl.xdialog.R
import java.util.Collections


/**
 * @author cncoderx
 */
open class WheelView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mCyclic: Boolean
    var mItemCount: Int
    private var mItemWidth: Int
    var mItemHeight: Int
    var mClipRectTop: Rect? = null
    var mClipRectMiddle: Rect? = null
    var mClipRectBottom: Rect? = null
    var mTextPaint: TextPaint
    var mSelectedTextPaint: TextPaint
    private var mDividerPaint: Paint
    private var mHighlightPaint: Paint
    var mScroller: WheelScroller
    private val mEntries: MutableList<CharSequence> = ArrayList()

    // 设置默认蒙版颜色,默认灰色
    private var mMiddleMaskColor: Int = Color.parseColor("#E1E8F9")


    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.X_WheelView)
        val cyclic = a.getBoolean(R.styleable.X_WheelView_wheelCyclic, false)
        val itemCount = a.getInt(R.styleable.X_WheelView_wheelItemCount, 9)
        val itemWidth = a.getDimensionPixelOffset(
            R.styleable.X_WheelView_wheelItemWidth,
            dp(R.dimen.x_wheel_item_width)
        )
        val itemHeight = a.getDimensionPixelOffset(
            R.styleable.X_WheelView_wheelItemHeight,
            dp(R.dimen.x_wheel_item_height)
        )
        val textSize = a.getDimensionPixelSize(
            R.styleable.X_WheelView_wheelTextSize,
            sp(R.dimen.x_wheel_text_size)
        )
        val textColor =
            a.getColor(R.styleable.X_WheelView_wheelTextColor, color(R.color.x_wheel_text_color))
        val selectedTextColor = a.getColor(
            R.styleable.X_WheelView_wheelSelectedTextColor,
            color(R.color.x_wheel_selected_text_color)
        )
        val dividerColor = a.getColor(
            R.styleable.X_WheelView_wheelDividerColor,
            color(R.color.x_wheel_divider_color)
        )
        val highlightColor = a.getColor(
            R.styleable.X_WheelView_wheelHighlightColor,
            color(R.color.x_wheel_highlight_color)
        )
        //中间蒙版图层颜色
        mMiddleMaskColor = a.getColor(
            R.styleable.X_WheelView_wheelMiddleLayerColor,
            mMiddleMaskColor
        )

        val entries = a.getTextArray(R.styleable.X_WheelView_wheelEntries)
        a.recycle()
        mCyclic = cyclic
        mItemCount = itemCount
        mItemWidth = itemWidth
        mItemHeight = itemHeight
        mTextPaint = TextPaint()
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = textSize.toFloat()
        mTextPaint.color = textColor
        mSelectedTextPaint = TextPaint()
        mSelectedTextPaint.isAntiAlias = true
        mSelectedTextPaint.textAlign = Paint.Align.CENTER
        mSelectedTextPaint.textSize = textSize.toFloat()
        mSelectedTextPaint.color = selectedTextColor
        mDividerPaint = Paint()
        mDividerPaint.isAntiAlias = true
        mDividerPaint.strokeWidth =
            resources.getDimensionPixelOffset(R.dimen.x_wheel_divider_height).toFloat()
        mDividerPaint.color = dividerColor
        mHighlightPaint = Paint()
        mHighlightPaint.isAntiAlias = true
        mHighlightPaint.style = Paint.Style.FILL
        mHighlightPaint.color = highlightColor
        if (entries != null && entries.isNotEmpty()) {
            mEntries.addAll(listOf(*entries))
        }
        mScroller = WheelScroller(context, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == MeasureSpec.EXACTLY
            && heightSpecMode == MeasureSpec.EXACTLY
        ) {
            setMeasuredDimension(widthSpecSize, heightSpecSize)
        } else if (widthSpecMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSpecSize, prefHeight)
        } else if (heightSpecMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(prefWidth, heightSpecSize)
        } else {
            setMeasuredDimension(prefWidth, prefHeight)
        }
        updateClipRect()
    }

    private fun updateClipRect() {
        val clipLeft = paddingLeft
        val clipRight = measuredWidth - paddingRight
        val clipTop = paddingTop
        val clipBottom = measuredHeight - paddingBottom
        val clipVMiddle = (clipTop + clipBottom) / 2
        mClipRectMiddle = Rect()
        mClipRectMiddle!!.left = clipLeft
        mClipRectMiddle!!.right = clipRight
        mClipRectMiddle!!.top = clipVMiddle - mItemHeight / 2
        mClipRectMiddle!!.bottom = clipVMiddle + mItemHeight / 2
        mClipRectTop = Rect()
        mClipRectTop!!.left = clipLeft
        mClipRectTop!!.right = clipRight
        mClipRectTop!!.top = clipTop
        mClipRectTop!!.bottom = clipVMiddle - mItemHeight / 2
        mClipRectBottom = Rect()
        mClipRectBottom!!.left = clipLeft
        mClipRectBottom!!.right = clipRight
        mClipRectBottom!!.top = clipVMiddle + mItemHeight / 2
        mClipRectBottom!!.bottom = clipBottom
    }

    private fun dp(resId: Int): Int {
        return resources.getDimensionPixelOffset(resId)
    }

    private fun sp(resId: Int): Int {
        return resources.getDimensionPixelSize(resId)
    }

    fun color(resId: Int): Int {
        return resources.getColor(resId)
    }

    private val prefWidth: Int
        /**
         * @return 控件的预算宽度
         */
        get() {
            val paddingHorizontal = paddingLeft + paddingRight
            return paddingHorizontal + mItemWidth
        }
    open val prefHeight: Int
        /**
         * @return 控件的预算高度
         */
        get() {
            val paddingVertical = paddingTop + paddingBottom
            return paddingVertical + mItemHeight * mItemCount
        }

    override fun onDraw(canvas: Canvas) {
        // 新增绘制中间蒙版的方法
        drawMiddleMask(canvas)
        drawHighlight(canvas)
        drawItems(canvas)
        drawDivider(canvas)
    }

    private fun drawMiddleMask(canvas: Canvas) {
        mClipRectMiddle?.let {
            canvas.drawRect(it, Paint().apply {
                isAntiAlias = true
                color = mMiddleMaskColor
            })
        }
    }

    private fun drawItems(canvas: Canvas) {
        val index = mScroller.itemIndex
        val offset = mScroller.itemOffset
        val hf = (mItemCount + 1) / 2
        val minIdx: Int
        val maxIdx: Int
        if (offset < 0) {
            minIdx = index - hf - 1
            maxIdx = index + hf
        } else if (offset > 0) {
            minIdx = index - hf
            maxIdx = index + hf + 1
        } else {
            minIdx = index - hf
            maxIdx = index + hf
        }
        for (i in minIdx until maxIdx) {
            drawItem(canvas, i, offset)
        }
    }

    protected open fun drawItem(canvas: Canvas, index: Int, offset: Int) {
        val text = getCharSequence(index) ?: return
        val centerX = mClipRectMiddle!!.centerX()
        val centerY = mClipRectMiddle!!.centerY()

        // 和中间选项的距离
        val range = (index - mScroller.itemIndex) * mItemHeight - offset
        val fontMetrics = mTextPaint.fontMetrics
        val baseline = ((fontMetrics.top + fontMetrics.bottom) / 2).toInt()

        // 绘制与下分界线相交的文字
        if (range in 1 until mItemHeight) {
            canvas.save()
            canvas.clipRect(mClipRectMiddle!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mSelectedTextPaint
            )
            canvas.restore()
            canvas.save()
            canvas.clipRect(mClipRectBottom!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mTextPaint
            )
            canvas.restore()
        } else if (range >= mItemHeight) {
            canvas.save()
            canvas.clipRect(mClipRectBottom!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mTextPaint
            )
            canvas.restore()
        } else if (range < 0 && range > -mItemHeight) {
            canvas.save()
            canvas.clipRect(mClipRectMiddle!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mSelectedTextPaint
            )
            canvas.restore()
            canvas.save()
            canvas.clipRect(mClipRectTop!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mTextPaint
            )
            canvas.restore()
        } else if (range <= -mItemHeight) {
            canvas.save()
            canvas.clipRect(mClipRectTop!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mTextPaint
            )
            canvas.restore()
        } else {
            canvas.save()
            canvas.clipRect(mClipRectMiddle!!)
            canvas.drawText(
                text,
                0,
                text.length,
                centerX.toFloat(),
                (centerY + range - baseline).toFloat(),
                mSelectedTextPaint
            )
            canvas.restore()
        }
    }

    fun getCharSequence(index: Int): CharSequence? {
        val size = mEntries.size
        if (size == 0) return null
        var text: CharSequence? = null
        if (isCyclic) {
            var i = index % size
            if (i < 0) {
                i += size
            }
            text = mEntries[i]
        } else {
            if (index in 0 until size) {
                text = mEntries[index]
            }
        }
        return text
    }

    private fun drawHighlight(canvas: Canvas) {
        canvas.drawRect(mClipRectMiddle!!, mHighlightPaint)
    }

    private fun drawDivider(canvas: Canvas) {
        // 绘制上层分割线
        canvas.drawLine(
            mClipRectMiddle!!.left.toFloat(),
            mClipRectMiddle!!.top.toFloat(),
            mClipRectMiddle!!.right.toFloat(),
            mClipRectMiddle!!.top.toFloat(),
            mDividerPaint
        )

        // 绘制下层分割线
        canvas.drawLine(
            mClipRectMiddle!!.left.toFloat(),
            mClipRectMiddle!!.bottom.toFloat(),
            mClipRectMiddle!!.right.toFloat(),
            mClipRectMiddle!!.bottom.toFloat(),
            mDividerPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mScroller.onTouchEvent(event)
    }

    override fun computeScroll() {
        mScroller.computeScroll()
    }

    var isCyclic: Boolean
        get() = mCyclic
        set(cyclic) {
            mCyclic = cyclic
            mScroller.reset()
            invalidate()
        }
    val textSize: Float
        get() = mTextPaint.textSize

    /**
     * 设置文字大小
     */
    fun setTextSize(textSize: Int) {
        mTextPaint.textSize = textSize.toFloat()
        mSelectedTextPaint.textSize = textSize.toFloat()
        invalidate()
    }

    /**
     * 设置中间蒙版颜色
     */
    fun setMiddleMaskColor(color: Int) {
        mMiddleMaskColor = color
        invalidate()
    }

    /**
     * 设置中间边框颜色
     */
    fun setDividerColor(dividerColor: Int) {
        mDividerPaint.color = dividerColor
        invalidate()
    }

    /**
     * 设置没有选中时的文字颜色
     */
    fun setNoTextColor(noTextColor: Int) {
        mTextPaint.color = noTextColor
        invalidate()
    }

    /**
     * 设置选中时的文字颜色
     */
    fun setWhenSelectedTextColor(textColor: Int) {
        mSelectedTextPaint.color = textColor
        invalidate()
    }

    var textColor: Int
        get() = mTextPaint.color
        set(color) {
            mTextPaint.color = color
            invalidate()
        }

    var selectedTextColor: Int
        get() = mSelectedTextPaint.color
        set(color) {
            mSelectedTextPaint.color = color
            invalidate()
        }
    val itemSize: Int
        get() = mEntries.size

    fun getItem(index: Int): CharSequence? {
        return if (index < 0 || index >= mEntries.size) null else mEntries[index]
    }

    val currentItem: CharSequence?
        get() = getItem(currentIndex)
    private var currentIndex: Int
        get() = mScroller.currentIndex
        set(index) {
            setCurrentIndex(index, false)
        }

    private fun setCurrentIndex(index: Int, animated: Boolean) {
        mScroller.setCurrentIndex(index, animated)
    }

    fun setEntries(vararg entries: CharSequence) {
        mEntries.clear()
        if (entries.isNotEmpty()) {
            Collections.addAll(mEntries, *entries)
        }
        mScroller.reset()
        invalidate()
    }

    fun setEntries(entries: Collection<CharSequence>?) {
        mEntries.clear()
        if (!entries.isNullOrEmpty()) {
            mEntries.addAll(entries)
        }
        mScroller.reset()
        invalidate()
    }

    var onWheelChangedListener: OnWheelChangedListener?
        get() = mScroller.onWheelChangedListener
        set(onWheelChangedListener) {
            mScroller.onWheelChangedListener = onWheelChangedListener
        }
}
