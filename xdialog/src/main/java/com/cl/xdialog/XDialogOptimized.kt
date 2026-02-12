package com.cl.xdialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

/**
 * 优化版本的XDialog
 * 特性：
 * 1. 简洁的API接口设计，支持链式调用
 * 2. 高性能渲染机制，减少DOM操作
 * 3. 优化内存管理，避免内存泄漏
 * 4. 完善的事件回调机制
 * 5. 轻量化代码设计
 */
open class XDialogOptimized : DialogFragment(), DefaultLifecycleObserver {

    // 使用WeakReference避免内存泄漏
    private var contextRef: WeakReference<Context>? = null
    protected var config: DialogConfig = DialogConfig()
    private var viewCache: SparseArray<View> = SparseArray()
    private var isViewBound = false

    companion object {
        private const val KEY_CONFIG = "dialog_config"
        
        /**
         * 创建Dialog实例的静态方法
         */
        @JvmStatic
        fun create(fragmentManager: FragmentManager): Builder {
            return Builder(fragmentManager)
        }

        internal fun showAuto(
            fragmentManager: FragmentManager,
            dialog: DialogFragment,
            tag: String?
        ): DialogFragment {
            if (!fragmentManager.isStateSaved) {
                dialog.show(fragmentManager, tag)
                return dialog
            }
            if (fragmentManager.isDestroyed) {
                return dialog
            }
            val callback = object : FragmentManager.FragmentLifecycleCallbacks() {
                private fun attemptShow(fm: FragmentManager) {
                    if (!fm.isStateSaved && !dialog.isAdded) {
                        dialog.show(fm, tag)
                    }
                    if (!fm.isStateSaved) {
                        fm.unregisterFragmentLifecycleCallbacks(this)
                    }
                }

                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    attemptShow(fm)
                }

                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    attemptShow(fm)
                }
            }
            fragmentManager.registerFragmentLifecycleCallbacks(callback, true)
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<DialogFragment>.onCreate(savedInstanceState)

        // 恢复配置
        savedInstanceState?.let {
            config = it.getParcelable(KEY_CONFIG) ?: DialogConfig()
        }
        
        // 注册生命周期观察者
        lifecycle.addObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contextRef = WeakReference(requireContext())

        val customView = config.customView
        if (customView != null) {
            (customView.parent as? ViewGroup)?.removeView(customView)
            return customView
        }
        if (config.layoutRes > 0) {
            return inflater.inflate(config.layoutRes, container, false)
        }
        throw IllegalStateException("必须设置布局资源或自定义View")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 配置Dialog属性
        dialog?.apply {
            setCanceledOnTouchOutside(config.cancelableOutside)
            setOnKeyListener(config.keyListener)
            
            window?.apply {
                if (config.animationRes > 0) {
                    setWindowAnimations(config.animationRes)
                }
            }
        }
        
