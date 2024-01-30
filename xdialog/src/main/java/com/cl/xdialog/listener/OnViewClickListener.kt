package com.cl.xdialog.listener

import android.view.View
import com.cl.xdialog.XDialog
import com.cl.xdialog.base.BindViewHolder

/**
 * 项目：xDialog
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 * 修订历史：
 */
interface OnViewClickListener {
    fun onViewClick(viewHolder: BindViewHolder?, view: View?, xDialog: XDialog?)
}