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

```xml
xml配置属性介绍
  <declare-styleable name="X_WheelView">
    <!-- 是否启用循环滚动，true 为循环，false 为不循环 -->
    <attr name="wheelCyclic" format="boolean"/>
    
    <!-- 滚轮的内容数据来源，可以是一个字符串数组或资源引用 -->
    <attr name="wheelEntries" format="reference"/>
    
    <!-- 滚轮同时可见的项目数量 -->
    <attr name="wheelItemCount" format="integer"/>
    
    <!-- 每个滚轮项的宽度 -->
    <attr name="wheelItemWidth" format="dimension"/>
    
    <!-- 每个滚轮项的高度 -->
    <attr name="wheelItemHeight" format="dimension"/>
    
    <!-- 滚轮项文字的字体大小 -->
    <attr name="wheelTextSize" format="dimension"/>
    
    <!-- 滚轮项文字的默认颜色 -->
    <attr name="wheelTextColor" format="color"/>
    
    <!-- 当前选中项文字的颜色 -->
    <attr name="wheelSelectedTextColor" format="color"/>
    
    <!-- 滚轮分割线的颜色 -->
    <attr name="wheelDividerColor" format="color"/>
    
    <!-- 选中区域的高亮背景色 -->
    <attr name="wheelHighlightColor" format="color"/>
    
    <!-- 滚轮中间层的颜色，通常用于遮罩或分层效果 -->
    <attr name="wheelMiddleLayerColor" format="color"/>
</declare-styleable>

<declare-styleable name="X_Wheel3DView">
    <!-- 滚轮的3D视角方向 -->
    <attr name="wheelToward" format="enum">
        <!-- 不倾斜，保持垂直视图 -->
        <enum name="none" value="0"/>
        <!-- 向左倾斜 -->
        <enum name="left" value="-1"/>
        <!-- 向右倾斜 -->
        <enum name="right" value="1"/>
    </attr>
</declare-styleable>

```

### 使用方法
在xml文件中添加
```xml
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
```java
WheelView wheelView = (WheelView) findViewById(R.id.wheel);
wheelView.setOnWheelChangedListener(new OnWheelChangedListener() {
    @Override
    public void onChanged(WheelView view, int oldIndex, int newIndex) {
        CharSequence text = view.getItem(newIndex);
        Log.i("WheelView", String.format("index: %d, text: %s", newIndex, text));
    }
});
```
 
