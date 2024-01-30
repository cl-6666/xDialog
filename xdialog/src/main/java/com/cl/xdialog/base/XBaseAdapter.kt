package com.cl.xdialog.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
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
abstract class XBaseAdapter<T>(
    @param:LayoutRes private val layoutRes: Int,
    private val datas: List<T>
) : RecyclerView.Adapter<BindViewHolder>() {
    private var adapterItemClickListener: OnAdapterItemClickListener<T>? = null
    private var dialog: XDialog? = null
    protected abstract fun onBind(holder: BindViewHolder?, position: Int, t: T)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder {
        return BindViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
    }

    override fun onBindViewHolder(holder: BindViewHolder, position: Int) {
        onBind(holder, position, datas[position])
        holder.itemView.setOnClickListener {
            adapterItemClickListener?.onItemClick(holder, position, datas[position], dialog)
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun setTDialog(tDialog: XDialog?) {
        dialog = tDialog
    }

    fun setOnAdapterItemClickListener(listener: OnAdapterItemClickListener<T>?) {
        adapterItemClickListener = listener
    }
}