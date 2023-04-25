package com.cl.xdialog.base

import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.FragmentManager
import com.cl.xdialog.listener.OnAdapterItemClickListener
import com.cl.xdialog.listener.OnBindViewListener
import com.cl.xdialog.listener.OnViewClickListener
import java.io.Serializable

/**
 * 项目：xDialog
 * 版权：蒲公英公司 版权所有
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-08-17
 * 描述：
 * 数据保存封装的容器类
 * 修订历史：
 */
class XController<A : XBaseAdapter<*>?> : Parcelable, Serializable {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //get
    var fragmentManager: FragmentManager? = null
    var layoutRes = 0
    var height = 0
    var width = 0
    var dimAmount = 0f
    var gravity = 0
    var tag: String? = null
    var ids: IntArray? = null
    var isCancelableOutside = false
    var onViewClickListener: OnViewClickListener? = null
    var onBindViewListener: OnBindViewListener? = null


    //列表
    var adapter: A? = null
        private set
    var adapterItemClickListener: OnAdapterItemClickListener<*>? = null
    var orientation = 0
    var dialogAnimationRes = 0
    var dialogView: View? = null
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var onKeyListener: DialogInterface.OnKeyListener? = null

    //////////////////////////////////////////Parcelable持久化//////////////////////////////////////////////////////
    constructor() {}
    protected constructor(`in`: Parcel) {
        layoutRes = `in`.readInt()
        height = `in`.readInt()
        width = `in`.readInt()
        dimAmount = `in`.readFloat()
        gravity = `in`.readInt()
        tag = `in`.readString()
        ids = `in`.createIntArray()
        isCancelableOutside = `in`.readByte().toInt() != 0
        orientation = `in`.readInt()
    }

    //内容描述接口,不用管
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(layoutRes)
        dest.writeInt(height)
        dest.writeInt(width)
        dest.writeFloat(dimAmount)
        dest.writeInt(gravity)
        dest.writeString(tag)
        dest.writeIntArray(ids)
        dest.writeByte((if (isCancelableOutside) 1 else 0).toByte())
        dest.writeInt(orientation)
    }

    fun setAdapter(adapter: A) {
        this.adapter = adapter
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<XController<*>> =
            object : Parcelable.Creator<XController<*>> {
                override fun createFromParcel(`in`: Parcel): XController<*> {
                    return XController<XBaseAdapter<*>>(`in`)
                }

                override fun newArray(size: Int): Array<XController<*>?> {
                    return arrayOfNulls(size)
                }
            }
    }
}