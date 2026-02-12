package com.cl.xdialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cl.xdialog.XDialogOptimized
import com.cl.xdialog.base.OptimizedViewHolder
import com.cl.xdialog.listener.OnOptimizedAdapterItemClickListener
import java.lang.ref.WeakReference

/**
 * 优化的适配器基类
 * 特性：
 * 1. 使用WeakReference避免内存泄漏
 * 2. ViewHolder复用优化
 * 3. 支持多种布局类型
 * 4. 高性能的视图缓存机制
 */
abstract class XOptimizedAdapter<T> @JvmOverloads constructor(
    @LayoutRes private val layoutRes: Int,
    private val data: List<T>,
    @LayoutRes private val emptyLayoutRes: Int? = null
) : RecyclerView.Adapter<OptimizedViewHolder>() {

    private var dialogRef: WeakReference<XDialogOptimized>? = null
    private var itemClickListener: OnOptimizedAdapterItemClickListener<T>? = null
    
    companion object {
        private const val VIEW_TYPE_ITEM = 1
        private const val VIEW_TYPE_EMPTY = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptimizedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            VIEW_TYPE_EMPTY -> {
                val emptyLayout = emptyLayoutRes ?: android.R.layout.simple_list_item_1
                val inflatedView = inflater.inflate(emptyLayout, parent, false)
                inflatedView ?: throw IllegalStateException("Failed to inflate empty view layout: $emptyLayout")
            }
            else -> {
                val inflatedView = inflater.inflate(layoutRes, parent, false)
                inflatedView ?: throw IllegalStateException("Failed to inflate item layout: $layoutRes")
            }
        }
        return OptimizedViewHolder(view)
    }

    override fun onBindViewHolder(holder: OptimizedViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                val item = data[position]
                onBind(holder, position, item)
                
                // 设置点击监听
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(
                        holder,
                        position,
                        item,
                        dialogRef?.get()
                    )
                }
            }
            VIEW_TYPE_EMPTY -> {
                onBindEmpty(holder)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (data.isEmpty()) 1 else data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onViewRecycled(holder: OptimizedViewHolder) {
        super.onViewRecycled(holder)
        // 清理ViewHolder缓存，避免内存泄漏
        holder.clearCache()
    }

    /**
     * 绑定数据项
     */
    protected abstract fun onBind(holder: OptimizedViewHolder, position: Int, item: T)

    /**
     * 绑定空视图
     */
    protected open fun onBindEmpty(holder: OptimizedViewHolder) {
        // 默认实现，子类可重写
    }

    /**
     * 设置Dialog引用
     */
    fun setDialog(dialog: XDialogOptimized?) {
        dialogRef = dialog?.let { WeakReference(it) }
    }

    /**
     * 设置Dialog引用（兼容方法）
     */
    fun setTDialog(dialog: XDialogOptimized?) {
        setDialog(dialog)
    }

    /**
     * 设置项点击监听器
     */
    fun setOnAdapterItemClickListener(listener: OnOptimizedAdapterItemClickListener<T>?) {
        itemClickListener = listener
    }

    /**
     * 获取数据项
     */
    fun getItem(position: Int): T? {
        return if (position in data.indices) data[position] else null
    }

    /**
     * 检查是否为空
     */
    val isEmpty: Boolean get() = data.isEmpty()
}
