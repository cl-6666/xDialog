package com.cl.dialog.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cl.dialog.R;
import com.cl.xdialog.XDialogOptimized;
import com.cl.xdialog.base.OptimizedViewHolder;
import com.cl.xdialog.adapter.XOptimizedAdapter;
import com.cl.xdialog.XListDialogOptimized;
import com.cl.xdialog.listener.OnOptimizedAdapterItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment封装
 */
public class DialogEncapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_encap);
    }

    /**
     * 展示Dialog
     */
    public void showTDialog(View view) {
        XDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_click)
                .widthPercent(DialogEncapActivity.this, 0.8f)
                .tag("DialogTest")
                .dimAmount(0.6f)
                .gravity(Gravity.CENTER)
                .onBind(bindViewHolder -> {
                    bindViewHolder.setText(R.id.tv_content, "abcdef");
                    return null;
                })
                .onClick(new int[]{R.id.btn_right, R.id.tv_title}, (XDialogOptimized.ViewHolder viewHolder, View view1, XDialogOptimized dialog) -> {
                    int id = view1.getId();
                    if (id == R.id.btn_right) {
                        Toast.makeText(DialogEncapActivity.this, "btn_right", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else if (id == R.id.tv_title) {
                        Toast.makeText(DialogEncapActivity.this, "tv_title", Toast.LENGTH_SHORT).show();
                    }
                    return null;
                })
                .show();
    }

    public void showTListDialog(View view) {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("item:" + i);
        }

        XListDialogOptimized.create(getSupportFragmentManager())
                .layout(R.layout.dialog_recycler_test)
                .recyclerViewId(R.id.recycler_view)
                .height(600)
                .widthPercent(DialogEncapActivity.this, 1.0f)
                .optimizedAdapter(new XOptimizedAdapter<String>(R.layout.item_simple_text, datas) {
                    @Override
                    protected void onBind(OptimizedViewHolder holder, int position, String item) {
                        holder.setText(R.id.tv, item);
                    }
                })
                .onOptimizedItemClick(new OnOptimizedAdapterItemClickListener<String>() {
                    @Override
                    public void onItemClick(OptimizedViewHolder holder, int position, String o, XDialogOptimized dialog) {
                        String item = (String) o;
                        Toast.makeText(DialogEncapActivity.this, "pos:" + position + "," + item, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .gravity(Gravity.BOTTOM)
                .show();
    }


}
