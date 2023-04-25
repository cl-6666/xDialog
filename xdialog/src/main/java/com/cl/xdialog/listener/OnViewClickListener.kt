package com.cl.xdialog.listener;

import android.view.View;

import com.cl.xdialog.XDialog;
import com.cl.xdialog.base.BindViewHolder;


/**
 * 项目：xDialog
 * 版权：蒲公英公司 版权所有
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 * 修订历史：
 */
public interface OnViewClickListener {
    void onViewClick(BindViewHolder viewHolder, View view, XDialog xDialog);
}
