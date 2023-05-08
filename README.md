#### 前言
>我看到很多封装Dialog的，但是我觉得都有缺点，所以我也就取其中一个封装通用的弹窗Dialog出来，支持AndroidX，平常开发当中自用，会经常维护。
>建议以依赖方式使用，最后希望大家能给出宝贵的意见。
##### 文章目录
* XDialog框架的由来
* 框架使用解析
* 框架原理解析

#### 一.XDialog的由来
所有框架的由来都是为了更方便,更高效的解决问题,XDialog也一样,是为了在项目中更高效的实现项目的弹窗效果

XDialog是继承自DialogFragment进行封装的,大部分开发者在实现弹窗效果的时候,会首选系统提供的AlertDialog;
但是使用系统的Dialog在某些情况下会出现问题,最常见的场景是当手机屏幕旋转时Dialog弹窗会消失,并抛出一个系统,这个异常不会导致异常崩溃,因为Google开发者知道这个问题,并进行了处理.
Dialog使用起来其实更简单,但是Google却是推荐尽量使用DialogFragment.
##### 1.DialogFragment的优点
* DialogFragment 本身是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
* 在手机配置变化导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建，然而基于 Dialog 实现的对话框却没有这样的能力
####使用
1.添加依赖
 a. 在工程build.gradle文件repositories中添加
```
  repositories {
    ...
    jcenter() 
}
```
 b.在model下build.gradle文件添加
