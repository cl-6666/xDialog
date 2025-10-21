package com.cl.dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cl.dialog.ui.DatePickerTestActivity;
import com.cl.dialog.ui.DialogDemoActivity;
import com.cl.dialog.ui.DialogEncapActivity;
import com.cl.dialog.ui.DiffentDialogActivity;
import com.cl.dialog.ui.NormalDFActivity;
import com.cl.dialog.ui.SystemDialog;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /**
     * 系统Dialog使用
     *
     * @param view
     */
    public void systemDialog(View view) {
        startActivity(new Intent(this, SystemDialog.class));
    }

    /**
     * DialogFragment的使用
     *
     * @param view
     */
    public void NormalDF(View view) {
        startActivity(new Intent(this, NormalDFActivity.class));
    }

    /**
     * DialogFragment封装
     *
     * @param view
     */
    public void DialogEncap(View view) {
        startActivity(new Intent(this, DialogEncapActivity.class));
    }

    /**
     * 常用的各种Dialog
     *
     * @param view
     */
    public void diffentDialog(View view) {
        startActivity(new Intent(this, DiffentDialogActivity.class));
    }

    public void diffentDialog2(View view) {
        startActivity(new Intent(this, DatePickerTestActivity.class));
    }

    /**
     * 新架构弹窗组件演示
     *
     * @param view
     */
    public void dialogDemo(View view) {
        startActivity(new Intent(this, DialogDemoActivity.class));
    }


}
