package com.cl.xdialog.listener

import com.cl.xdialog.XDialogOptimized
import com.cl.xdialog.base.OptimizedViewHolder

/**
 * 优化版本的适配器项点击监听器
 * 支持XDialogOptimized类型
 */
interface OnOptimizedAdapterItemClickListener<T> {
    fun onItemClick(holder: OptimizedViewHolder?, position: Int, item: T, dialog: XDialogOptimized?)
}