```java
支持Androidx
 implementation 'com.github.cl-6666:xDialog:v3.1.0'
v7请使用
implementation 'com.github.cl-6666:xDialog:v1.0.2'


```
2.Activity或者Fragment中使用
```java

        new XDialog.Builder(getSupportFragmentManager())
                .setLayoutRes(R.layout.dialog_click)    //设置弹窗展示的xml布局
//                .setDialogView(view)  //设置弹窗布局,直接传入View
                .setWidth(600)  //设置弹窗宽度(px)
                .setHeight(800)  //设置弹窗高度(px)
                .setScreenWidthAspect(this, 0.8f)   //设置弹窗宽度(参数aspect为屏幕宽度比例 0 - 1f)
                .setScreenHeightAspect(this, 0.3f)  //设置弹窗高度(参数aspect为屏幕宽度比例 0 - 1f)
                .setGravity(Gravity.CENTER)     //设置弹窗展示位置
                .setTag("DialogTest")   //设置Tag
                .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
                .setCancelableOutside(true)     //弹窗在界面外是否可以点击取消
                .setDialogAnimationRes(R.style.animate_dialog)  //设置弹窗动画
                .setOnDismissListener(new DialogInterface.OnDismissListener() { //弹窗隐藏时回调方法
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(DiffentDialogActivity.this, "弹窗消失回调", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnBindViewListener(new OnBindViewListener() {   //通过BindViewHolder拿到控件对象,进行修改
                    @Override
                    public void bindView(BindViewHolder bindViewHolder) {
                        bindViewHolder.setText(R.id.tv_content, "abcdef");
                        bindViewHolder.setText(R.id.tv_title, "我是Title");
                    }
                })
                .addOnClickListener(R.id.btn_left, R.id.btn_right, R.id.tv_title)   //添加进行点击控件的id
                .setOnViewClickListener(new OnViewClickListener() {     //View控件点击事件回调
                    @Override
                    public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
                        switch (view.getId()) {
                            case R.id.btn_left:
                                Toast.makeText(DiffentDialogActivity.this, "left clicked", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.btn_right:
                                Toast.makeText(DiffentDialogActivity.this, "right clicked", Toast.LENGTH_SHORT).show();
                                tDialog.dismiss();
                                break;
                            case R.id.tv_title:
                                Toast.makeText(DiffentDialogActivity.this, "title clicked", Toast.LENGTH_SHORT).show();
                                viewHolder.setText(R.id.tv_title, "Title点击了");
                                break;
                        }
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return false;
                    }
                })
                .create()   //创建TDialog
                .show();    //展示

```
添加动画姿势
```java
新建补间动画文件
enter.xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="300"
    android:fromYDelta="100%p"
    android:toYDelta="0%p">

</translate>
exit.xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="300"
    android:fromYDelta="0%p"
    android:toYDelta="100%p">

</translate>
style.xml文件
<style name="animate_dialog">
    <item name="android:windowEnterAnimation">@anim/enter</item>
    <item name="android:windowExitAnimation">@anim/exit</item>
</style>
```
#### 使用方法解析
TDialog的实现原理和系统Dialog原理差不多,主要使用Builder设计模式实现
1.创建弹窗,传入xml布局文件或者传入View控件,且自己设置背景色,因为默认是透明背景色
```java
new XDialog.Builder(getSupportFragmentManager())
        .setLayoutRes(R.layout.dialog_click)
        .setDialogView(view)
        .create()
        .show();
```
2.设置弹窗的宽高(如果不设置宽或者高,默认使用包裹内容的高度)
```java
   new XDialog.Builder(getSupportFragmentManager())
            .setLayoutRes(R.layout.dialog_click)
            .setWidth(600)  //设置弹窗固定宽度(单位:px)
            .setHeight(800)//设置弹窗固定高度
            .setScreenWidthAspect(Activity.this,0.5f) //动态设置弹窗宽度为屏幕宽度百分比(取值0-1f)
            .setScreenHeightAspect(Activity.this,0.6f)//设置弹窗高度为屏幕高度百分比(取值0-1f)
            .create()
            .show();
```
3.设置弹窗展示的位置
```java
.setGravity(Gravity.CENTER)
其他位置有:Gravity.Bottom / Gravity.LEFT等等和设置控件位置一样
```
4.设置弹窗背景色透明度(取值0-1f,0为全透明)
```java
.setDimAmount(0.6f)
```
5.设置弹窗外部是否可以点击取消(默认可点击取消),和设置弹窗是否可以取消(默认可取消),弹窗隐藏时回调方法
```java
.setCancelableOutside(true)
.setOnDismissListener(new DialogInterface.OnDismissListener() { //弹窗隐藏时回调方法
    @Override
    public void onDismiss(DialogInterface dialog) {
        Toast.makeText(DiffentDialogActivity.this, "弹窗隐藏回调", Toast.LENGTH_SHORT).show();
    }
})
```
6.当弹窗需要动态改变控件子view内容时,这里借鉴了RecyclerView.Adapter的设计思想,内部封装好一个BindViewHolder
```java
.setOnBindViewListener(new OnBindViewListener() {
    @Override
    public void bindView(BindViewHolder bindViewHolder) {
        bindViewHolder.setText(R.id.tv_content, "abcdef");
    bindViewHolder.setText(R.id.tv_title,"我是Title");        
    }
})
```
7.监听弹窗子控件的点击事件,内部也是通过BindViewHolder实现
addOnClickListener(ids[])只需要将点击事件控件的id传入,并设置回调接口setOnViewClickListener()
```java
.addOnClickListener(R.id.btn_right, R.id.tv_title)
.setOnViewClickListener(new OnViewClickListener() {
    @Override
    public void onViewClick(BindViewHolder viewHolder,View view1, TDialog tDialog) {
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
```
8.设置弹窗动画
```java
.setDialogAnimationRes(R.style.animate_dialog) 
```
9.监听返回键点击事件,需配合setCancelableOutside(false)方法一起使用
```java
.setOnKeyListener(new DialogInterface.OnKeyListener() {
    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
           if (keyCode == KeyEvent.KEYCODE_BACK) {
                Toast.makeText(DiffentDialogActivity.this, "返回健无效，请强制升级后使用", Toast.LENGTH_SHORT).show();
                 return true;
           }
           return false;  //默认返回值
     }
})
```
a.列表弹窗-使用XListDialog,XListDialog继承自XDialog,可以使用父类所有的方法,并且扩展列表数据展示丰富setAdapter()和item点击事件回调方法setOnAdapterItemClickListener()
```java
new XListDialog.Builder(getSupportFragmentManager())
        .setHeight(600)
        .setScreenWidthAspect(this, 0.8f)
        .setGravity(Gravity.CENTER)
        .setAdapter(new TBaseAdapter<String>(R.layout.item_simple_text, Arrays.asList(data)) {

            @Override
            protected void onBind(BindViewHolder holder, int position, String s) {
                holder.setText(R.id.tv, s);
            }
        })
        .setOnAdapterItemClickListener(new TBaseAdapter.OnAdapterItemClickListener<String>() {
            @Override
            public void onItemClick(BindViewHolder holder, int position, String s, TDialog tDialog) {
                Toast.makeText(DiffentDialogActivity.this, "click:" + s, Toast.LENGTH_SHORT).show();
                tDialog.dismiss();
            }
        })
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
TBaseAdapter实现:需要使用者传入item的xml布局,和List数据
```java
public abstract class XBaseAdapter<T> extends RecyclerView.Adapter<BindViewHolder> {

