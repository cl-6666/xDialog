package com.cl.xdialog

import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager

/**
 * 加载对话框
 * 特性：
 * 1. 多种加载样式：图标(支持旋转/脉冲/翻转)、进度条、点动画、自定义View
 * 2. 可配置文本、颜色、大小等
 * 3. 支持进度更新和自动关闭
 * 4. 内存安全设计
 */
class XLoadingDialog : XDialogOptimized() {

    private var loadingConfig: LoadingConfig = LoadingConfig()
    private var loadingContainer: FrameLayout? = null
    private var loadingIcon: ImageView? = null
    private var loadingProgress: ProgressBar? = null
    private var loadingCustomContainer: FrameLayout? = null
    private var loadingText: TextView? = null
    private var customAnimator: ObjectAnimator? = null
    private var autoCloseHandler: Handler? = null
    private var autoCloseRunnable: Runnable? = null

    companion object {
        @JvmStatic
        fun show(manager: FragmentManager, message: String? = null): XLoadingDialog {
            val builder = create(manager)
            message?.let { builder.message(it) }
            return builder.show()
        }
        @JvmStatic
        fun create(fragmentManager: FragmentManager): LoadingBuilder {
            return LoadingBuilder(fragmentManager)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initLoadingViews(view)
        setupLoadingStyle()
        setupAutoClose()
    }

    /**
     * 初始化加载视图
     */
    private fun initLoadingViews(view: View) {
        loadingContainer = view.findViewById(R.id.xdialog_loading_container)
        loadingIcon = view.findViewById(R.id.xdialog_loading_icon)
        loadingProgress = view.findViewById(R.id.xdialog_loading_progress)
        loadingCustomContainer = view.findViewById(R.id.xdialog_loading_custom_container)
        loadingText = view.findViewById(R.id.xdialog_loading_text)
    }

    /**
     * 设置加载样式
     */
    private fun setupLoadingStyle() {
        // 隐藏所有加载视图
        loadingIcon?.visibility = View.GONE
        loadingProgress?.visibility = View.GONE
        loadingCustomContainer?.visibility = View.GONE

        // 根据配置显示对应的加载样式
        when (loadingConfig.style) {
            LoadingStyle.ICON -> {
                if (loadingConfig.iconRes != 0) {
                    loadingIcon?.visibility = View.VISIBLE
                    loadingIcon?.setImageResource(loadingConfig.iconRes)
                    setupIconStyle()
                }
            }
            LoadingStyle.STYLE1 -> {
                loadingIcon?.visibility = View.VISIBLE
                loadingIcon?.setImageResource(R.drawable.x_loading_1)
                // 默认旋转
                loadingConfig.animStyle = AnimStyle.ROTATE
                setupIconStyle()
            }
            LoadingStyle.STYLE2 -> {
                loadingIcon?.visibility = View.VISIBLE
                loadingIcon?.setImageResource(R.drawable.x_loading2)
                // 默认旋转
                loadingConfig.animStyle = AnimStyle.ROTATE
                setupIconStyle()
            }
            LoadingStyle.PROGRESS -> {
                loadingProgress?.visibility = View.VISIBLE
                setupProgressStyle()
            }
            LoadingStyle.CUSTOM_VIEW -> {
                loadingCustomContainer?.visibility = View.VISIBLE
                setupCustomView()
            }
        }

        // 设置文本
        loadingText?.apply {
            text = loadingConfig.message
            visibility = if (loadingConfig.message.isNotEmpty()) View.VISIBLE else View.GONE
            setTextColor(loadingConfig.textColor)
            textSize = loadingConfig.textSize
        }
    }

    /**
     * 设置图标样式
     */
    private fun setupIconStyle() {
        loadingIcon?.apply {
            layoutParams = layoutParams.apply {
                width = loadingConfig.size
                height = loadingConfig.size
            }
            
            // 根据动画类型设置动画
            when (loadingConfig.animStyle) {
                AnimStyle.ROTATE -> setupRotationAnimation(this)
                AnimStyle.NONE -> { /* 无动画 */ }
            }
        }
    }
    
    /**
     * 设置自定义View
     */
    private fun setupCustomView() {
        loadingConfig.customView?.let { view ->
            loadingCustomContainer?.removeAllViews()
            // 如果view已经有父容器，先移除
            (view.parent as? android.view.ViewGroup)?.removeView(view)
            loadingCustomContainer?.addView(view)
        }
    }

    /**
     * 设置进度条样式
     */
    private fun setupProgressStyle() {
        loadingProgress?.apply {
            layoutParams = layoutParams.apply {
                width = loadingConfig.progressWidth
            }
            progress = loadingConfig.progress
            max = loadingConfig.maxProgress
        }
    }

    /**
     * 设置旋转动画
     */
    private fun setupRotationAnimation(view: View?) {
        customAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }
    }

    /**
     * 设置自动关闭
     */
    private fun setupAutoClose() {
        if (loadingConfig.autoCloseDelay > 0) {
            autoCloseHandler = Handler(Looper.getMainLooper())
            autoCloseRunnable = Runnable {
                if (isAdded && !isDetached) {
                    dismiss()
                }
            }
            autoCloseHandler?.postDelayed(autoCloseRunnable!!, loadingConfig.autoCloseDelay)
        }
    }

    /**
     * 更新进度
     */
    fun updateProgress(progress: Int) {
        loadingProgress?.progress = progress
        loadingConfig.progress = progress
    }

