package com.cl.xdialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
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
        
        return when {
            config.customView != null -> config.customView
            config.layoutRes > 0 -> inflater.inflate(config.layoutRes, container, false)
            else -> throw IllegalStateException("必须设置布局资源或自定义View")
        }
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

        fun height(height: Int) = apply {
            config.height = height
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
            dialog.show(fragmentManager, this.config.tag.takeIf { it.isNotEmpty() })
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
        
        fun <T : View> findViewById(id: Int): T? {
            // 优先从缓存获取
            dialog.getCachedView<T>(id)?.let { return it }
            
            // 缓存未命中时查找并缓存
            return rootView.findViewById<T>(id)?.also {
                dialog.viewCache.put(id, it)
            }
        }

        fun setText(id: Int, text: CharSequence) {
            findViewById<android.widget.TextView>(id)?.text = text
        }

        fun setVisibility(id: Int, visibility: Int) {
            findViewById<View>(id)?.visibility = visibility
        }

        fun setOnClickListener(id: Int, listener: View.OnClickListener) {
            findViewById<View>(id)?.setOnClickListener(listener)
        }

        fun dismiss() {
            dialog.dismiss()
        }
    }
}