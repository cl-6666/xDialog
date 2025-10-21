package com.cl.dialog.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cl.dialog.R;
import com.cl.xdialog.XDialogOptimized;
import com.cl.xdialog.base.OptimizedViewHolder;
import com.cl.xdialog.adapter.XOptimizedAdapter;
import com.cl.xdialog.XListDialogOptimized;
import com.cl.xdialog.listener.OnOptimizedAdapterItemClickListener;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * 常用的各种Dialog
 */
public class DiffentDialogActivity extends AppCompatActivity {

    private static final String TAG = "TDialog";
    private static final int WHAT_LOADING = 0;
    private static final int WHAT_PROGRESS = 1;
    private String[] data = {"java", "android", "NDK", "c++", "python", "ios", "Go", "Unity3D", "Kotlin", "Swift", "js"};
    private String[] sharePlatform = {"微信", "朋友圈", "短信", "微博", "QQ空间", "Google", "FaceBook", "微信", "朋友圈", "短信", "微博", "QQ空间"};

    private String[] test = {"微信"};

    private static XDialogOptimized tDialog;
    static int currProgress = 5;
    private static ProgressBar progressBar;
    private static TextView tvProgress;
    //静态Handler防止内存泄漏
    private static Handler handler;


    private static class MyHandler extends Handler {
        WeakReference<DiffentDialogActivity> weakReference;

