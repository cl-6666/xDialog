package com.cl.xdialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

/**
 * 加载对话框
 * 特性：
 * 1. 多种加载样式：旋转圆圈、进度条、点动画
 * 2. 可配置文本、颜色、大小等
 * 3. 支持进度更新和自动关闭
 * 4. 内存安全设计
 */
class XLoadingDialog : XDialogOptimized() {

    private var loadingConfig: LoadingConfig = LoadingConfig()
    private var loadingContainer: FrameLayout? = null
    private var loadingSpinner: ProgressBar? = null
    private var loadingDots: View? = null
    private var loadingProgress: ProgressBar? = null
    private var loadingText: TextView? = null
    private var dotsAnimator: AnimatorSet? = null
    private var autoCloseHandler: Handler? = null
    private var autoCloseRunnable: Runnable? = null

    companion object {
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
        loadingSpinner = view.findViewById(R.id.xdialog_loading_spinner)
        loadingDots = view.findViewById(R.id.xdialog_loading_dots)
        loadingProgress = view.findViewById(R.id.xdialog_loading_progress)
        loadingText = view.findViewById(R.id.xdialog_loading_text)
    }

    /**
     * 设置加载样式
     */
    private fun setupLoadingStyle() {
        // 隐藏所有加载视图
        loadingSpinner?.visibility = View.GONE
        loadingDots?.visibility = View.GONE
        loadingProgress?.visibility = View.GONE

        // 根据配置显示对应的加载样式
        when (loadingConfig.style) {
            LoadingStyle.SPINNER -> {
                loadingSpinner?.visibility = View.VISIBLE
                setupSpinnerStyle()
            }
            LoadingStyle.DOTS -> {
                loadingDots?.visibility = View.VISIBLE
                setupDotsAnimation()
            }
            LoadingStyle.PROGRESS -> {
                loadingProgress?.visibility = View.VISIBLE
                setupProgressStyle()
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
     * 设置旋转圆圈样式
     */
    private fun setupSpinnerStyle() {
        loadingSpinner?.apply {
            layoutParams = layoutParams.apply {
                width = loadingConfig.size
                height = loadingConfig.size
            }
        }
    }

    /**
     * 设置点动画
     */
    private fun setupDotsAnimation() {
        val dot1 = loadingDots?.findViewById<View>(R.id.xdialog_dot1)
        val dot2 = loadingDots?.findViewById<View>(R.id.xdialog_dot2)
        val dot3 = loadingDots?.findViewById<View>(R.id.xdialog_dot3)

        // 设置点的颜色
        dot1?.setBackgroundColor(loadingConfig.primaryColor)
        dot2?.setBackgroundColor(loadingConfig.primaryColor)
        dot3?.setBackgroundColor(loadingConfig.primaryColor)

        // 创建动画
        dotsAnimator = AnimatorSet().apply {
            val anim1 = createDotAnimation(dot1, 0)
            val anim2 = createDotAnimation(dot2, 200)
            val anim3 = createDotAnimation(dot3, 400)
            
            playTogether(anim1, anim2, anim3)
            duration = 1200
            // AnimatorSet doesn't have repeatCount, we set it on individual animators
            start()
        }
    }

    /**
     * 创建单个点的动画
     */
    private fun createDotAnimation(dot: View?, delay: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(dot, "scaleY", 1f, 1.5f, 1f).apply {
            startDelay = delay
            repeatCount = ObjectAnimator.INFINITE
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
        dotsAnimator?.cancel()
        dotsAnimator = null
        autoCloseRunnable?.let { autoCloseHandler?.removeCallbacks(it) }
        autoCloseHandler = null
        autoCloseRunnable = null
    }

    /**
     * 加载样式枚举
     */
    enum class LoadingStyle {
        SPINNER,    // 旋转圆圈
        DOTS,       // 点动画
        PROGRESS    // 进度条
    }

    /**
     * 加载配置类
     */
    data class LoadingConfig(
        var style: LoadingStyle = LoadingStyle.SPINNER,
        var message: String = "加载中...",
        var size: Int = 144, // 48dp in pixels
        var progressWidth: Int = 240, // 80dp in pixels
        var progress: Int = 0,
        var maxProgress: Int = 100,
        var primaryColor: Int = 0xFF007AFF.toInt(),
        var textColor: Int = 0xFF333333.toInt(),
        var textSize: Float = 14f,
        var autoCloseDelay: Long = 0 // 0表示不自动关闭
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

        fun textSize(size: Float) = apply {
            loadingConfig.textSize = size
        }

        fun autoClose(delay: Long) = apply {
            loadingConfig.autoCloseDelay = delay
        }

        fun onBind(listener: (XDialogOptimized.ViewHolder) -> Unit) = apply {
            dialogBuilder.onBind(listener)
        }

        fun show(): XLoadingDialog {
            val dialog = build()
            dialog.show(fragmentManager, loadingConfig.style.name + "_" + System.currentTimeMillis())
            return dialog
        }

        fun build(): XLoadingDialog {
            return XLoadingDialog().apply {
                // 复制基础配置
                this.config = dialogBuilder.config.copy()
                this.loadingConfig = this@LoadingBuilder.loadingConfig.copy()
            }
        }
    }
}