        // 绑定视图
        bindViews(view)
        isViewBound = true
    }

    override fun onStart() {
        super<DialogFragment>.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.apply {
            // 设置窗口属性
            attributes = attributes.apply {
                width = if (config.width > 0) config.width else ViewGroup.LayoutParams.WRAP_CONTENT
                height = if (config.height > 0) config.height else ViewGroup.LayoutParams.WRAP_CONTENT
                gravity = config.gravity
                dimAmount = config.dimAmount
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_CONFIG, config)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config.dismissListener?.onDismiss(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        config.bindListener = null
        config.clickListener = null
        config.dismissListener = null
        config.keyListener = null
        config.clickIds.clear()
        config.customView = null
        clearResources()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        // 清理资源，避免内存泄漏
        clearResources()
    }

    /**
     * 高性能视图绑定
     */
    private fun bindViews(rootView: View) {
        // 缓存视图引用，避免重复findViewById
        config.clickIds.forEach { id ->
            val view = rootView.findViewById<View>(id)
            view?.let {
                viewCache.put(id, it)
                it.setOnClickListener { clickedView ->
                    config.clickListener?.invoke(ViewHolder(rootView, this), clickedView, this)
                }
            }
        }
        
        // 执行视图绑定回调
        config.bindListener?.invoke(ViewHolder(rootView, this))
    }

    /**
     * 清理资源
     */
    private fun clearResources() {
        viewCache.clear()
        contextRef?.clear()
        contextRef = null
        isViewBound = false
    }

    /**
     * 获取缓存的视图
     */
    fun <T : View> getCachedView(id: Int): T? {
        @Suppress("UNCHECKED_CAST")
        return viewCache.get(id) as? T
    }

    /**
     * Builder类 - 提供流畅的链式调用API
     */
    class Builder(private val fragmentManager: FragmentManager) {
        internal val config = DialogConfig()

        fun layout(@LayoutRes layoutRes: Int) = apply {
            config.layoutRes = layoutRes
        }

        fun customView(view: View) = apply {
            config.customView = view
        }

        fun size(width: Int, height: Int) = apply {
            config.width = width
            config.height = height
        }

        fun width(width: Int) = apply {
            config.width = width
        }

        fun widthDp(dp: Int) = apply {
            config.width = (dp * android.content.res.Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        }

        fun height(height: Int) = apply {
            config.height = height
        }

        fun heightDp(dp: Int) = apply {
            config.height = (dp * android.content.res.Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        }

        fun widthPercent(context: Context, percent: Float) = apply {
            config.width = (context.resources.displayMetrics.widthPixels * percent).toInt()
        }

        fun heightPercent(context: Context, percent: Float) = apply {
            config.height = (context.resources.displayMetrics.heightPixels * percent).toInt()
        }

        fun gravity(gravity: Int) = apply {
            config.gravity = gravity
        }

        fun dimAmount(dimAmount: Float) = apply {
            config.dimAmount = dimAmount
        }

        fun cancelableOutside(cancelable: Boolean) = apply {
            config.cancelableOutside = cancelable
        }

        fun animation(animationRes: Int) = apply {
            config.animationRes = animationRes
        }

        fun tag(tag: String) = apply {
            config.tag = tag
        }

        fun onBind(listener: (ViewHolder) -> Unit) = apply {
            config.bindListener = listener
        }

        fun onClick(vararg ids: Int, listener: (ViewHolder, View, XDialogOptimized) -> Unit) = apply {
            config.clickIds.addAll(ids.toList())
            config.clickListener = listener
        }

        fun onDismiss(listener: DialogInterface.OnDismissListener) = apply {
            config.dismissListener = listener
        }

        fun onKey(listener: DialogInterface.OnKeyListener) = apply {
            config.keyListener = listener
        }

        /**
         * 创建并显示Dialog
         */
        fun show(): XDialogOptimized {
            val dialog = build()
            showAuto(fragmentManager, dialog, this.config.tag.takeIf { it.isNotEmpty() })
            return dialog
        }

        /**
         * 仅创建Dialog实例
         */
        fun build(): XDialogOptimized {
            return XDialogOptimized().apply {
                this.config = this@Builder.config.copy()
            }
        }
    }

    /**
     * ViewHolder - 简化视图操作
     */
    class ViewHolder(private val rootView: View, private val dialog: XDialogOptimized) {
        
        /**
         * 查找视图（带缓存）
         */
        fun <T : View> findViewById(id: Int): T? {
            // 优先从缓存获取
            dialog.getCachedView<T>(id)?.let { return it }
            
            // 缓存未命中时查找并缓存
            return rootView.findViewById<T>(id)?.also {
                dialog.viewCache.put(id, it)
            }
        }

        /**
         * 设置文本
         */
        fun setText(id: Int, text: CharSequence?): ViewHolder {
            findViewById<android.widget.TextView>(id)?.text = text
            return this
        }

        /**
         * 设置文本（资源ID）
         */
        fun setText(id: Int, resId: Int): ViewHolder {
            findViewById<android.widget.TextView>(id)?.setText(resId)
            return this
        }

        /**
         * 设置文本颜色
         */
        fun setTextColor(id: Int, color: Int): ViewHolder {
            findViewById<android.widget.TextView>(id)?.setTextColor(color)
            return this
        }

        /**
         * 设置图片资源
         */
        fun setImageResource(id: Int, resId: Int): ViewHolder {
            findViewById<android.widget.ImageView>(id)?.setImageResource(resId)
            return this
        }

        /**
         * 设置图片Drawable
         */
        fun setImageDrawable(id: Int, drawable: android.graphics.drawable.Drawable?): ViewHolder {
            findViewById<android.widget.ImageView>(id)?.setImageDrawable(drawable)
            return this
        }

        /**
         * 设置图片Bitmap
         */
        fun setImageBitmap(id: Int, bitmap: android.graphics.Bitmap?): ViewHolder {
            findViewById<android.widget.ImageView>(id)?.setImageBitmap(bitmap)
            return this
        }

        /**
         * 设置背景颜色
         */
        fun setBackgroundColor(id: Int, color: Int): ViewHolder {
            findViewById<View>(id)?.setBackgroundColor(color)
            return this
        }

        /**
         * 设置背景资源
         */
        fun setBackgroundResource(id: Int, resId: Int): ViewHolder {
            findViewById<View>(id)?.setBackgroundResource(resId)
            return this
        }

        /**
         * 设置可见性
         */
        fun setVisibility(id: Int, visibility: Int): ViewHolder {
            findViewById<View>(id)?.visibility = visibility
            return this
        }

        /**
         * 设置是否可见（VISIBLE/GONE）
         */
        fun setVisible(id: Int, visible: Boolean): ViewHolder {
            findViewById<View>(id)?.visibility = if (visible) View.VISIBLE else View.GONE
            return this
        }

        /**
         * 获取文本内容（支持TextView/EditText/Button等）
         */
        fun getText(id: Int): String {
            return findViewById<android.widget.TextView>(id)?.text?.toString() ?: ""
        }

        /**
         * 设置提示文本（支持TextView/EditText）
         */
        fun setHint(id: Int, hint: CharSequence?): ViewHolder {
            findViewById<android.widget.TextView>(id)?.hint = hint
            return this
        }

        /**
         * 设置提示文本（资源ID）
         */
        fun setHint(id: Int, resId: Int): ViewHolder {
            findViewById<android.widget.TextView>(id)?.setHint(resId)
            return this
        }

        /**
         * 设置提示文本颜色
         */
        fun setHintTextColor(id: Int, color: Int): ViewHolder {
            findViewById<android.widget.TextView>(id)?.setHintTextColor(color)
            return this
        }
        
        /**
         * 设置输入类型
         * @param type 例如: InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
         */
        fun setInputType(id: Int, type: Int): ViewHolder {
             findViewById<android.widget.TextView>(id)?.inputType = type
             return this
        }

        /**
         * 清空文本
         */
        fun clearText(id: Int): ViewHolder {
            findViewById<android.widget.TextView>(id)?.text = ""
            return this
        }

        /**
         * 设置点击事件
         */
        fun setOnClickListener(id: Int, listener: View.OnClickListener): ViewHolder {
            findViewById<View>(id)?.setOnClickListener(listener)
            return this
        }

        /**
         * 关闭弹窗
         */
        fun dismiss() {
            dialog.dismiss()
        }
    }
}
