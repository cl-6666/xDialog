


package com.cl.xdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cl.xdialog.adapter.TParams;
import com.cl.xdialog.base.BaseDialogFragment;
import com.cl.xdialog.base.BindViewHolder;
import com.cl.xdialog.base.XBaseAdapter;
import com.cl.xdialog.base.XController;
import com.cl.xdialog.listener.OnBindViewListener;
import com.cl.xdialog.listener.OnViewClickListener;

/**
 * name：cl
 * date：2023/4/24
 * desc：借鉴RecyclerView.Adapter的ViewHolder写法
 *   将Dialog的根布局传入,主要处理点击方法
 */
public class XDialog extends BaseDialogFragment {


    private static final String KEY_TCONTROLLER = "XController";
    protected XController xController;

    public XDialog() {
        xController = new XController();
    }

    /**
     * 当设备旋转时,会重新调用onCreate,进行数据恢复
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            xController = (XController) savedInstanceState.getSerializable(KEY_TCONTROLLER);
        }
    }

    /**
     * 进行数据保存
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_TCONTROLLER, xController);
        super.onSaveInstanceState(outState);
    }

    /**
     * 弹窗消失时回调方法
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        DialogInterface.OnDismissListener onDismissListener = xController.getOnDismissListener();
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    /**
     * 获取弹窗xml布局界面
     */
    @Override
    protected int getLayoutRes() {
        return xController.getLayoutRes();
    }

    @Override
    protected View getDialogView() {
        return xController.getDialogView();
    }

    @Override
    protected void bindView(View view) {
        //控件点击事件处理
        BindViewHolder viewHolder = new BindViewHolder(view, this);
        if (xController.getIds() != null && xController.getIds().length > 0) {
            for (int id : xController.getIds()) {
                viewHolder.addOnClickListener(id);
            }
        }
        //回调方法获取到布局,进行处理
        if (xController.getOnBindViewListener() != null) {
            xController.getOnBindViewListener().bindView(viewHolder);
        }
    }

    @Override
    public int getGravity() {
        return xController.getGravity();
    }


    @Override
    public int getDialogHeight() {
        return xController.getHeight();
    }

    @Override
    public int getDialogWidth() {
        return xController.getWidth();
    }

    @Override
    public String getFragmentTag() {
        return xController.getTag();
    }

    public OnViewClickListener getOnViewClickListener() {
        return xController.getOnViewClickListener();
    }

    @Override
    protected boolean isCancelableOutside() {
        return xController.isCancelableOutside();
    }

    @Override
    protected int getDialogAnimationRes() {
        return xController.getDialogAnimationRes();
    }

    @Override
    protected DialogInterface.OnKeyListener getOnKeyListener() {
        return xController.getOnKeyListener();
    }

    public XDialog show() {
        Log.d(TAG, "show");
        try {
            FragmentTransaction ft = xController.getFragmentManager().beginTransaction();
            ft.add(this, xController.getTag());
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e("TDialog", e.toString());
        }
        return this;
    }
    /**
     * 返回getAdapter出去，方便外部调用notifyDataSetChanged()
     */
    public XBaseAdapter getAdapter() {
        return xController.getAdapter();
    }


    /*********************************************************************
     * 使用Builder模式实现
     *
     */
    public static class Builder {

        TParams params;

        public Builder(FragmentManager fragmentManager) {
            params = new TParams();
            params.mFragmentManager = fragmentManager;
        }

        /**
         * 传入弹窗xmL布局文件
         *
         * @param layoutRes
         * @return
         */
        public Builder setLayoutRes(@LayoutRes int layoutRes) {
            params.mLayoutRes = layoutRes;
            return this;
        }

        /**
         * 直接传入控件
         *
         * @param view
         * @return
         */
        public Builder setDialogView(View view) {
            params.mDialogView = view;
            return this;
        }

        /**
         * 设置弹窗宽度(单位:像素)
         *
         * @param widthPx
         * @return
         */
        public Builder setWidth(int widthPx) {
            params.mWidth = widthPx;
            return this;
        }

        /**
         * 设置弹窗高度(px)
         *
         * @param heightPx
         * @return
         */
        public Builder setHeight(int heightPx) {
            params.mHeight = heightPx;
            return this;
        }

        /**
         * 设置弹窗宽度是屏幕宽度的比例 0 -1
         */
        public Builder setScreenWidthAspect(Context context, float widthAspect) {
            params.mWidth = (int) (getScreenWidth(context.getApplicationContext()) * widthAspect);
            return this;
        }

        /**
         * 设置弹窗高度是屏幕高度的比例 0 -1
         */
        public Builder setScreenHeightAspect(Context context, float heightAspect) {
            params.mHeight = (int) (getScreenHeight(context.getApplicationContext()) * heightAspect);
            return this;
        }

        /**
         * 设置弹窗在屏幕中显示的位置
         *
         * @param gravity
         * @return
         */
        public Builder setGravity(int gravity) {
            params.mGravity = gravity;
            return this;
        }

        /**
         * 设置弹窗在弹窗区域外是否可以取消
         *
         * @param cancel
         * @return
         */
        public Builder setCancelableOutside(boolean cancel) {
            params.mIsCancelableOutside = cancel;
            return this;
        }

        /**
         * 弹窗dismiss时监听回调方法
         *
         * @param dismissListener
         * @return
         */
        public Builder setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
            params.mOnDismissListener = dismissListener;
            return this;
        }


        /**
         * 设置弹窗背景透明度(0-1f)
         *
         * @param dim
         * @return
         */
        public Builder setDimAmount(float dim) {
            params.mDimAmount = dim;
            return this;
        }

        public Builder setTag(String tag) {
            params.mTag = tag;
            return this;
        }

        /**
         * 通过回调拿到弹窗布局控件对象
         *
         * @param listener
         * @return
         */
        public Builder setOnBindViewListener(OnBindViewListener listener) {
            params.bindViewListener = listener;
            return this;
        }

        /**
         * 添加弹窗控件的点击事件
         *
         * @param ids 传入需要点击的控件id
         * @return
         */
        public Builder addOnClickListener(int... ids) {
            params.ids = ids;
            return this;
        }

        /**
         * 弹窗控件点击回调
         *
         * @param listener
         * @return
         */
        public Builder setOnViewClickListener(OnViewClickListener listener) {
            params.mOnViewClickListener = listener;
            return this;
        }

        /**
         * 设置弹窗动画
         *
         * @param animationRes
         * @return
         */
        public Builder setDialogAnimationRes(int animationRes) {
            params.mDialogAnimationRes = animationRes;
            return this;
        }

        /**
         * 监听弹窗后，返回键点击事件
         */
        public Builder setOnKeyListener(DialogInterface.OnKeyListener keyListener) {
            params.mKeyListener = keyListener;
            return this;
        }

        /**
         * 真正创建TDialog对象实例
         *
         * @return
         */
        public XDialog create() {
            XDialog dialog = new XDialog();
            Log.d(TAG, "create");
            //将数据从Buidler的DjParams中传递到DjDialog中
            params.apply(dialog.xController);
            return dialog;
        }
    }

}
