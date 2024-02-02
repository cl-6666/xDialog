```

                                                                          
               ,---,                          ,--,                        
             .'  .' `\    ,--,              ,--.'|                        
           ,---.'     \ ,--.'|              |  | :     ,---.              
 ,--,  ,--,|   |  .`\  ||  |,               :  : '    '   ,'\   ,----._,. 
 |'. \/ .`|:   : |  '  |`--'_      ,--.--.  |  ' |   /   /   | /   /  ' / 
 '  \/  / ;|   ' '  ;  :,' ,'|    /       \ '  | |  .   ; ,. :|   :     | 
  \  \.' / '   | ;  .  |'  | |   .--.  .-. ||  | :  '   | |: :|   | .\  . 
   \  ;  ; |   | :  |  '|  | :    \__\/: . .'  : |__'   | .; :.   ; ';  | 
  / \  \  \'   : | /  ; '  : |__  ," .--.; ||  | '.'|   :    |'   .   . | 
./__;   ;  \   | '` ,/  |  | '.'|/  /  ,.  |;  :    ;\   \  /  `---`-'| | 
|   :/\  \ ;   :  .'    ;  :    ;  :   .'   \  ,   /  `----'   .'__/\_: | 
`---'  `--`|   ,.'      |  ,   /|  ,     .-./---`-'            |   :    : 
           '---'         ---`-'  `--`---'                       \   \  /  
                                                                 `--`-'   
```


#### 前言
>我看到很多封装Dialog的，但是我觉得都有缺点，所以我也就取其中一个封装通用的弹窗Dialog出来，支持AndroidX，平常开发当中自用，会经常维护。
>建议以依赖方式使用，最后希望大家能给出宝贵的意见。

#### 效果演示  
<img src="https://github.com/cl-6666/xDialog/blob/master/img/jies.gif" alt="演示"/><img src="https://github.com/cl-6666/xDialog/blob/master/img/xzkj.gif" alt="演示"/>  

版本更新历史:  
[![](https://jitpack.io/v/cl-6666/xDialog.svg)](https://jitpack.io/#cl-6666/xDialog)  

- v3.1.3：(2024年01月25日)
  - 解决使用XListDialog的时候，无法刷新适配器问题
  - 代码优化
    
- v3.1.0：(2023年05月07日)
  - 内部使用kotlin代码优化  
  - 代码优化

#### 使用依赖
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
 implementation 'com.github.cl-6666:xDialog:v3.1.6'
v7请使用
implementation 'com.github.cl-6666:xDialog:v1.0.2'
```  
#### 如何使用  
- [弹窗使用说明](https://github.com/cl-6666/xDialog/blob/master/README_Dialog.md)
- [选择滚动控件使用说明](https://github.com/cl-6666/xDialog/blob/master/README_Choose.md)  

### 博客地址  
https://blog.csdn.net/a214024475/article/details/100926426

#### QQ 群：458173716  
<img src="https://github.com/cl-6666/serialPort/blob/master/qq2.jpg" width="350" height="560" alt="演示"/>  
