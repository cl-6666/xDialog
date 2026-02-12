package com.cl.dialog.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cl.dialog.R
import com.cl.xdialog.XDatePickerDialog
import com.cl.xdialog.XDialogOptimized
import com.cl.xdialog.XLoadingDialog
import java.util.ArrayList
import java.util.Calendar

/**
 * 新架构弹窗组件演示页面
 * 展示各种常用的弹窗组件，包括基础弹窗、确认对话框、通知提示、加载状态和自定义内容弹窗
 */
class DialogDemoActivity : AppCompatActivity() {

    private var loadingDialog: XDialogOptimized? = null
    private var mainHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_demo)

        mainHandler = Handler(Looper.getMainLooper())

        // 设置标题
        supportActionBar?.apply {
            title = "弹窗组件演示"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog != null && loadingDialog!!.isVisible) {
            loadingDialog!!.dismiss()
        }
        mainHandler?.removeCallbacksAndMessages(null)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * 基础弹窗（Modal）演示
     */
    fun showBasicModal(view: View?) {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_simple)
            .widthPercent(this, 0.7f)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "基础弹窗")
                viewHolder.setText(
                    R.id.tv_content,
                    "这是一个基础的模态弹窗示例，采用新架构实现。\n\n特点：\n• 居中显示\n• 支持点击外部关闭\n• 统一的视觉风格"
                )

                // 示例：设置图片（如果有）
                viewHolder.setImageResource(R.id.iv_icon, R.mipmap.ic_launcher)


