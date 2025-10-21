package com.cl.xdialog.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.cl.xdialog.DialogConfig
import com.cl.xdialog.manager.DialogMemoryManager
import com.cl.xdialog.performance.DialogPerformanceMonitor

/**
 * 优化的Dialog基类
 * 特性：
 * 1. 自动内存管理
 * 2. 性能监控
 * 3. 简化的生命周期管理
 */
abstract class BaseOptimizedDialogFragment : DialogFragment() {

    protected lateinit var config: DialogConfig
    private var performanceTag: String = ""
    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 恢复配置
        config = savedInstanceState?.getParcelable("dialog_config") ?: DialogConfig()
        performanceTag = config.tag ?: javaClass.simpleName
        
        // 开始性能监控
        DialogPerformanceMonitor.startCreate(performanceTag)
        
        // 注册到内存管理器
        DialogMemoryManager.registerDialog(performanceTag, this as com.cl.xdialog.XDialogOptimized, this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        
        // 设置Dialog属性
        dialog.window?.let { window ->
            // 设置背景透明
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            
            // 设置动画
            if (config.animationRes != 0) {
                window.setWindowAnimations(config.animationRes)
            }
            
            // 设置Dim
            window.setDimAmount(config.dimAmount)
        }
        
        // 设置可取消性
        dialog.setCancelable(true) // 默认可取消
        dialog.setCanceledOnTouchOutside(config.cancelableOutside)
        
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DialogPerformanceMonitor.startBind(performanceTag)
        
        try {
            rootView = if (config.customView != null) {
                config.customView
            } else {
                inflater.inflate(config.layoutRes, container, false)
            }
            return rootView
        } finally {
            DialogPerformanceMonitor.endBind(performanceTag)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 设置Dialog尺寸和位置
        setupDialogWindow()
        
        // 初始化视图
        initViews()
        
        // 绑定数据
        bindData()
        
        // 设置监听器
        setupListeners()
        
        DialogPerformanceMonitor.endCreate(performanceTag)
    }

    override fun onStart() {
        super.onStart()
        DialogPerformanceMonitor.startShow(performanceTag)
    }

    override fun onResume() {
        super.onResume()
        DialogPerformanceMonitor.endShow(performanceTag)
        DialogPerformanceMonitor.recordMemoryUsage(performanceTag)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("dialog_config", config)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
        DialogMemoryManager.unregisterDialog(performanceTag)
        DialogPerformanceMonitor.recordDismiss(performanceTag)
    }



    /**
     * 设置Dialog窗口属性
     */
    private fun setupDialogWindow() {
        dialog?.window?.let { window ->
            val layoutParams = window.attributes
            
            // 设置尺寸
            if (config.width > 0) {
                layoutParams.width = config.width
            }
            if (config.height > 0) {
                layoutParams.height = config.height
            }
            
            // 设置位置
            layoutParams.gravity = config.gravity
            
            window.attributes = layoutParams
        }
    }

    /**
     * 初始化视图（子类实现）
     */
    protected open fun initViews() {}

    /**
     * 绑定数据（子类实现）
     */
    protected open fun bindData() {}

    /**
     * 设置监听器（子类实现）
     */
    protected open fun setupListeners() {}

    /**
     * 设置配置
     */
    fun updateConfig(config: DialogConfig) {
        this.config = config
        this.performanceTag = config.tag ?: javaClass.simpleName
    }

    /**
     * 获取Context（安全）
     */
    protected fun safeContext(): Context? {
        return if (isAdded && !isDetached) context else null
    }

    /**
     * 安全地执行UI操作
     */
    protected fun safeRun(action: () -> Unit) {
        if (isAdded && !isDetached && view != null) {
            try {
                action()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 检查是否可以安全操作
     */
    protected fun isSafeToOperate(): Boolean {
        return isAdded && !isDetached && view != null && rootView != null
    }
}