        public MyHandler(DiffentDialogActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() != null) {
                // update android ui
                switch (msg.what) {
                    case WHAT_LOADING:
                        if (tDialog != null) {
                            tDialog.dismiss();
                            tDialog=null;
                        }
                        return;

                    case WHAT_PROGRESS:
                        currProgress += 20;
                        progressBar.setProgress(currProgress);
                        tvProgress.setText("progress:" + currProgress + "/100");
                        if (tDialog != null && currProgress >= 100) {
                            handler.removeMessages(WHAT_PROGRESS);
                            currProgress = 0;
                            tDialog.dismiss();
                            tDialog = null;
                        } else {
                            handler.sendEmptyMessageDelayed(WHAT_PROGRESS, 1000);
                        }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diffent_dialog);
        //创建Handler
        handler = new MyHandler(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果参数为null的话，会将所有的Callbacks和Messages全部清除掉。
        handler.removeCallbacksAndMessages(null);
    }

    public void testDialog(View view2) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        tDialog = XDialogOptimized.create(getSupportFragmentManager())
                .customView(view)
                .cancelableOutside(false)
                .onKey(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Toast.makeText(DiffentDialogActivity.this, "keyCode:" + keyCode, Toast.LENGTH_SHORT).show();
                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }

    public void useTDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_click)    //设置弹窗展示的xml布局
//                .customView(view)  //设置弹窗布局,直接传入View
                .width(600)  //设置弹窗宽度(px)
                .height(800)  //设置弹窗高度(px)
                .widthPercent(this, 0.8f)   //设置弹窗宽度(参数aspect为屏幕宽度比例 0 - 1f)
                .heightPercent(this, 0.3f)  //设置弹窗高度(参数aspect为屏幕宽度比例 0 - 1f)
                .gravity(Gravity.CENTER)     //设置弹窗展示位置
                .tag("DialogTest")   //设置Tag
                .dimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
                .cancelableOutside(true)     //弹窗在界面外是否可以点击取消
                .animation(R.style.animate_dialog) //设置弹窗动画
                .onDismiss(new DialogInterface.OnDismissListener() { //弹窗隐藏时回调方法
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(DiffentDialogActivity.this, "弹窗消失回调", Toast.LENGTH_SHORT).show();
                    }
                })
                .onBind((XDialogOptimized.ViewHolder viewHolder) -> {   //通过ViewHolder拿到控件对象,进行修改
                    viewHolder.setText(R.id.tv_content, "abcdef");
                    viewHolder.setText(R.id.tv_title, "我是Title");
                    return null;
                })
                .onClick(new int[]{R.id.btn_left, R.id.btn_right, R.id.tv_title}, (XDialogOptimized.ViewHolder viewHolder, View view1, XDialogOptimized dialog) -> {     //View控件点击事件回调
                    int id = view1.getId();
                    if (id == R.id.btn_left) {
                        Toast.makeText(DiffentDialogActivity.this, "left clicked", Toast.LENGTH_SHORT).show();
                    } else if (id == R.id.btn_right) {
                        Toast.makeText(DiffentDialogActivity.this, "right clicked", Toast.LENGTH_SHORT).show();
                    }else if (id == R.id.tv_title){
                        Toast.makeText(DiffentDialogActivity.this, "title clicked", Toast.LENGTH_SHORT).show();
                        viewHolder.setText(R.id.tv_title, "Title点击了");
                    }
                    return null;
                })
                .onKey(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        Toast.makeText(DiffentDialogActivity.this, "按键 keyCode:" + keyCode, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .show();    //展示
    }

    public void upgradeDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_version_upgrde)
                .widthPercent(this, 0.7f)
                .animation(R.style.animate_dialog_scale)
                .onClick(new int[]{R.id.tv_cancel, R.id.tv_confirm}, (XDialogOptimized.ViewHolder viewHolder, View view1, XDialogOptimized dialog) -> {
                    int id = view1.getId();
                    if (id == R.id.tv_cancel) {
                        dialog.dismiss();
                    }else if (id == R.id.tv_confirm){
                        Toast.makeText(DiffentDialogActivity.this, "开始下载新版本apk文件", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    public void upgradeDialogStrong(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_version_upgrde_strong)
                .widthPercent(this, 0.7f)
                .cancelableOutside(false)
                .animation(R.style.animate_dialog_scale)
                .onKey((dialog, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Toast.makeText(DiffentDialogActivity.this, "返回健无效，请强制升级后使用", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                })
                .onClick(new int[]{R.id.tv_confirm}, (viewHolder, view1, dialog) -> {
                    Toast.makeText(DiffentDialogActivity.this, "开始下载新版本apk文件", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return null;
                })
                .show();
    }

    public void tipsDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_vb_convert)
                .onBind(viewHolder -> {
                    // onBind logic here
                    return null;
                })
                .onClick(new int[]{R.id.tv_jiuyuan_desc, R.id.tv_cancel, R.id.tv_confirm}, (viewHolder, view1, dialog) -> {
                    int id = view1.getId();
                    if (id == R.id.btn_left) {
                        Toast.makeText(DiffentDialogActivity.this, "进入说明界面", Toast.LENGTH_SHORT).show();
                    }else if (id == R.id.tv_confirm){
                        Toast.makeText(DiffentDialogActivity.this, "执行优惠券兑换逻辑", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else if (id == R.id.tv_cancel){
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    public void loadingDialog(View view) {
        tDialog = XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_loading)
                .onBind(viewHolder -> {
                    // onBind logic here
                    return null;
                })
                .show();
        handler.sendEmptyMessageDelayed(WHAT_LOADING, 5 * 1000);
    }

    public void progressDialog(final View view) {
        tDialog = XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_loading_progress)
                .onBind(viewHolder -> {
                    progressBar = viewHolder.findViewById(R.id.progress_bar);
                    tvProgress = viewHolder.findViewById(R.id.tv_progress);
                    return null;
                })
                .show();
        handler.sendEmptyMessageDelayed(WHAT_PROGRESS, 1000);
    }

    public void dialogView(View view) {
        TextView textView = new TextView(this);
        textView.setText("DialogView");
        textView.setTextSize(25);
        textView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        textView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

        tDialog = XDialogOptimized.create(getSupportFragmentManager())
                .customView(textView)
                .onBind(viewHolder -> {
                    // onBind logic here
                    return null;
                })
                .show();
    }

    public void homeBannerDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_home_ad)
                .heightPercent(this, 0.7f)
                .widthPercent(this, 0.8f)
                .dimAmount(0.6f)
                .onBind(viewHolder -> {
                    //可对图片进行修改
                    return null;
                })
                .onClick(new int[]{R.id.iv_close}, (viewHolder, view1, dialog) -> {
                    dialog.dismiss();
                    return null;
                })
                .show();

    }

    public void updateHead(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_change_avatar)
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .animation(R.style.animate_dialog)
                .onClick(new int[]{R.id.tv_open_camera, R.id.tv_open_album, R.id.tv_cancel}, (XDialogOptimized.ViewHolder viewHolder, View view1, XDialogOptimized dialog) -> {
                    int id = view1.getId();
                    if (id == R.id.tv_open_camera) {
                        Toast.makeText(DiffentDialogActivity.this, "打开相机", Toast.LENGTH_SHORT).show();

                    }else if (id == R.id.tv_open_album) {
                        Toast.makeText(DiffentDialogActivity.this, "打开相册", Toast.LENGTH_SHORT).show();

                    }else if (id == R.id.tv_cancel) {
                        dialog.dismiss();
                    }
                    return null;
                })
                .show();
    }

    public void listDialog(View view) {
        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_recycler_test)
                .recyclerViewId(R.id.recycler_view)
                .height(600)
                .widthPercent(this, 0.8f)
                .gravity(Gravity.CENTER)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_simple_text, Arrays.asList(data)) {

                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .onOptimizedItemClick(new OnOptimizedAdapterItemClickListener<String>() {
                    @Override
                    public void onItemClick(OptimizedViewHolder holder, int position, String s, XDialogOptimized tDialog) {
                        Toast.makeText(DiffentDialogActivity.this, "click:" + s, Toast.LENGTH_SHORT).show();
                        tDialog.dismiss();
                    }
                })
                .show();
    }

    public void bottomListDialog(View view) {
        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_recycler_test)
                .recyclerViewId(R.id.recycler_view)
                .heightPercent(this, 0.5f)
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_simple_text, Arrays.asList(data)) {
                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .onOptimizedItemClick(new OnOptimizedAdapterItemClickListener<String>() {
                    @Override
                    public void onItemClick(OptimizedViewHolder holder, int position, String s, XDialogOptimized tDialog) {
                        Toast.makeText(DiffentDialogActivity.this, "click:" + s, Toast.LENGTH_SHORT).show();
                        tDialog.dismiss();
                    }
                })
                .onDismiss(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        Toast.makeText(DiffentDialogActivity.this, "setOnDismissListener 回调", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    //评价 弹出输入框
    public void evaluateDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_evaluate)
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .onBind((XDialogOptimized.ViewHolder viewHolder) -> {
                    final EditText editText = viewHolder.findViewById(R.id.editText);
                    editText.post(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) DiffentDialogActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(editText, 0);
                        }
                    });
                    return null;
                })
                .onClick(new int[]{R.id.btn_evluate}, (viewHolder, view1, dialog) -> {
                    EditText editText = viewHolder.findViewById(R.id.editText);
                    String content = editText.getText().toString().trim();
                    Toast.makeText(DiffentDialogActivity.this, "评价内容:" + content, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return null;
                })
                .show();
    }

    //底部分享
    public void shareDialog(View view) {
        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_share_recycler)
                .recyclerViewId(R.id.recycler_view)
                .layoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_share, Arrays.asList(sharePlatform)) {
                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .onOptimizedItemClick((OnOptimizedAdapterItemClickListener<String>) (holder, position, item, dialog) -> {
                    Toast.makeText(DiffentDialogActivity.this, item, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                })
                .show();
    }


    public void multipleDisplaysDialog(View view){
        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_share_recycler)
                .recyclerViewId(R.id.recycler_view)
                .layoutManager(new GridLayoutManager(this, 4))
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_share, Arrays.asList(sharePlatform)) {
                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .onOptimizedItemClick((OnOptimizedAdapterItemClickListener<String>) (holder, position, item, dialog) -> {
                    Toast.makeText(DiffentDialogActivity.this, item, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                })
                .show();
    }


    public void listEmptyViewDialog(View view){
        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_share_recycler)
                .recyclerViewId(R.id.recycler_view)
                .layoutManager(new GridLayoutManager(this, 4))
                .widthPercent(this, 1.0f)
                .gravity(Gravity.BOTTOM)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_share, Arrays.asList()) {
                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .onOptimizedItemClick((OnOptimizedAdapterItemClickListener<String>) (holder, position, item, dialog) -> {
                    Toast.makeText(DiffentDialogActivity.this, item, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                })
                .show();
    }

}
