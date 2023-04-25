package com.cl.xdialog.listener

import com.cl.xdialog.XDialog
import com.cl.xdialog.base.BindViewHolder

/**
 * name：cl
 * date：2023/4/24
 * desc：
 */
interface OnAdapterItemClickListener<T> {
     fun onItemClick(holder: BindViewHolder?, position: Int, t: T, tDialog: XDialog?)
}