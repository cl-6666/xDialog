package com.cl.xdialog.base

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.util.Linkify
import android.util.SparseArray
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.view.animation.AlphaAnimation
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Checkable
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.cl.xdialog.XDialog

/**
 * 项目：xDialog
 * 版权：蒲公英公司 版权所有
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 * 借鉴RecyclerView.Adapter的ViewHolder写法
 * 将Dialog的根布局传入,主要处理点击方法
 * 修订历史：
 */
class BindViewHolder : RecyclerView.ViewHolder {
    var bindView: View
    private var views: SparseArray<View?>
    private var dialog: XDialog? = null

    constructor(view: View) : super(view) {
        bindView = view
        views = SparseArray()
    }

    constructor(view: View, dialog: XDialog?) : super(view) {
        bindView = view
        this.dialog = dialog
        views = SparseArray()
    }

    fun <T : View?> getView(@IdRes viewId: Int): T? {
        var view = views[viewId]
        if (view == null) {
            view = bindView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T?
    }

    fun addOnClickListener(@IdRes viewId: Int): BindViewHolder {
        val view = getView<View>(viewId)
        if (view != null) {
            if (!view.isClickable) {
                view.isClickable = true
            }
            view.setOnClickListener(View.OnClickListener { v: View? ->
                if (dialog!!.onViewClickListener != null) {
                    dialog!!.onViewClickListener.onViewClick(this@BindViewHolder, view, dialog)
                }
            })
        }
        return this
    }

    fun setText(@IdRes viewId: Int, value: CharSequence?): BindViewHolder {
        val view = getView<TextView>(viewId)!!
        view.text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BindViewHolder {
        val view = getView<TextView>(viewId)!!
        view.setText(strId)
        return this
    }

    /**
     * Will set the image of an ImageView from a resource id.
     *
     * @param viewId     The view id.
     * @param imageResId The image resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BindViewHolder {
        val view = getView<ImageView>(viewId)!!
        view.setImageResource(imageResId)
        return this
    }

    /**
     * Will set background color of a view.
     *
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The BindViewHolder for chaining.
     */
    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setBackgroundColor(color)
        return this
    }

    /**
     * Will set background of a view.
     *
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BindViewHolder for chaining.
     */
    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * Will set text color of a TextView.
     *
     * @param viewId    The view id.
     * @param textColor The text color (not a resource id).
     * @return The BindViewHolder for chaining.
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): BindViewHolder {
        val view = getView<TextView>(viewId)!!
        view.setTextColor(textColor)
        return this
    }

