package com.cl.xdialog.choose

/**
 * name：cl
 * date：2024/02/02
 * desc：滚轮改变监听
 */
interface OnWheelChangedListener {
    fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int)
}
