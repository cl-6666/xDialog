#### 一.XDialog的由来
所有框架的由来都是为了更方便,更高效的解决问题,XDialog也一样,是为了在项目中更高效的实现项目的弹窗效果
##### 1.DialogFragment的优点
* DialogFragment 本身是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
* 在手机配置变化导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建，然而基于 Dialog 实现的对话框却没有这样的能力

##### API方法说明

|	XDialog方法	|	API方法说明	|	XListDialog方法	|	API方法说明	|
|	---		|	---		|	---		|	---		|
|	setLayoutRes	|	传入弹窗xmL布局文件	|	setLayoutRes	|	各种setXXX()方法设置数据	|
|	setDialogView	|	直接传入控件	|	setListLayoutRes	|	设置自定义LayoutManager列表布局和方向	|
|	setWidth	|	设置弹窗宽度(单位:像素)	|	setGridLayoutRes	|	设置自定义GridLayout列表布局和方向	|
|	setHeight	|	设置弹窗高度(px)	|		|		|
|	setScreenWidthAspect	|	设置弹窗宽度是屏幕宽度的比例 0 -1	|		|		|
|	setScreenHeightAspect	|	设置弹窗高度是屏幕高度的比例 0 -1	|		|		|
|	setGravity	|	 设置弹窗在屏幕中显示的位置	|		|		|
|	setCancelableOutside	|	设置弹窗在弹窗区域外是否可以取消	|		|		|
|	setOnDismissListener	|	弹窗dismiss时监听回调方法	|		|		|
|	setDimAmount	|	设置弹窗背景透明度(0-1f)	|		|		|
|	setTag	|	设置弹窗的标记	|		|		|
|	setOnBindViewListener	|	通过回调拿到弹窗布局控件对象	|		|		|
|	addOnClickListener	|	添加弹窗控件的点击事件	|		|		|
|	setOnViewClickListener	|	弹窗控件点击回调	|		|		|
|	setDialogAnimationRes	|	设置弹窗动画	|		|		|
|	setOnKeyListener	|	监听弹窗后，返回键点击事件	|		|		|



##### 使用介绍
2.Activity或者Fragment中使用
```java
  //普通弹窗效果演示
  new XDialog.Builder(getSupportFragmentManager())
          .setLayoutRes(R.layout.dialog_click)
          .setDialogView(view)
          .create()
          .show();
```

##### 列表弹窗
为了方便使用:
1. 不用传入layoutRes布局文件,XDialog内部设置了一个默认的RecyclerView布局,且RecyclerView的控件id为recycler_view,背景为#ffffff
2. setAdapter(Adapter),设置recyclerview的adapter,为了封装Adapter的item点击事件,传入的adapter需要为TBaseAdapter的实现类
3. setOnAdapterItemClickListener(),设置adapter的点击事件
```java
<?xml version="1.0" encoding="utf-8"?>
<androidx.recyclerview.widget.RecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#ffffff"
    android:orientation="vertical" />
```

##### 如果使用者需要使用自己的列表布局时,可以使用setListLayoutRes(layotuRes,LayoutManager)方法设置xml布局和布局管理器LayoutManager,切记xml布局中的RecyclerView的id必须设置为recycler_view(如效果图中的分享弹窗)

```
public void shareDialog(View view) {
     new XListDialog.Builder(getSupportFragmentManager())
                .setListLayoutRes(R.layout.dialog_share_recycler, LinearLayoutManager.HORIZONTAL)
                .setScreenWidthAspect(this, 1.0f)
                .setGravity(Gravity.BOTTOM)
                .setAdapter(new XBaseAdapter<String>(R.layout.item_share, Arrays.asList(sharePlatform)) {
                    @Override
                    protected void onBind(BindViewHolder holder, int position, String s) {
                        holder.setText(R.id.tv, s);
                    }
                })
                .setOnAdapterItemClickListener(new OnAdapterItemClickListener<String>() {
                    @Override
                    public void onItemClick(BindViewHolder holder, int position, String item, XDialog tDialog) {
                        Toast.makeText(DiffentDialogActivity.this, item, Toast.LENGTH_SHORT).show();
                        tDialog.dismiss();

                    }
                })
                .create()
                .show();
}
```

#### 框架原理解析
XDialog的实现原理主要分为三步
1. 实例化XDialog.Builer对象builder,然后调用各种setXXX()方法设置数据,设置的数据都保存在XController.XParams实例中
2. create()方法调用后才会实例化XDialog对象,并将XController.TParams中设置的数据传递到XDialog的属性XController对象中
3. show()方法调用显示弹窗

