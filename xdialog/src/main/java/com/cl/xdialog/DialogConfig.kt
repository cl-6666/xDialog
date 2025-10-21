package com.cl.xdialog

import android.content.DialogInterface
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import androidx.annotation.LayoutRes

/**
 * Dialog配置数据类
 * 使用data class简化代码，支持Parcelable序列化
 */
data class DialogConfig(
    @LayoutRes var layoutRes: Int = 0,
    var customView: View? = null,
    var width: Int = 0,
    var height: Int = 0,
    var gravity: Int = Gravity.CENTER,
    var dimAmount: Float = 0.2f,
    var cancelableOutside: Boolean = true,
    var animationRes: Int = 0,
    var tag: String = "XDialogOptimized",
    var clickIds: MutableList<Int> = mutableListOf(),
    var bindListener: ((XDialogOptimized.ViewHolder) -> Unit)? = null,
    var clickListener: ((XDialogOptimized.ViewHolder, View, XDialogOptimized) -> Unit)? = null,
    var dismissListener: DialogInterface.OnDismissListener? = null,
    var keyListener: DialogInterface.OnKeyListener? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        layoutRes = parcel.readInt(),
        customView = null, // View不支持序列化，重新创建时需要重新设置
        width = parcel.readInt(),
        height = parcel.readInt(),
        gravity = parcel.readInt(),
        dimAmount = parcel.readFloat(),
        cancelableOutside = parcel.readByte() != 0.toByte(),
        animationRes = parcel.readInt(),
        tag = parcel.readString() ?: "XDialogOptimized",
        clickIds = mutableListOf<Int>().apply {
            parcel.readList(this, Int::class.java.classLoader)
        }
        // 回调函数不支持序列化，需要重新设置
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(layoutRes)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeInt(gravity)
        parcel.writeFloat(dimAmount)
        parcel.writeByte(if (cancelableOutside) 1 else 0)
        parcel.writeInt(animationRes)
        parcel.writeString(tag)
        parcel.writeList(clickIds)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DialogConfig> {
        override fun createFromParcel(parcel: Parcel): DialogConfig {
            return DialogConfig(parcel)
        }

        override fun newArray(size: Int): Array<DialogConfig?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 创建配置副本
     */
    fun copy(): DialogConfig {
        return DialogConfig(
            layoutRes = layoutRes,
            customView = customView,
            width = width,
            height = height,
            gravity = gravity,
            dimAmount = dimAmount,
            cancelableOutside = cancelableOutside,
            animationRes = animationRes,
            tag = tag,
            clickIds = clickIds.toMutableList(),
            bindListener = bindListener,
            clickListener = clickListener,
            dismissListener = dismissListener,
            keyListener = keyListener
        )
    }
}