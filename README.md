# ExpandableButton
一个可展开可搜索的按钮。灵感和思路发源于这个组件https://github.com/CTSN/FlodAbleButton
区别包含但不限于：
- 修复Bug；
- 增加边框相关的属性配置，支持borderColor（边框颜色）、borderWidth（边框宽度）、borderCornor（边框圆角）三个属性；
换句话说，组件的圆角弧度也可以自定义了；
- 增加“从右向左”的布局方式；
- 移除原有handler抛消息控制动画的机制，也就顺道移除了step等属性。引入ValueAnimator，支持配置duration属性。这样读起来顺眼多了；
- 移除强制要求closeIcon和foldIcon，增加一个iconView，想要什么自己可以玩儿起来了；

## 效果
![效果](https://github.com/woshiaxie/ExpandableButton/blob/dev/app/imgs/demo.gif)

## 下一步计划
目前iconView和contentView都是固定的TextView。所以需要拆分，一个Layout，一个继承自Layout的View，后者才应该是可用的ExpandableButton。
1. 抽离iconView的赋值过程，使用调用者可以直接传递一个view作为iconView；
2. 抽离contentView的赋值过程，使用调用者可以直接传递一个view作为iconView；
