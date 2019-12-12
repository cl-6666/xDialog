package com.cl.dialog.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.cl.dialog.R;
import com.cl.xdialog.XDialog;
import com.cl.xdialog.base.BindViewHolder;
import com.cl.xdialog.base.XBaseAdapter;
import com.cl.xdialog.list.XListDialog;
import com.cl.xdialog.listener.OnBindViewListener;
import com.cl.xdialog.listener.OnViewClickListener;

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
        new XDialog.Builder(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_click)
                .setScreenWidthAspect(DialogEncapActivity.this, 0.8f)
                .setTag("DialogTest")
                .setDimAmount(0.6f)
                .setGravity(Gravity.CENTER)
                .setOnBindViewListener(new OnBindViewListener() {
                    @Override
                    public void bindView(BindViewHolder bindViewHolder) {
                        bindViewHolder.setText(R.id.tv_content, "abcdef");
                    }
                })
                .addOnClickListener(R.id.btn_right, R.id.tv_title)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view1, XDialog tDialog) {
                        switch (view1.getId()) {
                            case R.id.btn_right:
                                Toast.makeText(DialogEncapActivity.this, "btn_right", Toast.LENGTH_SHORT).show();
                                tDialog.dismiss();
                                break;
                            case R.id.tv_title:
                                Toast.makeText(DialogEncapActivity.this, "tv_title", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    public void showTListDialog(View view) {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add("item:" + i);
        }

        XListDialog.Builder builder = new XListDialog.Builder(getSupportFragmentManager());
        builder.setLayoutRes(R.layout.dialog_recycler_test);
        builder.setHeight(600);
        builder.setScreenWidthAspect(DialogEncapActivity.this, 1.0f);
        builder.setAdapter(new XBaseAdapter<String>(R.layout.item_simple_text, datas) {
            @Override
            protected void onBind(BindViewHolder holder, int position, String item) {
                holder.setText(R.id.tv, item);
            }
        });
        builder.setOnAdapterItemClickListener(new XBaseAdapter.OnAdapterItemClickListener() {
            @Override
            public void onItemClick(BindViewHolder holder, int position, Object o, XDialog tDialog) {
                String item = (String) o;
                Toast.makeText(DialogEncapActivity.this, "pos:" + position + "," + item, Toast.LENGTH_SHORT).show();
                tDialog.dismiss();
            }
        });
        builder.setGravity(Gravity.BOTTOM);

        XDialog tListDialog = builder.create();
        tListDialog.show();
    }


}
