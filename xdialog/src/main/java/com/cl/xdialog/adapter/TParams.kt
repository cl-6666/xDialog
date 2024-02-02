package com.cl.xdialog.adapter

import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cl.xdialog.R
import com.cl.xdialog.base.XBaseAdapter
import com.cl.xdialog.base.XController
import com.cl.xdialog.listener.OnAdapterItemClickListener
import com.cl.xdialog.listener.OnBindViewListener
import com.cl.xdialog.listener.OnViewClickListener


/**
 * name：cl
 * date：2023/4/24
 * desc：
 */
class TParams<T : XBaseAdapter<*>?> {
    @JvmField
    var mFragmentManager: FragmentManager? = null

    @JvmField
    var mLayoutRes = 0

    @JvmField
    var mWidth = 0

    @JvmField
    var mHeight = 0

    @JvmField
    var mDimAmount = 0.2f

    @JvmField
    var mGravity = Gravity.CENTER

    @JvmField
    var mTag = "XDialog"

    @JvmField
    var ids: IntArray? = null

    @JvmField
    var mIsCancelableOutside = true

    @JvmField
    var mOnViewClickListener: OnViewClickListener? = null

    @JvmField
    var bindViewListener: OnBindViewListener? = null

    /**
     * 弹窗动画
     */
    @JvmField
    var mDialogAnimationRes = 0

    /**
     * 列表适配器
     */
    var adapter: T? = null
    var adapterItemClickListener: OnAdapterItemClickListener<*>? = null
    var listLayoutRes = 0
    /**
     * 默认RecyclerView的列表方向为垂直方向
     */
    var orientation : LinearLayoutManager? = null

    /**
     * 默认RecyclerView的列表方向为垂直方向
     */
    var spanCount : GridLayoutManager? = null

    /**
     * 直接使用传入进来的View,而不需要通过解析Xml
     */
    @JvmField
    var mDialogView
            : View? = null

    @JvmField
    var mOnDismissListener: DialogInterface.OnDismissListener? = null

    @JvmField
    var mKeyListener: DialogInterface.OnKeyListener? = null

    fun apply(tController: XController<T>) {
        tController.fragmentManager = mFragmentManager
        if (mLayoutRes > 0) {
            tController.layoutRes = mLayoutRes
        }
        if (mDialogView != null) {
            tController.dialogView = mDialogView
        }
        if (mWidth > 0) {
            tController.width = mWidth
        }
        if (mHeight > 0) {
            tController.height = mHeight
        }
        tController.dimAmount = mDimAmount
        tController.gravity = mGravity
        tController.tag = mTag
        if (ids != null) {
            tController.ids = ids
        }
        tController.isCancelableOutside = mIsCancelableOutside
        tController.onViewClickListener = mOnViewClickListener
        tController.onBindViewListener = bindViewListener
        tController.onDismissListener = mOnDismissListener
        tController.dialogAnimationRes = mDialogAnimationRes
        tController.onKeyListener = mKeyListener
        if (adapter != null) {
            tController.setAdapter(adapter!!)
            if (listLayoutRes <= 0) { //使用默认的布局
                tController.layoutRes = R.layout.dialog_recycler
            } else {
                tController.layoutRes = listLayoutRes
            }
            tController.orientation = orientation
            tController.spanCount = spanCount
        } else {
            require(!(tController.layoutRes <= 0 && tController.dialogView == null)) { "请先调用setLayoutRes()方法设置弹窗所需的xml布局!" }
        }
        if (adapterItemClickListener != null) {
            tController.adapterItemClickListener = adapterItemClickListener
        }
        //如果宽高都没有设置,则默认给弹窗提供宽度为600
        if (tController.width <= 0 && tController.height <= 0) {
            tController.width = 600
        }
    }
}