    /**
     * Will set the image of an ImageView from a drawable.
     *
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The BindViewHolder for chaining.
     */
    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BindViewHolder {
        val view = getView<ImageView>(viewId)!!
        view.setImageDrawable(drawable)
        return this
    }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BindViewHolder {
        val view = getView<ImageView>(viewId)!!
        view.setImageBitmap(bitmap)
        return this
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    fun setAlpha(@IdRes viewId: Int, value: Float): BindViewHolder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId)!!.alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId)!!.startAnimation(alpha)
        }
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BindViewHolder for chaining.
     */
    fun setGone(@IdRes viewId: Int, visible: Boolean): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for INVISIBLE.
     * @return The BindViewHolder for chaining.
     */
    fun setVisible(@IdRes viewId: Int, visible: Boolean): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    fun setVisibility(@IdRes viewId: Int, visible: Int): BindViewHolder {
        getView<View>(viewId)!!.visibility = visible
        return this
    }

    /**
     * Add links into a TextView.
     *
     * @param viewId The id of the TextView to linkify.
     * @return The BindViewHolder for chaining.
     */
    fun linkify(@IdRes viewId: Int): BindViewHolder {
        val view = getView<TextView>(viewId)!!
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    /**
     * Apply the typeface to the given viewId, and enable subpixel rendering.
     */
    fun setTypeface(@IdRes viewId: Int, typeface: Typeface?): BindViewHolder {
        val view = getView<TextView>(viewId)!!
        view.typeface = typeface
        view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        return this
    }

    /**
     * Apply the typeface to all the given viewIds, and enable subpixel rendering.
     */
    fun setTypeface(typeface: Typeface?, vararg viewIds: Int): BindViewHolder {
        for (viewId in viewIds) {
            val view = getView<TextView>(viewId)!!
            view.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    /**
     * Sets the progress of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @return The BindViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int): BindViewHolder {
        val view = getView<ProgressBar>(viewId)!!
        view.progress = progress
        return this
    }

    /**
     * Sets the progress and max of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @param max      The max value of a ProgressBar.
     * @return The BindViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BindViewHolder {
        val view = getView<ProgressBar>(viewId)!!
        view.max = max
        view.progress = progress
        return this
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     *
     * @param viewId The view id.
     * @param max    The max value of a ProgressBar.
     * @return The BindViewHolder for chaining.
     */
    fun setMax(@IdRes viewId: Int, max: Int): BindViewHolder {
        val view = getView<ProgressBar>(viewId)!!
        view.max = max
        return this
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @return The BindViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float): BindViewHolder {
        val view = getView<RatingBar>(viewId)!!
        view.rating = rating
        return this
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @param max    The range of the RatingBar to 0...max.
     * @return The BindViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BindViewHolder {
        val view = getView<RatingBar>(viewId)!!
        view.max = max
        view.rating = rating
        return this
    }

    /**
     * Sets the on click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BindViewHolder for chaining.
     */
    @Deprecated("")
    fun setOnClickListener(@IdRes viewId: Int, listener: View.OnClickListener?): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setOnClickListener(listener)
        return this
    }

    /**
     * Sets the on touch listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on touch listener;
     * @return The BindViewHolder for chaining.
     */
    @Deprecated("")
    fun setOnTouchListener(@IdRes viewId: Int, listener: OnTouchListener?): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setOnTouchListener(listener)
        return this
    }

    /**
     * Sets the on long click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The BindViewHolder for chaining.
     */
    @Deprecated("")
    fun setOnLongClickListener(@IdRes viewId: Int, listener: OnLongClickListener?): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setOnLongClickListener(listener)
        return this
    }

    /**
     * Sets the listview or gridview's item click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item on click listener;
     * @return The BindViewHolder for chaining.
     * Please use [.addOnClickListener] (int)} (adapter.setOnItemChildClickListener(listener))}
     */
    @Deprecated("")
    fun setOnItemClickListener(@IdRes viewId: Int, listener: OnItemClickListener?): BindViewHolder {
        val view = getView<AdapterView<*>>(viewId)!!
        view.onItemClickListener = listener
        return this
    }

    /**
     * Sets the listview or gridview's item long click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item long click listener;
     * @return The BindViewHolder for chaining.
     */
    fun setOnItemLongClickListener(
        @IdRes viewId: Int,
        listener: OnItemLongClickListener?
    ): BindViewHolder {
        val view = getView<AdapterView<*>>(viewId)!!
        view.onItemLongClickListener = listener
        return this
    }

    /**
     * Sets the listview or gridview's item selected click listener of the view
     *
     * @param viewId   The view id.
     * @param listener The item selected click listener;
     * @return The BindViewHolder for chaining.
     */
    fun setOnItemSelectedClickListener(
        @IdRes viewId: Int,
        listener: OnItemSelectedListener?
    ): BindViewHolder {
        val view = getView<AdapterView<*>>(viewId)!!
        view.onItemSelectedListener = listener
        return this
    }

    /**
     * Sets the on checked change listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The checked change listener of compound button.
     * @return The BindViewHolder for chaining.
     */
    fun setOnCheckedChangeListener(
        @IdRes viewId: Int,
        listener: CompoundButton.OnCheckedChangeListener?
    ): BindViewHolder {
        val view = getView<CompoundButton>(viewId)!!
        view.setOnCheckedChangeListener(listener)
        return this
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The BindViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, tag: Any?): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.tag = tag
        return this
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The BindViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, key: Int, tag: Any?): BindViewHolder {
        val view = getView<View>(viewId)!!
        view.setTag(key, tag)
        return this
    }

    /**
     * Sets the checked status of a checkable.
     *
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The BindViewHolder for chaining.
     */
    fun setChecked(@IdRes viewId: Int, checked: Boolean): BindViewHolder {
        val view = getView<View>(viewId)!!
        // View unable cast to Checkable
        if (view is Checkable) {
            (view as Checkable).isChecked = checked
        }
        return this
    }

    /**
     * Sets the adapter of a adapter view.
     *
     * @param viewId  The view id.
     * @param adapter The adapter;
     * @return The BindViewHolder for chaining.
     */
    fun setAdapter(@IdRes viewId: Int, adapter: Adapter?): BindViewHolder {
        val view = getView<AdapterView<*>>(viewId)!!
        view.adapter=adapter
        return this
    }
}