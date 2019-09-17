package com.cl.xdialog.list;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cl.xdialog.R;
import com.cl.xdialog.XDialog;
import com.cl.xdialog.base.XBaseAdapter;
import com.cl.xdialog.base.XController;
import com.cl.xdialog.listener.OnBindViewListener;
import com.cl.xdialog.listener.OnViewClickListener;

/**
 * 项目：xDialog
 * 版权：蒲公英公司 版权所有
 * 作者：Arry
 * 版本：1.0
 * 创建日期：2019-09-17
 * 描述：
 *  列表弹窗  与TDialog实现分开处理
 * 修订历史：
 */
public class XListDialog extends XDialog {


    @Override
    protected void bindView(View view) {
        super.bindView(view);
        if (xController.getAdapter() != null) {//有设置列表
            //列表
            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            if (recyclerView == null) {
                throw new IllegalArgumentException("自定义列表xml布局,请设置RecyclerView的控件id为recycler_view");
            }
            xController.getAdapter().setTDialog(this);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(),xController.getOrientation(),false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(xController.getAdapter());
            xController.getAdapter().notifyDataSetChanged();
            if (xController.getAdapterItemClickListener() != null) {
                xController.getAdapter().setOnAdapterItemClickListener(xController.getAdapterItemClickListener());
            }
        }else{
            Log.d("TDialog","列表弹窗需要先调用setAdapter()方法!");
        }
    }

    /*********************************************************************
     * 使用Builder模式实现
     *
     */
    public static class Builder {

        XController.TParams params;

        public Builder(FragmentManager fragmentManager) {
            params = new XController.TParams();
            params.mFragmentManager = fragmentManager;
        }

        //各种setXXX()方法设置数据
        public XListDialog.Builder setLayoutRes(@LayoutRes int layoutRes) {
            params.mLayoutRes = layoutRes;
            return this;
        }

        //设置自定义列表布局和方向
        public XListDialog.Builder setListLayoutRes(@LayoutRes int layoutRes, int orientation) {
            params.listLayoutRes = layoutRes;
            params.orientation = orientation;
            return this;
        }

        /**
         * 设置弹窗宽度是屏幕宽度的比例 0 -1
         */
        public XListDialog.Builder setScreenWidthAspect(Activity activity, float widthAspect) {
            params.mWidth = (int) (getScreenWidth(activity) * widthAspect);
            return this;
        }

        public XListDialog.Builder setWidth(int widthPx) {
            params.mWidth = widthPx;
            return this;
        }

        /**
         * 设置屏幕高度比例 0 -1
         */
        public XListDialog.Builder setScreenHeightAspect(Activity activity, float heightAspect) {
            params.mHeight = (int) (getScreenHeight(activity) * heightAspect);
            return this;
        }

        public XListDialog.Builder setHeight(int heightPx) {
            params.mHeight = heightPx;
            return this;
        }

        public XListDialog.Builder setGravity(int gravity) {
            params.mGravity = gravity;
            return this;
        }

        public XListDialog.Builder setCancelOutside(boolean cancel) {
            params.mIsCancelableOutside = cancel;
            return this;
        }

        public XListDialog.Builder setDimAmount(float dim) {
            params.mDimAmount = dim;
            return this;
        }

        public XListDialog.Builder setTag(String tag) {
            params.mTag = tag;
            return this;
        }

        public XListDialog.Builder setOnBindViewListener(OnBindViewListener listener) {
            params.bindViewListener = listener;
            return this;
        }

        public XListDialog.Builder addOnClickListener(int... ids) {
            params.ids = ids;
            return this;
        }

        public XListDialog.Builder setOnViewClickListener(OnViewClickListener listener) {
            params.mOnViewClickListener = listener;
            return this;
        }

        //列表数据,需要传入数据和Adapter,和item点击数据
        public <A extends XBaseAdapter> XListDialog.Builder setAdapter(A adapter) {
            params.adapter = adapter;
            return this;
        }

        public XListDialog.Builder setOnAdapterItemClickListener(XBaseAdapter.OnAdapterItemClickListener listener) {
            params.adapterItemClickListener = listener;
            return this;
        }

        public XListDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            params.mOnDismissListener = dismissListener;
            return this;
        }

        public XListDialog create() {
            XListDialog dialog = new XListDialog();
            //将数据从Buidler的DjParams中传递到DjDialog中
            params.apply(dialog.xController);
            return dialog;
        }
    }
}
