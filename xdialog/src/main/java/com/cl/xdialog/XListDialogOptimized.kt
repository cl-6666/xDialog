package com.cl.xdialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cl.xdialog.adapter.XOptimizedAdapter
import com.cl.xdialog.listener.OnOptimizedAdapterItemClickListener

/**
 * 优化版本的列表Dialog
 * 继承自XDialogOptimized，专门处理列表显示
 */
class XListDialogOptimized : XDialogOptimized() {

    private var listConfig: ListConfig = ListConfig()
    private var recyclerView: RecyclerView? = null

    companion object {
        @JvmStatic
        fun create(fragmentManager: FragmentManager): ListBuilder {
            return ListBuilder(fragmentManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: android.os.Bundle?
    ): View? {
        // 如果未设置布局资源和自定义View，则自动创建一个包含RecyclerView的布局
        if (config.layoutRes <= 0 && config.customView == null) {
            val context = requireContext()
            val rv = RecyclerView(context)
            rv.layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            
            // 如果未设置ID，生成一个
            if (listConfig.recyclerViewId <= 0) {
                rv.id = View.generateViewId()
                listConfig.recyclerViewId = rv.id
            } else {
                rv.id = listConfig.recyclerViewId
            }
            return rv
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
    }

    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView(view: View) {
        if (listConfig.recyclerViewId <= 0) {
            throw IllegalArgumentException("必须设置RecyclerView的ID")
        }
        
        recyclerView = view.findViewById<RecyclerView>(listConfig.recyclerViewId)
            ?: throw IllegalArgumentException("列表布局必须包含指定ID的RecyclerView")

        recyclerView?.apply {
            // 设置布局管理器
            layoutManager = listConfig.layoutManager ?: LinearLayoutManager(context)
            
            // 设置优化版适配器
            if (listConfig.optimizedAdapter != null) {
                val adapter = listConfig.optimizedAdapter!!
                adapter.setTDialog(this@XListDialogOptimized)
                this.adapter = adapter
                
                // 设置点击监听
                listConfig.optimizedItemClickListener?.let { listener ->
                    @Suppress("UNCHECKED_CAST")
                    (adapter as XOptimizedAdapter<Any>).setOnAdapterItemClickListener(listener as OnOptimizedAdapterItemClickListener<Any>)
                }
            } else {
                throw IllegalArgumentException("必须设置优化版适配器")
            }
            
            // 性能优化
            setHasFixedSize(true)
            setItemViewCacheSize(20) // 增加缓存大小
        }
    }

    /**
     * 列表配置类
     */
    data class ListConfig(
        var optimizedAdapter: XOptimizedAdapter<*>? = null,
        var layoutManager: RecyclerView.LayoutManager? = null,
        var optimizedItemClickListener: OnOptimizedAdapterItemClickListener<*>? = null,
        var recyclerViewId: Int = 0
    )

    /**
     * 列表Dialog构建器
     */
    class ListBuilder(private val fragmentManager: FragmentManager) {
        private val dialogBuilder = XDialogOptimized.Builder(fragmentManager)
        private val listConfig = ListConfig()

        // 继承基础Dialog的所有方法
        fun layout(layoutRes: Int) = apply { dialogBuilder.layout(layoutRes) }
        fun customView(view: View) = apply { dialogBuilder.customView(view) }
        fun size(width: Int, height: Int) = apply { dialogBuilder.size(width, height) }
        fun width(width: Int) = apply { dialogBuilder.width(width) }
        fun height(height: Int) = apply { dialogBuilder.height(height) }
        fun widthPercent(context: android.content.Context, percent: Float) = apply { 
            dialogBuilder.widthPercent(context, percent) 
        }
        fun heightPercent(context: android.content.Context, percent: Float) = apply { 
            dialogBuilder.heightPercent(context, percent) 
        }
        fun gravity(gravity: Int) = apply { dialogBuilder.gravity(gravity) }
        fun dimAmount(dimAmount: Float) = apply { dialogBuilder.dimAmount(dimAmount) }
        fun cancelableOutside(cancelable: Boolean) = apply { dialogBuilder.cancelableOutside(cancelable) }
        fun animation(animationRes: Int) = apply { dialogBuilder.animation(animationRes) }
        fun tag(tag: String) = apply { dialogBuilder.tag(tag) }
        fun onDismiss(listener: android.content.DialogInterface.OnDismissListener) = apply { 
            dialogBuilder.onDismiss(listener) 
        }
        fun onKey(listener: android.content.DialogInterface.OnKeyListener) = apply { 
            dialogBuilder.onKey(listener) 
        }

        // 列表特有的方法
        fun optimizedAdapter(adapter: XOptimizedAdapter<*>) = apply {
            listConfig.optimizedAdapter = adapter
        }

        fun recyclerViewId(id: Int) = apply {
            listConfig.recyclerViewId = id
        }

        fun layoutManager(layoutManager: RecyclerView.LayoutManager) = apply {
            listConfig.layoutManager = layoutManager
        }

        fun <T> onOptimizedItemClick(listener: OnOptimizedAdapterItemClickListener<T>) = apply {
            @Suppress("UNCHECKED_CAST")
            listConfig.optimizedItemClickListener = listener as OnOptimizedAdapterItemClickListener<*>
        }

        fun onBind(listener: (XDialogOptimized.ViewHolder) -> Unit) = apply {
            dialogBuilder.onBind(listener)
        }

        /**
         * 创建并显示列表Dialog
         */
        fun show(): XListDialogOptimized {
            val dialog = build()
            XDialogOptimized.showAuto(
                fragmentManager,
                dialog,
                dialogBuilder.config.tag.takeIf { it.isNotEmpty() }
            )
            return dialog
        }

        /**
         * 仅创建列表Dialog实例
         */
        fun build(): XListDialogOptimized {
            return XListDialogOptimized().apply {
                // 复制基础配置
                this.config = dialogBuilder.config.copy()
                this.listConfig = this@ListBuilder.listConfig.copy()
            }
        }
    }

    /**
     * 获取RecyclerView实例
     */
    fun getRecyclerView(): RecyclerView? = recyclerView

    /**
     * 刷新列表数据
     */
    fun notifyDataSetChanged() {
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    /**
     * 滚动到指定位置
     */
    fun scrollToPosition(position: Int) {
        recyclerView?.scrollToPosition(position)
    }
}