    private final int layoutRes;
    private List<T> datas;
    private OnAdapterItemClickListener adapterItemClickListener;
    private XDialog dialog;

    protected abstract void onBind(BindViewHolder holder, int position, T t);

    public XBaseAdapter(@LayoutRes int layoutRes, List<T> datas) {
        this.layoutRes = layoutRes;
        this.datas = datas;
    }

    @Override
    public BindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
    }

    @Override
    public void onBindViewHolder(final BindViewHolder holder, final int position) {
        onBind(holder, position, datas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterItemClickListener.onItemClick(holder, position, datas.get(position), dialog);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setTDialog(XDialog tDialog) {
        this.dialog = tDialog;
    }

    public interface OnAdapterItemClickListener<T> {
        void onItemClick(BindViewHolder holder, int position, T t, XDialog tDialog);
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        this.adapterItemClickListener = listener;
    }

}

```
##### 如果使用者需要使用自己的列表布局时,可以使用setListLayoutRes(layotuRes,LayoutManager)方法设置xml布局和布局管理器LayoutManager,切记xml布局中的RecyclerView的id必须设置为recycler_view(如效果图中的分享弹窗)
```java
//底部分享
public void shareDialog(View view) {
    new XListDialog.Builder(getSupportFragmentManager())
            .setListLayoutRes(R.layout.dialog_share_recycler, LinearLayoutManager.HORIZONTAL)
            .setScreenWidthAspect(this, 1.0f)
            .setGravity(Gravity.BOTTOM)
            .setAdapter(new TBaseAdapter<String>(R.layout.item_share, Arrays.asList(sharePlatform)) {
                @Override
                protected void onBind(BindViewHolder holder, int position, String s) {
                    holder.setText(R.id.tv, s);
                }
            })
            .setOnAdapterItemClickListener(new TBaseAdapter.OnAdapterItemClickListener<String>() {
                @Override
                public void onItemClick(BindViewHolder holder, int position, String item, TDialog tDialog) {
                    Toast.makeText(DiffentDialogActivity.this, item, Toast.LENGTH_SHORT).show();
                    tDialog.dismiss();
                }
            })
            .create()
            .show();
}
```
自定义列表布局
```java
<?xml version="1.0" encoding="utf-8"?>
<<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#242424">

    <TextView
        android:id="@+id/textView3"
        android:layout_width="0dp"
        android:layout_height="50dp"
        android:gravity="center"
        android:text="分享到"
        android:textColor="@color/white"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/textView3" />

</<androidx.constraintlayout.widget.ConstraintLayout>
```
#### 框架原理解析
XDialog的实现原理主要分为三步
1. 实例化XDialog.Builer对象builder,然后调用各种setXXX()方法设置数据,设置的数据都保存在XController.XParams实例中
2. create()方法调用后才会实例化XDialog对象,并将XController.TParams中设置的数据传递到XDialog的属性XController对象中
3. show()方法调用显示弹窗

### 博客地址  
https://blog.csdn.net/a214024475/article/details/100926426
