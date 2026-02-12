package com.cl.xdialog.base

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 优化的ViewHolder
 * 特性：
 * 1. 高性能的视图缓存机制
 * 2. 简化的视图操作API
 * 3. 内存泄漏防护
 * 4. 兼容性支持
 */
class OptimizedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    private val viewCache = SparseArray<View>()

    /**
     * 高性能的findViewById，带缓存
     */
    fun <V : View> getView(id: Int): V? {
        var view = viewCache.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            if (view != null) {
                viewCache.put(id, view)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return view as? V
    }

    /**
     * 兼容旧API
     */
    fun <V : View> findViewById(id: Int): V? {
        return getView(id)
    }

    /**
     * 设置文本
     */
    fun setText(id: Int, text: CharSequence?) {
        getView<TextView>(id)?.text = text
    }

    /**
     * 获取文本
     */
    fun getText(id: Int): CharSequence? {
        return getView<TextView>(id)?.text
    }

    /**
     * 设置图片资源
     */
    fun setImageResource(id: Int, resId: Int) {
        getView<ImageView>(id)?.setImageResource(resId)
    }

    /**
     * 设置可见性
     */
    fun setVisibility(id: Int, visibility: Int) {
        getView<View>(id)?.visibility = visibility
    }

    /**
     * 设置点击监听器
     */
    fun setOnClickListener(id: Int, listener: View.OnClickListener?) {
        getView<View>(id)?.setOnClickListener(listener)
    }

    /**
     * 设置长按监听器
     */
    fun setOnLongClickListener(id: Int, listener: View.OnLongClickListener?) {
        getView<View>(id)?.setOnLongClickListener(listener)
    }

    /**
     * 设置背景
     */
    fun setBackgroundResource(id: Int, resId: Int) {
        getView<View>(id)?.setBackgroundResource(resId)
    }

    /**
     * 设置背景颜色
     */
    fun setBackgroundColor(id: Int, color: Int) {
        getView<View>(id)?.setBackgroundColor(color)
    }

    /**
     * 设置标签
     */
    fun setTag(id: Int, tag: Any?) {
        getView<View>(id)?.tag = tag
    }

    /**
     * 获取标签
     */
    fun getTag(id: Int): Any? {
        return getView<View>(id)?.tag
    }

    /**
     * 清理缓存
     */
    fun clearCache() {
        viewCache.clear()
    }

    /**
     * 获取根视图
     */
    fun getRootView(): View = itemView
}