### 前言
这个库包含两种类型的滚轮：普通滚轮和立体滚轮，普通滚轮调用WheelView，立体滚轮调用Wheel3DView。两种滚轮实现原理相同，但显示效果不同。立体滚轮类似IOS时间选择控件，效果如下。

### API方法介绍

|	API方法	|	API方法说明	|
|	---		|	---		|
|	setTextSize	|	设置文字大小	|
|	setMiddleMaskColor	|	设置中间蒙版颜色	|
|	setDividerColor	|	设置中间边框颜色	|
|	setNoTextColor	|	设置没有选中时的文字颜色	|
|	setWhenSelectedTextColor	|	设置选中时的文字颜色	|

``` java
xml配置
        <attr name="wheelCyclic" format="boolean"/>
        <attr name="wheelEntries" format="reference"/>
        <attr name="wheelItemCount" format="integer"/>
        <attr name="wheelItemWidth" format="dimension"/>
        <attr name="wheelItemHeight" format="dimension"/>
        <attr name="wheelTextSize" format="dimension"/>
        <attr name="wheelTextColor" format="color"/>
        <attr name="wheelSelectedTextColor" format="color"/>
        <attr name="wheelDividerColor" format="color"/>
        <attr name="wheelHighlightColor" format="color"/>
        <attr name="wheelMiddleLayerColor" format="color"/>

```

### 使用方法
在xml文件中添加
``` java
      <com.cl.xdialog.choose.Wheel3DView
            android:id="@+id/wheel_list"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:wheelCyclic="true"
            app:wheelDividerColor="@color/x_wheel_selected_text_color"
            app:wheelEntries="@array/default_arra"
            app:wheelHighlightColor="@color/topic_trans"
            app:wheelItemCount="9"
            app:wheelItemHeight="40dp"
            app:wheelItemWidth="160dp"
            app:wheelSelectedTextColor="@color/T_00"
            app:wheelTextColor="@color/topic"
            app:wheelTextSize="17dp" />
```
回调监听
``` java
WheelView wheelView = (WheelView) findViewById(R.id.wheel);
wheelView.setOnWheelChangedListener(new OnWheelChangedListener() {
    @Override
    public void onChanged(WheelView view, int oldIndex, int newIndex) {
        CharSequence text = view.getItem(newIndex);
        Log.i("WheelView", String.format("index: %d, text: %s", newIndex, text));
    }
});
```