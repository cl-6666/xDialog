package com.cl.dialog.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cl.dialog.R;
import com.cl.xdialog.XDialogOptimized;
import com.cl.xdialog.XLoadingDialog;
import com.cl.xdialog.XDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 新架构弹窗组件演示页面
 * 展示各种常用的弹窗组件，包括基础弹窗、确认对话框、通知提示、加载状态和自定义内容弹窗
 */
public class DialogDemoActivity extends AppCompatActivity {

    private static final String TAG = "DialogDemo";
    private XDialogOptimized loadingDialog;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_demo);
        
        mainHandler = new Handler(Looper.getMainLooper());
        
        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("弹窗组件演示");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * 基础弹窗（Modal）演示
     */
    public void showBasicModal(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_simple)
                .widthPercent(this, 0.7f)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "基础弹窗");
                    viewHolder.setText(R.id.tv_content, "这是一个基础的模态弹窗示例，采用新架构实现。\n\n特点：\n• 居中显示\n• 支持点击外部关闭\n• 统一的视觉风格");
                    return null;
                })
                .onClick(new int[]{R.id.btn_confirm, R.id.btn_cancel}, (viewHolder, view1, tDialog) -> {
                    int id = view1.getId();
                    if (id == R.id.btn_confirm) {
                        showToast("确认按钮被点击");
                        tDialog.dismiss();
                    } else if (id == R.id.btn_cancel) {
                        tDialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    /**
     * 确认对话框演示
     */
    public void showConfirmDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_simple)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "确认操作");
                    viewHolder.setText(R.id.tv_content, "您确定要执行此操作吗？此操作不可撤销。");
                    return null;
                })
                .onClick(new int[]{R.id.btn_confirm, R.id.btn_cancel}, (viewHolder, view1, dialog) -> {
                    int id = view1.getId();
                    if (id == R.id.btn_confirm) {
                        Toast.makeText(DialogDemoActivity.this, "操作已确认", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (id == R.id.btn_cancel) {
                        Toast.makeText(DialogDemoActivity.this, "操作已取消", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    /**
     * 通知提示演示
     */
    public void showNotificationDemo(View view) {
        // 显示系统Toast
        showToast("这是一个系统Toast通知");
        
        // 延迟显示自定义通知弹窗
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showCustomNotification();
            }
        }, 1500);
    }

    /**
     * 自定义通知弹窗
     */
    private void showCustomNotification() {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_simple)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "📢 通知消息");
                    viewHolder.setText(R.id.tv_content, "这是一个自定义的通知弹窗，支持：\n\n• 自定义位置显示\n• 淡入淡出动画\n• 自动消失功能");
                    
                    // 隐藏取消按钮，只显示确认按钮
                    viewHolder.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                    viewHolder.setText(R.id.btn_confirm, "知道了");
                    return null;
                })
                .onClick(new int[]{R.id.btn_confirm}, (viewHolder, view, tDialog) -> {
                    tDialog.dismiss();
                    return null;
                })
                .show();
    }

    /**
     * 加载中状态弹窗演示 - 展示多种加载样式
     */
    public void showLoadingDialog(View view) {
        // 演示1: 脉冲动画样式 (内置样式1)
        showPulseLoading();
        
        // 延迟演示其他样式
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 演示2: 翻转动画样式 (内置样式2)
                showFlipLoading();
            }
        }, 3500);

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 演示4: 进度条样式 (内置样式5)
                showProgressLoading();
            }
        }, 7000);

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 演示5: 可配置演示 (黑色主题)
                showDarkLoading();
            }
        }, 10500);
    }
    
    /**
     * 演示加载样式1 (内置样式1)
     */
    private void showPulseLoading() {
        XLoadingDialog pulseDialog = XLoadingDialog.create(getSupportFragmentManager())
                .style(XLoadingDialog.LoadingStyle.STYLE1) // 使用内置STYLE1样式
                .message("正在处理 (样式1)...")
                .show();
        
        // 3秒后自动关闭
        mainHandler.postDelayed(() -> {
            if (pulseDialog.isVisible()) {
                pulseDialog.dismiss();
                showToast("样式1加载完成");
            }
        }, 3000);
    }
    
    /**
     * 演示加载样式2 (内置样式2)
     */
    private void showFlipLoading() {
        XLoadingDialog flipDialog = XLoadingDialog.create(getSupportFragmentManager())
                .style(XLoadingDialog.LoadingStyle.STYLE2) // 使用内置STYLE2样式
                .message("正在同步 (样式2)...")
                .show();
        
        // 3秒后自动关闭
        mainHandler.postDelayed(() -> {
            if (flipDialog.isVisible()) {
                flipDialog.dismiss();
                showToast("样式2加载完成");
            }
        }, 3000);
    }
    
    /**
     * 演示进度条加载样式 (内置样式5)
     */
    private void showProgressLoading() {
        XLoadingDialog progressDialog = XLoadingDialog.create(getSupportFragmentManager())
                .style(XLoadingDialog.LoadingStyle.PROGRESS)
                .message("下载中 (样式5)...")
                .progress(0)
                .maxProgress(100)
                .progressWidth(300)
                .primaryColor(0xFF34C759)
                .show();
        
        // 模拟进度更新
        simulateProgress(progressDialog);
    }
    
    /**
     * 模拟进度更新
     */
    private void simulateProgress(XLoadingDialog progressDialog) {
        final int[] currentProgress = {0};
        final Runnable updateProgress = new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isVisible() && currentProgress[0] <= 100) {
                    progressDialog.updateProgress(currentProgress[0]);
                    progressDialog.updateMessage("下载中... " + currentProgress[0] + "%");
                    currentProgress[0] += 10;
                    
                    if (currentProgress[0] <= 100) {
                        mainHandler.postDelayed(this, 300);
                    } else {
                        // 下载完成
                        mainHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog.isVisible()) {
                                    progressDialog.dismiss();
                                    showToast("进度条加载完成");
                                }
                            }
                        }, 500);
                    }
                }
            }
        };
        
        mainHandler.postDelayed(updateProgress, 300);
    }



    /**
     * 演示黑色背景加载样式
     */
    private void showDarkLoading() {
        XLoadingDialog darkDialog = XLoadingDialog.create(getSupportFragmentManager())
                .icon(R.mipmap.loading_test1)
                .rotate(true)
                .message("黑色主题加载...")
                .backgroundColor(0xCC000000) // 半透明黑色背景
                .textColor(0xFFFFFFFF)       // 白色文字
                .cancelableOutside(false)
                .show();

        // 3秒后自动关闭
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (darkDialog.isVisible()) {
                    darkDialog.dismiss();
                    showToast("黑色主题加载完成");
                }
            }
        }, 3000);
    }

    /**
     * 自定义内容弹窗演示
     */
    public void showCustomContentDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_custom)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "自定义输入");
                    viewHolder.setText(R.id.tv_content, "请输入您的反馈内容：");
                    
                    // 获取输入框并设置焦点
                    EditText editText = viewHolder.findViewById(R.id.et_input);
                    editText.setHint("请输入内容...");
                    editText.requestFocus();
                    return null;
                })
                .onClick(new int[]{R.id.btn_submit, R.id.btn_cancel}, (viewHolder, clickView, dialog) -> {
                    int id = clickView.getId();
                    if (id == R.id.btn_submit) {
                        EditText editText = viewHolder.findViewById(R.id.et_input);
                        String input = editText.getText().toString().trim();
                        if (!input.isEmpty()) {
                            showToast("您输入的内容：" + input);
                            dialog.dismiss();
                        } else {
                            showToast("请输入内容");
                        }
                    } else if (id == R.id.btn_cancel) {
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    /**
     * 显示有数据的列表弹窗
     */
    public void showListWithData(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_list_demo)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "列表演示 - 有数据");
                    
                    RecyclerView recyclerView = viewHolder.findViewById(R.id.recycler_view);
                    LinearLayout emptyLayout = viewHolder.findViewById(R.id.layout_empty);
                    
                    // 显示列表，隐藏空白页面
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                    
                    // 设置列表数据
                    List<ListItem> items = createSampleData();
                    ListAdapter adapter = new ListAdapter(items);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                    
                    return null;
                })
                .onClick(new int[]{R.id.btn_confirm, R.id.btn_cancel}, (viewHolder, clickView, dialog) -> {
                    int id = clickView.getId();
                    if (id == R.id.btn_confirm) {
                        showToast("确定操作");
                        dialog.dismiss();
                    } else if (id == R.id.btn_cancel) {
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    /**
     * 显示空白页面的列表弹窗
     */
    public void showListEmpty(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_list_demo)
                .onBind(viewHolder -> {
                    viewHolder.setText(R.id.tv_title, "列表演示 - 空白页面");
                    
                    RecyclerView recyclerView = viewHolder.findViewById(R.id.recycler_view);
                    LinearLayout emptyLayout = viewHolder.findViewById(R.id.layout_empty);
                    
                    // 隐藏列表，显示空白页面
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                    
                    // 配置空白页面
                    configureEmptyView(viewHolder);
                    
                    return null;
                })
                .onClick(new int[]{R.id.btn_confirm, R.id.btn_cancel}, (viewHolder, clickView, dialog) -> {
                    int id = clickView.getId();
                    if (id == R.id.btn_confirm) {
                        showToast("确定操作");
                        dialog.dismiss();
                    } else if (id == R.id.btn_cancel) {
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    /**
     * 配置空白页面
     */
    private void configureEmptyView(XDialogOptimized.ViewHolder viewHolder) {
        // 配置空白图标
        ImageView emptyIcon = viewHolder.findViewById(R.id.iv_empty_icon);
        emptyIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        
        // 配置空白标题
        TextView emptyTitle = viewHolder.findViewById(R.id.tv_empty_title);
        emptyTitle.setText("暂无数据");
        
        // 配置空白描述
        TextView emptyDesc = viewHolder.findViewById(R.id.tv_empty_desc);
        emptyDesc.setText("当前没有可显示的内容，您可以稍后再试");
        
        // 配置空白操作按钮
        View emptyAction = viewHolder.findViewById(R.id.btn_empty_action);
        emptyAction.setVisibility(View.VISIBLE);
        emptyAction.setOnClickListener(v -> {
            showToast("刷新操作");
            // 这里可以添加刷新逻辑
        });
    }

    /**
     * 创建示例数据
     */
    private List<ListItem> createSampleData() {
        List<ListItem> items = new ArrayList<>();
        items.add(new ListItem("项目 1", "这是第一个列表项的描述"));
        items.add(new ListItem("项目 2", "这是第二个列表项的描述"));
        items.add(new ListItem("项目 3", "这是第三个列表项的描述"));
        items.add(new ListItem("项目 4", "这是第四个列表项的描述"));
        items.add(new ListItem("项目 5", "这是第五个列表项的描述"));
        return items;
    }

    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 基础日期选择弹窗演示
     */
    public void showBasicDatePicker(View view) {
        XDatePickerDialog.create(getSupportFragmentManager())
                .title("选择日期")
                .onDateSelected((date, year, month, day) -> {
                    String selectedDate = year + "年" + (month + 1) + "月" + day + "日";
                    showToast("选择的日期：" + selectedDate);
                })
                .show();
    }

    /**
     * 自定义日期选择弹窗演示
     */
    public void showCustomDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18); // 18年前
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 10); // 10年后

        XDatePickerDialog.create(getSupportFragmentManager())
                .title("选择生日")
                .initialDate(1990, 0, 1) // 1990年1月1日
                .minDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .maxDate(maxDate.get(Calendar.YEAR), maxDate.get(Calendar.MONTH), maxDate.get(Calendar.DAY_OF_MONTH))
                .confirmText("确定")
                .cancelText("取消")
                .showTitle(true)
                .onDateSelected((date, year, month, day) -> {
                    String selectedDate = year + "年" + (month + 1) + "月" + day + "日";
                    showToast("选择的生日：" + selectedDate);
                })
                .show();
    }

    /**
     * 列表项数据类
     */
    private static class ListItem {
        String title;
        String subtitle;

        ListItem(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }
    }

    /**
     * 列表适配器
     */
    private static class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private List<ListItem> items;

        ListAdapter(List<ListItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_demo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ListItem item = items.get(position);
            holder.title.setText(item.title);
            holder.subtitle.setText(item.subtitle);
            
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "点击了: " + item.title, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView subtitle;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tv_title);
                subtitle = itemView.findViewById(R.id.tv_subtitle);
            }
        }
    }
}