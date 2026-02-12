# xDialog - Android 弹窗框架

[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Version](https://img.shields.io/badge/Version-3.1.6-orange.svg)](https://github.com/chenli/xDialog)

xDialog 是一个功能强大、高性能的 Android 弹窗框架，提供了丰富的弹窗组件和灵活的扩展机制。支持基础弹窗、加载框、日期选择器等多种常用对话框，具有链式调用、内存优化、高性能渲染等特性。

## ✨ 特性

- 🚀 **高性能渲染** - 优化的视图缓存和内存管理
- 🔗 **链式调用** - 流畅的 Builder 模式 API
- 🎨 **丰富组件** - 基础弹窗、加载框、日期选择器等
- 🔧 **灵活扩展** - 支持自定义弹窗和插件机制
- 📱 **响应式设计** - 支持百分比布局和多屏幕适配
- 🌍 **多语言支持** - 内置国际化支持
- 💾 **内存优化** - 自动资源清理和生命周期管理


## 📖 版本说明

- **当前版本**: 4.0.0 (推荐) - 全新架构，功能强大
- **历史版本**: [3.1.7版本文档](README_Dialog.md) - 稳定版本

## 📦 安装

### Gradle 依赖

在项目的 `build.gradle` 文件中添加：

```gradle
dependencies {
    implementation 'com.github.cl-6666:xDialog:v4.0.0'

}
```

## 🚀 快速开始

### 基础弹窗

```kotlin
// 最简单的弹窗
XDialogOptimized.create(supportFragmentManager)
    .layout(R.layout.dialog_custom)
    .show()

// 带回调的弹窗
XDialogOptimized.create(supportFragmentManager)
    .layout(R.layout.dialog_confirm)
    .onBind { holder ->
        holder.setText(R.id.tv_title, "确认操作")
        holder.setText(R.id.tv_message, "确定要执行此操作吗？")
    }
    .onClick(R.id.btn_confirm, R.id.btn_cancel) { holder, view, dialog ->
        when (view.id) {
            R.id.btn_confirm -> {
                // 确认操作
                dialog.dismiss()
            }
            R.id.btn_cancel -> {
                dialog.dismiss()
            }
        }
    }
    .show()
```

### 加载框

```kotlin
// 基础加载框
val loadingDialog = XLoadingDialog.create(supportFragmentManager)
    .message("加载中...")
    .show()

// 自定义样式加载框
XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.STYLE1)
    .message("正在处理...")
    .autoClose(5000) // 5秒后自动关闭
    .show()
```

### 日期选择器

```kotlin
// 基础日期选择器
XDatePickerDialog.create(supportFragmentManager)
    .initialDate(2024, 1, 15)
    .minDate(2020, 1, 1)
    .maxDate(2030, 12, 31)
    .onDateSelected { year, month, day ->
        Toast.makeText(this, "选择日期: $year-$month-$day", Toast.LENGTH_SHORT).show()
    }
    .show()
```

## 📚 详细使用指南

### 1. 常用弹窗

#### 1.1 基础弹窗

基础弹窗是最常用的对话框类型，支持自定义布局和丰富的配置选项。

```kotlin
XDialogOptimized.create(supportFragmentManager)
    .layout(R.layout.dialog_basic)           // 设置布局
    .size(300, 200)                          // 设置固定尺寸
    .gravity(Gravity.CENTER)                 // 设置位置
    .dimAmount(0.5f)                         // 设置背景透明度
    .cancelableOutside(true)                 // 点击外部可关闭
    .animation(R.style.DialogAnimation)      // 设置动画
    .tag("basic_dialog")                     // 设置标签
    .show()
```

**参数说明：**

| 参数 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| layout | @LayoutRes Int | 弹窗布局资源ID | 必填 |
| size | (Int, Int) | 弹窗宽高（像素） | WRAP_CONTENT |
| widthPercent | Float | 宽度百分比（0.0-1.0） | - |
| heightPercent | Float | 高度百分比（0.0-1.0） | - |
| gravity | Int | 弹窗位置 | Gravity.CENTER |
| dimAmount | Float | 背景透明度（0.0-1.0） | 0.5f |
| cancelableOutside | Boolean | 点击外部是否可关闭 | true |
| animation | @StyleRes Int | 动画资源ID | - |
| tag | String | 弹窗标签 | 自动生成 |

#### 1.2 确认框

```kotlin
// 自定义确认框布局 (res/layout/dialog_confirm.xml)
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="280dp"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="24dp"
    android:background="@drawable/dialog_bg">

    <TextView
        android:id="@+id/tv_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="确认"
        android:textSize="18sp"
        android:textStyle="bold"
        android:gravity="center" />

    <TextView
        android:id="@+id/tv_message"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="确定要执行此操作吗？"
        android:textSize="14sp"
        android:gravity="center" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="24dp"
        android:orientation="horizontal">

        <Button
            android:id="@+id/btn_cancel"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginEnd="8dp"
            android:text="取消" />

        <Button
            android:id="@+id/btn_confirm"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="8dp"
            android:text="确认" />

    </LinearLayout>

</LinearLayout>
```

```kotlin
// 使用确认框
fun showConfirmDialog(title: String, message: String, onConfirm: () -> Unit) {
    XDialogOptimized.create(supportFragmentManager)
        .layout(R.layout.dialog_confirm)
        .onBind { holder ->
            holder.setText(R.id.tv_title, title)
            holder.setText(R.id.tv_message, message)
        }
        .onClick(R.id.btn_confirm, R.id.btn_cancel) { holder, view, dialog ->
            when (view.id) {
                R.id.btn_confirm -> {
                    onConfirm()
                    dialog.dismiss()
                }
                R.id.btn_cancel -> {
                    dialog.dismiss()
                }
            }
        }
        .show()
}

// 调用示例
showConfirmDialog(
    title = "删除确认",
    message = "确定要删除这个文件吗？此操作不可撤销。"
) {
    // 执行删除操作
    deleteFile()
}
```

#### 1.3 提示框

```kotlin
// 信息提示框
fun showInfoDialog(title: String, message: String) {
    XDialogOptimized.create(supportFragmentManager)
        .layout(R.layout.dialog_info)
        .onBind { holder ->
            holder.setText(R.id.tv_title, title)
            holder.setText(R.id.tv_message, message)
        }
        .onClick(R.id.btn_ok) { _, _, dialog ->
            dialog.dismiss()
        }
        .show()
}

// 错误提示框
fun showErrorDialog(error: String) {
    XDialogOptimized.create(supportFragmentManager)
        .layout(R.layout.dialog_error)
        .onBind { holder ->
            holder.setText(R.id.tv_error, error)
            holder.findViewById<ImageView>(R.id.iv_icon)?.setImageResource(R.drawable.ic_error)
        }
        .onClick(R.id.btn_ok) { _, _, dialog ->
            dialog.dismiss()
        }
        .show()
}
```

### 2. 内置加载框

XLoadingDialog 提供了多种加载动画样式和丰富的自定义选项。

#### 2.1 基础用法

```kotlin
// 显示加载框
val loadingDialog = XLoadingDialog.create(supportFragmentManager)
    .message("加载中...")
    .show()

// 执行异步操作
doAsyncWork {
    // 操作完成后关闭加载框
    loadingDialog.dismiss()
}
```

#### 2.2 加载样式

```kotlin
// 旋转图标样式1（内置）
XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.STYLE1)
    .message("加载中...")
    .show()

// 旋转图标样式2（内置）
XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.STYLE2)
    .message("正在处理...")
    .show()

// 进度条样式
XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.PROGRESS)
    .message("下载中...")
    .progress(30)
    .maxProgress(100)
    .show()
```

#### 2.3 自定义选项

```kotlin
XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.STYLE1)
    .message("正在上传文件...")
    .size(120)                              // 加载图标大小
    .primaryColor(Color.parseColor("#007AFF")) // 主色调
    .textColor(Color.BLACK)                 // 文字颜色
    .textSize(16f)                          // 文字大小
    .cancelableOutside(false)               // 不可点击外部关闭
    .autoClose(10000)                       // 10秒后自动关闭
    .show()
```

#### 2.4 进度更新

```kotlin
// 创建进度加载框
val progressDialog = XLoadingDialog.create(supportFragmentManager)
    .style(XLoadingDialog.LoadingStyle.PROGRESS)
    .message("下载中...")
    .progress(0)
    .maxProgress(100)
    .show()

// 模拟下载进度更新
var progress = 0
val handler = Handler(Looper.getMainLooper())
val updateRunnable = object : Runnable {
    override fun run() {
        progress += 10
        progressDialog.updateProgress(progress)
        progressDialog.updateMessage("下载中... $progress%")
        
        if (progress < 100) {
            handler.postDelayed(this, 500)
        } else {
            progressDialog.dismiss()
        }
    }
}
handler.post(updateRunnable)
```

**LoadingDialog 参数说明：**

| 参数 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| style | LoadingStyle | 加载样式 (ICON, STYLE1, STYLE2, PROGRESS, CUSTOM_VIEW) | ICON |
| message | String | 加载文字 | "加载中..." |
| size | Int | 图标大小（像素） | 144 |
| progressWidth | Int | 进度条宽度（像素） | 240 |
| progress | Int | 当前进度 | 0 |
| maxProgress | Int | 最大进度 | 100 |
| primaryColor | Int | 主色调 | #007AFF |
| textColor | Int | 文字颜色 | #333333 |
| textSize | Float | 文字大小 | 14f |
| autoClose | Long | 自动关闭延时（毫秒） | 0（不自动关闭） |

### 3. 日期选择器

XDatePickerDialog 提供了功能完整的日期选择组件，支持日期范围限制、多语言等特性。

#### 3.1 基础用法

```kotlin
XDatePickerDialog.create(supportFragmentManager)
    .initialDate(2024, 1, 15)               // 初始日期
    .onDateSelected { year, month, day ->
        val selectedDate = "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        Toast.makeText(this, "选择日期: $selectedDate", Toast.LENGTH_SHORT).show()
    }
    .show()
```

#### 3.2 日期范围设置

```kotlin
XDatePickerDialog.create(supportFragmentManager)
    .initialDate(2024, 6, 15)
    .minDate(2020, 1, 1)                    // 最小日期
    .maxDate(2030, 12, 31)                  // 最大日期
    .onDateSelected { year, month, day ->
        // 处理日期选择
    }
    .show()
```

#### 3.3 完整配置示例

```kotlin
XDatePickerDialog.create(supportFragmentManager)
    .initialDate(2024, 6, 15)
    .minDate(2020, 1, 1)
    .maxDate(2030, 12, 31)
    .title("选择生日")                       // 自定义标题
    .confirmText("确定")                     // 确认按钮文字
    .cancelText("取消")                      // 取消按钮文字
    .onDateSelected { year, month, day ->
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)
        
        // 保存选择的日期
        saveBirthday(formattedDate)
    }
    .onCancel {
        // 取消选择的回调
        Log.d("DatePicker", "用户取消了日期选择")
    }
    .show()
```

**DatePickerDialog 参数说明：**

| 参数 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| initialDate | (Int, Int, Int) | 初始日期（年，月，日） | 当前日期 |
| minDate | (Int, Int, Int) | 最小日期（年，月，日） | 1900-1-1 |
| maxDate | (Int, Int, Int) | 最大日期（年，月，日） | 2100-12-31 |
| title | String | 对话框标题 | "选择日期" |
| confirmText | String | 确认按钮文字 | "确定" |
| cancelText | String | 取消按钮文字 | "取消" |

### 4. 扩展机制

xDialog 提供了灵活的扩展机制，支持自定义弹窗类型和插件开发。

#### 4.1 自定义弹窗类

```kotlin
// 继承 XDialogOptimized 创建自定义弹窗
class CustomDialog : XDialogOptimized() {
    
    private var customConfig: CustomConfig = CustomConfig()
    
    data class CustomConfig(
        var title: String = "",
        var items: List<String> = emptyList(),
        var onItemClick: ((Int, String) -> Unit)? = null
    )
    
    companion object {
        @JvmStatic
        fun create(fragmentManager: FragmentManager): CustomBuilder {
            return CustomBuilder(fragmentManager)
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCustomViews(view)
    }
    
    private fun setupCustomViews(view: View) {
        // 设置标题
        view.findViewById<TextView>(R.id.tv_title)?.text = customConfig.title
        
        // 设置列表
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_items)
        recyclerView?.adapter = CustomAdapter(customConfig.items) { position, item ->
            customConfig.onItemClick?.invoke(position, item)
            dismiss()
        }
    }
    
    class CustomBuilder(private val fragmentManager: FragmentManager) {
        private val dialogBuilder = XDialogOptimized.Builder(fragmentManager)
        private val customConfig = CustomConfig()
        
        init {
            dialogBuilder.layout(R.layout.dialog_custom_list)
        }
        
        fun title(title: String) = apply {
            customConfig.title = title
        }
        
        fun items(items: List<String>) = apply {
            customConfig.items = items
        }
        
        fun onItemClick(listener: (Int, String) -> Unit) = apply {
            customConfig.onItemClick = listener
        }
        
        fun show(): CustomDialog {
            return CustomDialog().apply {
                this.config = dialogBuilder.config.copy()
                this.customConfig = this@CustomBuilder.customConfig.copy()
            }.also {
                it.show(fragmentManager, "custom_dialog")
            }
        }
    }
}
```

#### 4.2 使用自定义弹窗

```kotlin
// 使用自定义列表弹窗
CustomDialog.create(supportFragmentManager)
    .title("选择操作")
    .items(listOf("编辑", "删除", "分享", "复制"))
    .onItemClick { position, item ->
        when (position) {
            0 -> editItem()
            1 -> deleteItem()
            2 -> shareItem()
            3 -> copyItem()
        }
    }
    .show()
```

#### 4.3 插件接口规范

```kotlin
// 定义弹窗插件接口
interface DialogPlugin {
    fun getName(): String
    fun getVersion(): String
    fun createDialog(fragmentManager: FragmentManager): XDialogOptimized
    fun getSupportedFeatures(): List<String>
}

// 实现插件
class ImagePickerPlugin : DialogPlugin {
    
    override fun getName() = "ImagePicker"
    
    override fun getVersion() = "1.0.0"
    
    override fun createDialog(fragmentManager: FragmentManager): XDialogOptimized {
        return ImagePickerDialog.create(fragmentManager)
    }
    
    override fun getSupportedFeatures() = listOf(
        "camera_capture",
        "gallery_selection",
        "image_crop"
    )
}

// 插件管理器
object DialogPluginManager {
    private val plugins = mutableMapOf<String, DialogPlugin>()
    
    fun registerPlugin(plugin: DialogPlugin) {
        plugins[plugin.getName()] = plugin
    }
    
    fun getPlugin(name: String): DialogPlugin? {
        return plugins[name]
    }
    
    fun getAllPlugins(): List<DialogPlugin> {
        return plugins.values.toList()
    }
}
```


#### 5.1 项目配置

**1. 混淆配置**

在 `proguard-rules.pro` 中添加：

```proguard
# xDialog
-keep class com.cl.xdialog.** { *; }
-keepclassmembers class com.cl.xdialog.** { *; }

# 保持自定义弹窗类
-keep class * extends com.cl.xdialog.XDialogOptimized { *; }
```


#### 5.2 最佳实践

**1. 内存管理**

```kotlin
class MainActivity : AppCompatActivity() {
    
    private var currentDialog: XDialogOptimized? = null
    
    override fun onDestroy() {
        super.onDestroy()
        // 确保弹窗被正确关闭
        currentDialog?.dismiss()
    }
    
    private fun showDialog() {
        // 关闭之前的弹窗
        currentDialog?.dismiss()
        
        currentDialog = XDialogOptimized.create(supportFragmentManager)
            .layout(R.layout.dialog_custom)
            .show()
    }
}
```

**2. 生命周期管理**

```kotlin
class MyFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 在 Fragment 中使用弹窗
        showDialog()
    }
    
    private fun showDialog() {
        // 使用 childFragmentManager 而不是 parentFragmentManager
        XDialogOptimized.create(childFragmentManager)
            .layout(R.layout.dialog_fragment)
            .show()
    }
}
```

**3. 状态保存**

```kotlin
class StatefulDialog : XDialogOptimized() {
    
    private var userInput: String = ""
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("user_input", userInput)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            userInput = it.getString("user_input", "")
        }
    }
}
```


## ❓ 常见问题

### Q1: 弹窗在屏幕旋转后消失了怎么办？

**A:** 这是 DialogFragment 的正常行为。可以通过以下方式解决：

```kotlin
// 方法1：禁用屏幕旋转
android:screenOrientation="portrait"

// 方法2：保存状态并重新创建
class PersistentDialog : XDialogOptimized() {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存必要的状态
    }
}
```

### Q2: 如何自定义弹窗动画？

**A:** 创建动画资源文件：

```xml
<!-- res/anim/dialog_enter.xml -->
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <scale
        android:duration="300"
        android:fromXScale="0.8"
        android:fromYScale="0.8"
        android:toXScale="1.0"
        android:toYScale="1.0"
        android:pivotX="50%"
        android:pivotY="50%" />
    <alpha
        android:duration="300"
        android:fromAlpha="0.0"
        android:toAlpha="1.0" />
</set>
```

```kotlin
XDialogOptimized.create(supportFragmentManager)
    .animation(R.style.CustomDialogAnimation)
    .show()
```

### Q3: 弹窗内容过多时如何处理？

**A:** 使用 ScrollView 或 RecyclerView：

```xml
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:maxHeight="400dp">
    
    <!-- 弹窗内容 -->
    
</ScrollView>
```

### Q4: 如何处理弹窗的内存泄漏？

**A:** 遵循以下最佳实践：

```kotlin
class SafeDialog : XDialogOptimized() {
    
    private var callback: ((String) -> Unit)? = null
    
    fun setCallback(callback: (String) -> Unit) {
        this.callback = callback
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理回调引用
        callback = null
    }
}
```

### Q5: 如何在弹窗中使用 ViewBinding？

**A:** 

```kotlin
class ViewBindingDialog : XDialogOptimized() {
    
    private var binding: DialogCustomBinding? = null
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCustomBinding.inflate(inflater, container, false)
        return binding?.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding?.apply {
            btnConfirm.setOnClickListener {
                dismiss()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
```

### 博客地址  
https://blog.csdn.net/a214024475/article/details/100926426

#### QQ 群：458173716  
<img src="https://github.com/cl-6666/serialPort/blob/master/img/qq2.jpg" width="350" height="560" alt="演示"/>  

⭐ 如果这个项目对你有帮助，请给我们一个 Star！