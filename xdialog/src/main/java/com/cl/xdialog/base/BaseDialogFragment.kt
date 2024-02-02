package com.cl.xdialog.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * 项目：xDialog
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 *
 * DialogFragment的基类
 * 1.系统默认onCreateDialog方法返回一个Dialog对象,对其不做处理
 * 2.主要操作onCreateView方法
 * 因为DialogFragment继承自Fragment,所以可以在onCreteView()方法返回xml布局,
 * 该布局在onActivityCreated()方法中,设置给系统之前创建的Dialog对象
 * //           @Override
 * //            public void onActivityCreated(Bundle savedInstanceState) {
 * //                super.onActivityCreated(savedInstanceState);
 * //
 * //                if (!mShowsDialog) {
 * //                return;
 * //                }
 * //
 * //                View view = getView();
 * //                if (view != null) {
 * //                if (view.getParent() != null) {
 * //                throw new IllegalStateException(
 * //                "DialogFragment can not be attached to a container view");
 * //                }
 * //                mDialog.setContentView(view);
 * //                }
 * //           }
 * 3.对应使用Dialog不同部分包括
 * a.xml布局
 * b.宽高
 * c.位置
 * d.背景色
 * e.透明度
 * f.是否可以点击空白处隐藏
 * 控制方法在onStart处理,
 * 4.暴露方法:界面中控件处理和点击事件处理
 * 5.监听回调,很多弹窗需要输入信息,然后将输入的信息通过回调的方法返回
 * 6.当设备Configure属性变化时,数据保存和恢复处理
 */
abstract class BaseDialogFragment : DialogFragment() {
    protected abstract val layoutRes: Int
    protected abstract fun bindView(view: View?)
    protected abstract val dialogView: View?
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        if (layoutRes > 0) {
            view = inflater.inflate(layoutRes, container, false)
        }
        if (dialogView != null) {
            view = dialogView
        }
        bindView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //去除Dialog默认头部
        val dialog = dialog
        //        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog!!.setCanceledOnTouchOutside(isCancelableOutside)
        if (dialog.window != null && dialogAnimationRes > 0) {
            dialog.window!!.setWindowAnimations(dialogAnimationRes)
        }
        if (onKeyListener != null) {
            dialog.setOnKeyListener(onKeyListener)
        }
    }

    protected open val onKeyListener: DialogInterface.OnKeyListener?
        get() = null

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        if (window != null) {
            //设置窗体背景色透明
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //设置宽高
            val layoutParams = window.attributes
            if (dialogWidth > 0) {
                layoutParams.width = dialogWidth
            } else {
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            }
            if (dialogHeight > 0) {
                layoutParams.height = dialogHeight
            } else {
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            //透明度
            layoutParams.dimAmount = dimAmount
            //位置
            layoutParams.gravity = gravity
            window.attributes = layoutParams
        }
    }

    //默认弹窗位置为中心
    open val gravity: Int
        get() = Gravity.CENTER

    //默认宽高为包裹内容
    open val dialogHeight: Int
        get() = WindowManager.LayoutParams.WRAP_CONTENT
    open val dialogWidth: Int
        get() = WindowManager.LayoutParams.WRAP_CONTENT
    open val fragmentTag: String?
        get() = TAG

    fun show(fragmentManager: FragmentManager?) {
        show(fragmentManager!!, fragmentTag)
    }

    protected open val isCancelableOutside: Boolean
        get() = true

    //获取弹窗显示动画,子类实现
    protected open val dialogAnimationRes: Int
        get() = 0

    companion object {
        const val TAG = "XDialog"

        //默认透明度为0.2
        const val dimAmount = 0.2f
        //获取设备屏幕宽度
        @JvmStatic
        fun getScreenWidth(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        //获取设备屏幕高度
        @JvmStatic
        fun getScreenHeight(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }
    }
}