//                val ivIcon = viewHolder.findViewById<ImageView>(R.id.iv_icon)
//                ivIcon?.visibility = View.VISIBLE
//                ivIcon?.setImageResource(R.mipmap.ic_launcher)
            }
            .onClick(
                R.id.btn_confirm,
                R.id.btn_cancel
            ) { _, view1, tDialog ->
                when (view1.id) {
                    R.id.btn_confirm -> {
                        showToast("确认按钮被点击")
                        tDialog.dismiss()
                    }
                    R.id.btn_cancel -> {
                        tDialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 确认对话框演示
     */
    fun showConfirmDialog(view: View?) {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_simple)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "确认操作")
                viewHolder.setText(R.id.tv_content, "您确定要执行此操作吗？此操作不可撤销。")
            }
            .onClick(
                R.id.btn_confirm,
                R.id.btn_cancel
            ) { _, view1, dialog ->
                when (view1.id) {
                    R.id.btn_confirm -> {
                        showToast("操作已确认")
                        dialog.dismiss()
                    }
                    R.id.btn_cancel -> {
                        showToast("操作已取消")
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 通知提示演示
     */
    fun showNotificationDemo(view: View?) {
        // 显示系统Toast
        showToast("这是一个系统Toast通知")

        // 延迟显示自定义通知弹窗
        mainHandler?.postDelayed({
            showCustomNotification()
        }, 1500)
    }

    /**
     * 自定义通知弹窗
     */
    private fun showCustomNotification() {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_simple)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "📢 通知消息")
                viewHolder.setText(
                    R.id.tv_content,
                    "这是一个自定义的通知弹窗，支持：\n\n• 自定义位置显示\n• 淡入淡出动画\n• 自动消失功能"
                )

                // 隐藏取消按钮，只显示确认按钮
                viewHolder.findViewById<View>(R.id.btn_cancel)?.visibility = View.GONE
                viewHolder.setText(R.id.btn_confirm, "知道了")
            }
            .onClick(R.id.btn_confirm) { _, _, tDialog ->
                tDialog.dismiss()
            }
            .show()
    }

    /**
     * 加载中状态弹窗演示 - 展示多种加载样式
     */
    fun showLoadingDialog(view: View?) {
        // 演示1: 旋转图标样式1 (内置样式1)
        showPulseLoading()

        // 延迟演示其他样式
        mainHandler?.postDelayed({
            // 演示2: 旋转图标样式2 (内置样式2)
            showFlipLoading()
        }, 3500)

        mainHandler?.postDelayed({
            // 演示4: 进度条样式 (内置样式5)
            showProgressLoading()
        }, 7000)

        mainHandler?.postDelayed({
            // 演示5: 可配置演示 (黑色主题)
            showDarkLoading()
        }, 10500)
    }

    /**
     * 演示加载样式1 (内置样式1)
     */
    private fun showPulseLoading() {
        val pulseDialog = XLoadingDialog.create(supportFragmentManager)
            .style(XLoadingDialog.LoadingStyle.STYLE1) // 使用内置STYLE1样式
            .message("正在处理 (样式1)...")
            .show()

        // 3秒后自动关闭
        mainHandler?.postDelayed({
            if (pulseDialog.isVisible) {
                pulseDialog.dismiss()
                showToast("样式1加载完成")
            }
        }, 3000)
    }

    /**
     * 演示加载样式2 (内置样式2)
     */
    private fun showFlipLoading() {
        val flipDialog = XLoadingDialog.create(supportFragmentManager)
            .style(XLoadingDialog.LoadingStyle.STYLE2) // 使用内置STYLE2样式
            .message("正在同步 (样式2)...")
            .show()

        // 3秒后自动关闭
        mainHandler?.postDelayed({
            if (flipDialog.isVisible) {
                flipDialog.dismiss()
                showToast("样式2加载完成")
            }
        }, 3000)
    }

    /**
     * 演示进度条加载样式 (内置样式5)
     */
    private fun showProgressLoading() {
        val progressDialog = XLoadingDialog.create(supportFragmentManager)
            .style(XLoadingDialog.LoadingStyle.PROGRESS)
            .message("下载中 (样式5)...")
            .progress(0)
            .maxProgress(100)
            .progressWidth(300)
            .primaryColor(-0xcb38a7) // 0xFF34C759
            .show()

        // 模拟进度更新
        simulateProgress(progressDialog)
    }

    /**
     * 模拟进度更新
     */
    private fun simulateProgress(progressDialog: XLoadingDialog) {
        var currentProgress = 0
        val updateProgress = object : Runnable {
            override fun run() {
                if (progressDialog.isVisible && currentProgress <= 100) {
                    progressDialog.updateProgress(currentProgress)
                    progressDialog.updateMessage("下载中... $currentProgress%")
                    currentProgress += 10

                    if (currentProgress <= 100) {
                        mainHandler?.postDelayed(this, 300)
                    } else {
                        // 下载完成
                        mainHandler?.postDelayed({
                            if (progressDialog.isVisible) {
                                progressDialog.dismiss()
                                showToast("进度条加载完成")
                            }
                        }, 500)
                    }
                }
            }
        }

        mainHandler?.postDelayed(updateProgress, 300)
    }


    /**
     * 演示黑色背景加载样式
     */
    private fun showDarkLoading() {
        val darkDialog = XLoadingDialog.create(supportFragmentManager)
            .icon(R.mipmap.loading_test1)
            .rotate(true)
            .message("黑色主题加载...")
            .backgroundColor(-0x34000000) // 半透明黑色背景 0xCC000000
            .textColor(-0x1) // 白色文字 0xFFFFFFFF
            .cancelableOutside(false)
            .show()

        // 3秒后自动关闭
        mainHandler?.postDelayed({
            if (darkDialog.isVisible) {
                darkDialog.dismiss()
                showToast("黑色主题加载完成")
            }
        }, 3000)
    }

    /**
     * 自定义内容弹窗演示
     */
    fun showCustomContentDialog(view: View?) {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_custom)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "自定义输入")
                viewHolder.setText(R.id.tv_content, "请输入您的反馈内容：")
                viewHolder.setHint(R.id.et_input, "请输入内容...")
                // 获取输入框并设置焦点