    /**
     * 更新加载文本
     */
    fun updateMessage(message: String) {
        loadingText?.text = message
        loadingConfig.message = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 清理动画和Handler
        customAnimator?.cancel()
        customAnimator = null
        autoCloseRunnable?.let { autoCloseHandler?.removeCallbacks(it) }
        autoCloseHandler = null
        autoCloseRunnable = null
        loadingCustomContainer?.removeAllViews()
    }

    /**
     * 加载样式枚举
     */
    enum class LoadingStyle {
        ICON,       // 图标(支持多种动画)
        STYLE1,     // 内置：Logo 1 (旋转)
        STYLE2,     // 内置：Logo 2 (旋转)
        PROGRESS,   // 进度条
        CUSTOM_VIEW // 自定义View
    }

    /**
     * 动画类型枚举
     */
    enum class AnimStyle {
        ROTATE, // 旋转
        NONE    // 无动画
    }

    /**
     * 加载配置类
     */
    data class LoadingConfig(
        var style: LoadingStyle = LoadingStyle.ICON,
        var message: String = "加载中...",
        var size: Int = 144, // 48dp in pixels
        var progressWidth: Int = 240, // 80dp in pixels
        var progress: Int = 0,
        var maxProgress: Int = 100,
        var primaryColor: Int = 0xFF007AFF.toInt(),
        var textColor: Int = 0xFFFFFFFF.toInt(),
        var backgroundColor: Int = 0, // 0表示使用默认背景
        var textSize: Float = 14f,
        var autoCloseDelay: Long = 0, // 0表示不自动关闭
        @DrawableRes var iconRes: Int = 0,
        var animStyle: AnimStyle = AnimStyle.ROTATE, // 替换原来的 animateIcon
        var customView: View? = null,
        // 兼容旧字段
        var animateIcon: Boolean = true 
    )

    /**
     * 加载对话框构建器
     */
    class LoadingBuilder(private val fragmentManager: FragmentManager) {
        private val dialogBuilder = XDialogOptimized.Builder(fragmentManager)
        private val loadingConfig = LoadingConfig()

        init {
            // 设置默认布局
            dialogBuilder.layout(R.layout.xdialog_loading)
                .cancelableOutside(false) // 默认不可取消
                .gravity(android.view.Gravity.CENTER)
        }

        // 基础Dialog配置方法
        fun width(width: Int) = apply { dialogBuilder.width(width) }
        fun height(height: Int) = apply { dialogBuilder.height(height) }
        fun gravity(gravity: Int) = apply { dialogBuilder.gravity(gravity) }
        fun cancelableOutside(cancelable: Boolean) = apply { dialogBuilder.cancelableOutside(cancelable) }
        fun animation(animationRes: Int) = apply { dialogBuilder.animation(animationRes) }
        fun tag(tag: String) = apply { dialogBuilder.tag(tag) }

        // 加载特有配置方法
        fun style(style: LoadingStyle) = apply {
            loadingConfig.style = style
        }

        fun message(message: String) = apply {
            loadingConfig.message = message
        }

        fun size(size: Int) = apply {
            loadingConfig.size = size
        }

        fun progressWidth(width: Int) = apply {
            loadingConfig.progressWidth = width
        }

        fun progress(progress: Int) = apply {
            loadingConfig.progress = progress
        }

        fun maxProgress(max: Int) = apply {
            loadingConfig.maxProgress = max
        }

        fun primaryColor(color: Int) = apply {
            loadingConfig.primaryColor = color
        }

        fun textColor(color: Int) = apply {
            loadingConfig.textColor = color
        }

        fun backgroundColor(color: Int) = apply {
            loadingConfig.backgroundColor = color
        }

        fun textSize(size: Float) = apply {
            loadingConfig.textSize = size
        }

        fun autoClose(delay: Long) = apply {
            loadingConfig.autoCloseDelay = delay
        }
        
        fun icon(@DrawableRes iconRes: Int) = apply {
            loadingConfig.iconRes = iconRes
            loadingConfig.style = LoadingStyle.ICON
        }
        
        // 兼容旧API
        fun rotate(animate: Boolean) = apply {
            loadingConfig.animateIcon = animate
            loadingConfig.animStyle = if (animate) AnimStyle.ROTATE else AnimStyle.NONE
        }

        // 新增动画设置API
        fun animStyle(style: AnimStyle) = apply {
            loadingConfig.animStyle = style
        }
        
        fun customView(view: View) = apply {
            loadingConfig.customView = view
            loadingConfig.style = LoadingStyle.CUSTOM_VIEW
        }

        fun onBind(listener: (XDialogOptimized.ViewHolder) -> Unit) = apply {
            dialogBuilder.onBind(listener)
        }

        fun show(): XLoadingDialog {
            val dialog = build()
            XDialogOptimized.showAuto(
                fragmentManager,
                dialog,
                loadingConfig.style.name + "_" + System.currentTimeMillis()
            )
            return dialog
        }

        fun build(): XLoadingDialog {
            return XLoadingDialog().apply {
                // 复制基础配置
                this.config = dialogBuilder.config.copy()
                
                // 复制加载配置
                try {
                    val field = XLoadingDialog::class.java.getDeclaredField("loadingConfig")
                    field.isAccessible = true
                    field.set(this, this@LoadingBuilder.loadingConfig.copy())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
