package com.cl.xdialog.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.cl.xdialog.XDialogOptimized
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * Dialog内存管理器
 * 负责管理Dialog的生命周期，防止内存泄漏
 */
object DialogMemoryManager : Application.ActivityLifecycleCallbacks {

    private val dialogMap = ConcurrentHashMap<String, WeakReference<XDialogOptimized>>()
    private val lifecycleObservers = ConcurrentHashMap<String, LifecycleEventObserver>()
    
    /**
     * 注册Dialog
     */
    fun registerDialog(tag: String, dialog: XDialogOptimized, lifecycleOwner: LifecycleOwner? = null) {
        // 清理已经被回收的Dialog
        cleanupRecycledDialogs()
        
        // 注册新的Dialog
        dialogMap[tag] = WeakReference(dialog)
        
        // 如果有LifecycleOwner，监听其生命周期
        lifecycleOwner?.let { owner ->
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        dismissDialog(tag)
                        unregisterDialog(tag)
                    }
                    Lifecycle.Event.ON_PAUSE -> {
                        // 在Activity暂停时可以选择隐藏Dialog
                        getDialog(tag)?.let { d ->
                            if (d.isVisible && Config.dismissOnActivityPause) {
                                d.dismiss()
                            }
                        }
                    }
                    else -> {}
                }
            }
            
            owner.lifecycle.addObserver(observer)
            lifecycleObservers[tag] = observer
        }
    }

    /**
     * 注销Dialog
     */
    fun unregisterDialog(tag: String) {
        dialogMap.remove(tag)
        lifecycleObservers.remove(tag)
    }

    /**
     * 获取Dialog
     */
    fun getDialog(tag: String): XDialogOptimized? {
        return dialogMap[tag]?.get()
    }

    /**
     * 关闭指定Dialog
     */
    fun dismissDialog(tag: String) {
        getDialog(tag)?.dismiss()
        unregisterDialog(tag)
    }

    /**
     * 关闭所有Dialog
     */
    fun dismissAllDialogs() {
        dialogMap.keys.forEach { tag ->
            dismissDialog(tag)
        }
    }

    /**
     * 清理已被回收的Dialog
     */
    private fun cleanupRecycledDialogs() {
        val iterator = dialogMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.get() == null) {
                iterator.remove()
                lifecycleObservers.remove(entry.key)
            }
        }
    }

    /**
     * 获取当前活跃的Dialog数量
     */
    fun getActiveDialogCount(): Int {
        cleanupRecycledDialogs()
        return dialogMap.size
    }

    /**
     * 检查是否有活跃的Dialog
     */
    fun hasActiveDialogs(): Boolean {
        return getActiveDialogCount() > 0
    }

    /**
     * 获取所有活跃的Dialog标签
     */
    fun getActiveDialogTags(): Set<String> {
        cleanupRecycledDialogs()
        return dialogMap.keys.toSet()
    }

    // Activity生命周期回调
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {
        // Activity暂停时，可以选择隐藏某些Dialog
        if (activity is FragmentActivity) {
            val fragmentManager = activity.supportFragmentManager
            dismissDialogsForFragmentManager(fragmentManager)
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        // Activity销毁时，清理相关的Dialog
        if (activity is FragmentActivity) {
            val fragmentManager = activity.supportFragmentManager
            dismissDialogsForFragmentManager(fragmentManager)
        }
    }

    /**
     * 为指定的FragmentManager关闭Dialog
     */
    private fun dismissDialogsForFragmentManager(fragmentManager: FragmentManager) {
        val iterator = dialogMap.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val dialog = entry.value.get()
            if (dialog?.fragmentManager == fragmentManager) {
                dialog.dismiss()
                iterator.remove()
                lifecycleObservers.remove(entry.key)
            }
        }
    }

    /**
     * 内存优化配置
     */
    object Config {
        /**
         * 最大同时显示的Dialog数量
         */
        var maxConcurrentDialogs = 5

        /**
         * 自动清理间隔（毫秒）
         */
        var autoCleanupInterval = 30000L // 30秒

        /**
         * 是否在Activity暂停时自动关闭Dialog
         */
        var dismissOnActivityPause = false
    }

    /**
     * 启动自动清理任务
     */
    fun startAutoCleanup() {
        // 可以使用Handler或者其他机制定期清理
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            cleanupRecycledDialogs()
            startAutoCleanup() // 递归调用
        }, Config.autoCleanupInterval)
    }
}