//                val editText = viewHolder.findViewById<EditText>(R.id.et_input)
//                editText?.hint = "请输入内容..."
//                editText?.requestFocus()
            }
            .onClick(
                R.id.btn_submit,
                R.id.btn_cancel
            ) { viewHolder, clickView, dialog ->
                when (clickView.id) {
                    R.id.btn_submit -> {
                        val editText = viewHolder.findViewById<EditText>(R.id.et_input)
                        val input = editText?.text.toString().trim()
                        if (input.isNotEmpty()) {
                            showToast("您输入的内容：$input")
                            dialog.dismiss()
                        } else {
                            showToast("请输入内容")
                        }
                    }
                    R.id.btn_cancel -> {
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 显示有数据的列表弹窗
     */
    fun showListWithData(view: View?) {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_list_demo)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "列表演示 - 有数据")
                val recyclerView = viewHolder.findViewById<RecyclerView>(R.id.recycler_view)
                val emptyLayout = viewHolder.findViewById<LinearLayout>(R.id.layout_empty)

                // 显示列表，隐藏空白页面
                recyclerView?.visibility = View.VISIBLE
                emptyLayout?.visibility = View.GONE

                // 设置列表数据
                val items = createSampleData()
                val adapter = ListAdapter(items)
                recyclerView?.layoutManager = LinearLayoutManager(this)
                recyclerView?.adapter = adapter
            }
            .onClick(
                R.id.btn_confirm,
                R.id.btn_cancel
            ) { _, clickView, dialog ->
                when (clickView.id) {
                    R.id.btn_confirm -> {
                        showToast("确定操作")
                        dialog.dismiss()
                    }
                    R.id.btn_cancel -> {
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 显示空白页面的列表弹窗
     */
    fun showListEmpty(view: View?) {
        XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_list_demo)
            .onBind { viewHolder ->
                viewHolder.setText(R.id.tv_title, "列表演示 - 空白页面")
                val recyclerView = viewHolder.findViewById<RecyclerView>(R.id.recycler_view)
                val emptyLayout = viewHolder.findViewById<LinearLayout>(R.id.layout_empty)

                // 隐藏列表，显示空白页面
                recyclerView?.visibility = View.GONE
                emptyLayout?.visibility = View.VISIBLE

                // 配置空白页面
                configureEmptyView(viewHolder)
            }
            .onClick(
                R.id.btn_confirm,
                R.id.btn_cancel
            ) { _, clickView, dialog ->
                when (clickView.id) {
                    R.id.btn_confirm -> {
                        showToast("确定操作")
                        dialog.dismiss()
                    }
                    R.id.btn_cancel -> {
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    /**
     * 配置空白页面
     */
    private fun configureEmptyView(viewHolder: XDialogOptimized.ViewHolder) {
        // 配置空白图标
        val emptyIcon = viewHolder.findViewById<ImageView>(R.id.iv_empty_icon)
        emptyIcon?.setImageResource(android.R.drawable.ic_menu_gallery)

        // 配置空白标题
        val emptyTitle = viewHolder.findViewById<TextView>(R.id.tv_empty_title)
        emptyTitle?.text = "暂无数据"

        // 配置空白描述
        val emptyDesc = viewHolder.findViewById<TextView>(R.id.tv_empty_desc)
        emptyDesc?.text = "当前没有可显示的内容，您可以稍后再试"

        // 配置空白操作按钮
        val emptyAction = viewHolder.findViewById<View>(R.id.btn_empty_action)
        emptyAction?.visibility = View.VISIBLE
        emptyAction?.setOnClickListener {
            showToast("刷新操作")
        }
    }

    /**
     * 创建示例数据
     */
    private fun createSampleData(): MutableList<ListItem> {
        val items = ArrayList<ListItem>()
        items.add(ListItem("项目 1", "这是第一个列表项的描述"))
        items.add(ListItem("项目 2", "这是第二个列表项的描述"))
        items.add(ListItem("项目 3", "这是第三个列表项的描述"))
        items.add(ListItem("项目 4", "这是第四个列表项的描述"))
        items.add(ListItem("项目 5", "这是第五个列表项的描述"))
        return items
    }

    /**
     * 显示Toast消息
     */
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 基础日期选择弹窗演示
     */
    fun showBasicDatePicker(view: View?) {
        XDatePickerDialog.create(supportFragmentManager)
            .title("选择日期")
            .onDateSelected { _, year, month, day ->
                val selectedDate = "${year}年${month}月${day}日"
                showToast("选择的日期：$selectedDate")
            }
            .show()
    }

    /**
     * 自定义日期选择弹窗演示
     */
    fun showCustomDatePicker(view: View?) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18) // 18年前
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, 10) // 10年后

        XDatePickerDialog.create(supportFragmentManager)
            .title("选择生日")
            .initialDate(1990, 1, 1) // 1990年1月1日
            .minDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            .maxDate(
                maxDate.get(Calendar.YEAR),
                maxDate.get(Calendar.MONTH) + 1,
                maxDate.get(Calendar.DAY_OF_MONTH)
            )
            .confirmText("确定")
            .cancelText("取消")
            .showTitle(true)
            .onDateSelected { _, year, month, day ->
                val selectedDate = "${year}年${month}月${day}日"
                showToast("选择的生日：$selectedDate")
            }
            .show()
    }

    /**
     * 列表项数据类
     */
    private data class ListItem(var title: String?, var subtitle: String?)

    /**
     * 列表适配器
     */
    private inner class ListAdapter(private val items: List<ListItem>) :
        RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_demo, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.title.text = item.title
            holder.subtitle.text = item.subtitle

            holder.itemView.setOnClickListener {
                Toast.makeText(it.context, "点击了: " + item.title, Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var title: TextView = itemView.findViewById(R.id.tv_title)
            var subtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
        }
    }

    companion object {
        private const val TAG = "DialogDemo"
    }
}
