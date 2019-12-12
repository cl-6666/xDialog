package com.cl.dialog.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.cl.dialog.R;
import com.cl.dialog.dialogfragment.MyAlertDialogFragment;
import com.cl.dialog.dialogfragment.MyDialogFragment;

/**
 * DialogFragment的使用
 */
public class NormalDFActivity extends AppCompatActivity {
    int mStackLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_df);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogFragment(View view) {
        mStackLevel++;
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = MyDialogFragment.getInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }

    public void showAlertDialogFragment(View view) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance("showAlertDialogFragment000");
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
}
