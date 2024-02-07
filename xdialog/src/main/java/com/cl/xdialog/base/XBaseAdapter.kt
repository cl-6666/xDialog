package com.cl.xdialog.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.cl.xdialog.R
import com.cl.xdialog.XDialog
import com.cl.xdialog.listener.OnAdapterItemClickListener

/**
 * 项目：xDialog
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 * 修订历史：
 */
abstract class XBaseAdapter<T>  @JvmOverloads constructor(
    @param:LayoutRes private val layoutRes: Int,
    private val datas: List<T>,
    @param:LayoutRes private val emptyView: Int? = null
) : RecyclerView.Adapter<BindViewHolder>() {
    private var adapterItemClickListener: OnAdapterItemClickListener<T>? = null
    private var dialog: XDialog? = null


    protected abstract fun onBind(holder: BindViewHolder?, position: Int, t: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            // Return a ViewHolder for the provided empty layout
//            BindViewHolder(emptyView ?: LayoutInflater.from(parent.context).inflate(R.layout.xdialog_layout_empty,
//                parent, false))
//            emptyView
            BindViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    emptyView ?: R.layout.xdialog_layout_empty,
                    parent, false
                )
            )
        } else {
            // Return a ViewHolder for the regular item layout
            BindViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BindViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            onBind(holder, position, datas[position])
            holder.itemView.setOnClickListener {
                adapterItemClickListener?.onItemClick(holder, position, datas[position], dialog)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (datas.isEmpty()) 1 else datas.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (datas.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    val isEmpty: Boolean
        get() = datas.isEmpty()

    fun setTDialog(tDialog: XDialog?) {
        dialog = tDialog
    }

    fun setOnAdapterItemClickListener(listener: OnAdapterItemClickListener<T>?) {
        adapterItemClickListener = listener
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}