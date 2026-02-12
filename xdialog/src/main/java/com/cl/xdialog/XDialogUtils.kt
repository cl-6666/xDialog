package com.cl.xdialog

import android.content.Context
import android.view.Gravity
import androidx.fragment.app.FragmentManager
import com.cl.xdialog.base.OptimizedViewHolder


/**
 * XDialog工具类
 * 提供常用的Dialog样式和快捷创建方法
 */
object XDialogUtils {

    /**
     * 创建确认对话框
     * 注意：需要用户提供布局资源
     */
    @JvmStatic
    fun confirm(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        title: String,
        message: String,
        titleViewId: Int,
        messageViewId: Int,
        confirmViewId: Int,
        cancelViewId: Int,
        onConfirm: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null
    ): XDialogOptimized {
        return XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .cancelableOutside(false)
            .onBind { holder ->
                holder.setText(titleViewId, title)
                holder.setText(messageViewId, message)
            }
            .onClick(confirmViewId, cancelViewId) { _, view, dialog ->
                when (view.id) {
                    confirmViewId -> {
                        onConfirm?.invoke()
                        dialog.dismiss()
                    }
                    cancelViewId -> {
                        onCancel?.invoke()
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 创建加载对话框
     * 注意：需要用户提供布局资源
     */
    @JvmStatic
    fun loading(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        messageViewId: Int,
        message: String = "加载中..."
    ): XDialogOptimized {
        return XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .cancelableOutside(false)
            .onBind { holder ->
                holder.setText(messageViewId, message)
            }
            .show()
    }

    /**
     * 创建提示对话框
     * 注意：需要用户提供布局资源
     */
    @JvmStatic
    fun alert(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        titleViewId: Int,
        messageViewId: Int,
        buttonViewId: Int,
        title: String,
        message: String,
        buttonText: String = "确定",
        onConfirm: (() -> Unit)? = null
    ): XDialogOptimized {
        return XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .onBind { holder ->
                holder.setText(titleViewId, title)
                holder.setText(messageViewId, message)
                holder.setText(buttonViewId, buttonText)
            }
            .onClick(buttonViewId) { _, _, dialog ->
                onConfirm?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    /**
     * 底部弹窗
     */
    @JvmStatic
    fun bottomSheet(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        animationRes: Int = 0
    ): XDialogOptimized.Builder {
        val builder = XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .gravity(Gravity.BOTTOM)
        
        if (animationRes != 0) {
            builder.animation(animationRes)
        }
        
        return builder
    }

    /**
     * 创建中心弹窗
     */
    @JvmStatic
    fun center(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        animationRes: Int = 0
    ): XDialogOptimized.Builder {
        val builder = XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .gravity(Gravity.CENTER)
            
        if (animationRes != 0) {
            builder.animation(animationRes)
        }
        
        return builder
    }

    /**
     * 创建全屏弹窗
     */
    @JvmStatic
    fun fullScreen(
        fragmentManager: FragmentManager,
        layoutRes: Int
    ): XDialogOptimized.Builder {
        return XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .size(-1, -1) // MATCH_PARENT
            .dimAmount(0f)
    }

    /**
     * 创建自定义尺寸弹窗
     */
    @JvmStatic
    fun custom(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        widthPercent: Float,
        heightPercent: Float,
        context: Context
    ): XDialogOptimized.Builder {
        return XDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .widthPercent(context, widthPercent)
            .heightPercent(context, heightPercent)
    }

    /**
     * 创建列表选择对话框
     * 注意：需要用户提供布局资源
     */
    @JvmStatic
    fun <T> list(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        titleViewId: Int,
        recyclerViewId: Int,
        title: String,
        items: List<T>,
        itemLayoutRes: Int,
        itemTextViewId: Int,
        onItemClick: (position: Int, item: T) -> Unit
    ): XListDialogOptimized {
        val adapter = object : com.cl.xdialog.adapter.XOptimizedAdapter<T>(
            itemLayoutRes,
            items
        ) {
            override fun onBind(holder: OptimizedViewHolder, position: Int, item: T) {
                holder.setText(itemTextViewId, item.toString())
            }
        }

        return XListDialogOptimized.create(fragmentManager)
            .layout(layoutRes)
            .recyclerViewId(recyclerViewId)
            .onBind { holder ->
                holder.setText(titleViewId, title)
            }
            .optimizedAdapter(adapter)
            .onOptimizedItemClick(object : com.cl.xdialog.listener.OnOptimizedAdapterItemClickListener<T> {
                override fun onItemClick(holder: OptimizedViewHolder?, position: Int, item: T, dialog: com.cl.xdialog.XDialogOptimized?) {
                    onItemClick(position, item)
                    dialog?.dismiss()
                }
            })
            .show()
    }

    /**
     * 创建进度对话框
     * 注意：需要用户提供布局资源
     */
    @JvmStatic
    fun progress(
        fragmentManager: FragmentManager,
        layoutRes: Int,
        titleViewId: Int,
        progressBarId: Int,
        progressTextId: Int,
        title: String = "请稍候",
        maxProgress: Int = 100
    ): ProgressDialog {
        return ProgressDialog(fragmentManager, layoutRes, titleViewId, progressBarId, progressTextId, title, maxProgress)
    }

    /**
     * 进度对话框包装类
     */
    class ProgressDialog(
        private val fragmentManager: FragmentManager,
        private val layoutRes: Int,
        private val titleViewId: Int,
        private val progressBarId: Int,
        private val progressTextId: Int,
        private val title: String,
        private val maxProgress: Int
    ) {
        private var dialog: XDialogOptimized? = null
        private var progressBar: android.widget.ProgressBar? = null
        private var progressText: android.widget.TextView? = null

        fun show(): ProgressDialog {
            dialog = XDialogOptimized.create(fragmentManager)
                .layout(layoutRes)
                .cancelableOutside(false)
                .onBind { holder ->
                    holder.setText(titleViewId, title)
                    progressBar = holder.findViewById(progressBarId)
                    progressText = holder.findViewById(progressTextId)
                    progressBar?.max = maxProgress
                }
                .show()
            return this
        }

        fun updateProgress(progress: Int) {
            progressBar?.progress = progress
            progressText?.text = "$progress%"
        }

        fun dismiss() {
            dialog?.dismiss()
        }
    }
}