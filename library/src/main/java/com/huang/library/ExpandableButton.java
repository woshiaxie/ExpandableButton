package com.huang.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.huang.library.util.CommonUtils.createRoundCornorDrawable;


/**
 * Created by roy on 2017/7/31.
 */

public class ExpandableButton extends LinearLayout {
    private static final String TAG = "MyLog";

    private ExpandableButton sfb;
    private WindowManager wm;
    private int windowWidth;

    private Circle mLeftCircle;
    private Circle mRightCircle;

    private int contentBackgroundColor;     //控件背景色

    private int contentTextColor;    //文本颜色
    private int contentTextSize;   //文本大小
    private String cotentText = ""; //文本

    private int iconTextColor;    //文本颜色
    private float iconTextSize;   //文本大小
    private String iconText = ""; //文本
    private int iconBackgroundColor = Color.WHITE;

    private int step = 80;  //拉伸步长
    private int degrees = 0;    //旋转度数
    private float stepRatio = 0.16f;   //旋转动画的步长比例，覆盖step值。

    private int spaceBetweenContentAndIcon = 0;

    private final static int IS_SLIDE_DECREASE = 0;  //递减状态
    private static final int IS_SLIDE_INCREASE = 1;  //递增状态

    private boolean isFolded = false;
    private boolean leftStart = false; //图标是否在左侧绘制

    private int width;
    private int maxWidth;
    private int height;
    private int radius;


    private int borderCornerRadius = -1;       //边框角度，这个角度同样会影响到iconView的圆角
    private int borderWidth;
    private int borderColor;

    private int gapBetweenCircles;

    private View contentView;                 //textview
    private View iconView;

    private int textWidth;              //文本宽度
    private int textHeight;             //文本高度

//    private boolean isIconRotatable = false; //是否支持icon旋转动画；目前这个旋转动画是有问题的
    private int iconWidth;  //icon宽度
    private int iconHeight; //icon高度

    private int rotateDegrees;        //旋转差值

    private boolean initWithFolded = false; //是否以折叠态进行初始化展现
    private boolean inited = false;         //是否初始化了

    private Paint paint;


    private FoldListener foldListener;        //折叠监听
    private OnClickListener onClickListener;  //点击监听
    private boolean canClick;

    private Handler mHandler = initHandler();

    public ExpandableButton(Context context) {
        this(context, null);
    }

    public ExpandableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initSelf();

        initFields(context, attrs);

        initData();

        initText(context);
        initIconView(context);
        initIconViewRotate();
    }
    private void initSelf() {
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setPadding(0,0,0,0);
        if (leftStart) {
            this.setGravity(Gravity.LEFT);
        } else {
            this.setGravity(Gravity.RIGHT);
        }
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = wm.getDefaultDisplay().getWidth();
        sfb = this;
    }

    private void initData() {
        mLeftCircle = new Circle();
        mRightCircle = new Circle();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
    }

    private void initFields(Context context, AttributeSet attrs) {
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ExpandableButton);

        /* content部分 */
        cotentText = type.getString(R.styleable.ExpandableButton_contentText);
        contentTextColor = type.getColor(R.styleable.ExpandableButton_contentTextColor, Color.BLACK);
        contentTextSize = type.getDimensionPixelSize(R.styleable.ExpandableButton_contentTextSize, 20);
        if (contentTextSize <= 0 ){
            contentTextSize = 12;
        }
        contentBackgroundColor = type.getColor(R.styleable.ExpandableButton_contentBackgroundColor, Color.WHITE);

        /* icon部分 */
        iconText = type.getString(R.styleable.ExpandableButton_iconText);
        iconTextColor = type.getColor(R.styleable.ExpandableButton_iconTextColor, Color.BLACK);
        iconTextSize = type.getDimensionPixelSize(R.styleable.ExpandableButton_iconTextSize, 20);
        if (iconTextSize <= 0 ){
            iconTextSize = 12;
        }
        iconBackgroundColor = type.getColor(R.styleable.ExpandableButton_iconBackgroundColor, Color.WHITE);

        /* icon旋转部分 */
//        isIconRotatable = type.getBoolean(R.styleable.FoldableButton_iconRotatable, false);
        degrees = type.getInteger(R.styleable.ExpandableButton_iconRotateDegree, 90);
        stepRatio = type.getFloat(R.styleable.ExpandableButton_stepRatio, 0.16f);
        if (stepRatio < 0 || stepRatio > 1.0f) {
            stepRatio = 0.16f;
        }

        /* 整体设置部分 */
        maxWidth = type.getDimensionPixelSize(R.styleable.ExpandableButton_maxWidth, windowWidth);

        initWithFolded = type.getBoolean(R.styleable.ExpandableButton_initFolded, false);
        isFolded = initWithFolded;

        spaceBetweenContentAndIcon = (int)type.getDimension(R.styleable.ExpandableButton_contentIconSpace, 10);

        leftStart = type.getBoolean(R.styleable.ExpandableButton_iconLeft, false);

        /* 边框部分 */
        borderColor = type.getColor(R.styleable.ExpandableButton_borderColor, Color.parseColor("#CCCCCC"));
        borderWidth = type.getDimensionPixelSize(R.styleable.ExpandableButton_borderWidth, 0);
        borderCornerRadius = type.getDimensionPixelSize(R.styleable.ExpandableButton_borderCorner, -1);

        type.recycle();
    }

    private void initText(Context context){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView tv = new TextView(context);
        tv.setLayoutParams(params);
        tv.setPadding(0,0,0,0);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setLines(1);
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        tv.setText(cotentText);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
        tv.setTextColor(contentTextColor);
        tv.setTag("content");
        addView(tv,params);
        requestLayout();
    }

    private void initIconView(Context context){
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        LinearLayout rootView = new LinearLayout(getContext());
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setGravity(Gravity.CENTER);
        rootView.setPadding(0,0,0,0);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView iconFont = new TextView(context);
        iconFont.setLayoutParams(params);
        iconFont.setPadding(0,0,0,0);
        iconFont.setEllipsize(TextUtils.TruncateAt.END);
        iconFont.setLines(1);
        iconFont.setGravity(Gravity.CENTER);
        iconFont.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        iconFont.setText(iconText);
        iconFont.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconTextSize);
        iconFont.setTextColor(iconTextColor);
        rootView.addView(iconFont, params);


        rootView.setTag("icon");
        iconView = rootView;
        addView(iconView,rootParams);

        requestLayout();
    }

    private void initIconViewRotate() {
        if (iconView == null) {
            return;
        }
        if (initWithFolded) {
            iconView.setRotation(0);
        } else {
            iconView.setRotation(degrees);
        }
    }

    private Handler initHandler() {
        Handler handler = new FoldableButtonHandler();
        return handler;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        if (width == 0) {
            //获取各个子控件
            for (int i = 0; i < this.getChildCount(); i++){
                View tempView = getChildAt(i);
                if (tempView != null && tempView.getTag() != null) {
                    if ("content".equals(tempView.getTag())) {
                        contentView = tempView;
                    } else if ("icon".equals(tempView.getTag())) {
                        iconView = tempView;
                    }
                }
            }
            if (contentView == null || iconView == null) {
                setMeasuredDimension((int)width,(int)height);
                super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            }

            measureChild(contentView, widthMeasureSpec, heightMeasureSpec);
            textWidth = contentView.getMeasuredWidth();
            textHeight = contentView.getMeasuredHeight();

            measureChild(iconView, widthMeasureSpec, heightMeasureSpec);
            iconWidth = iconView.getMeasuredWidth();
            iconHeight = iconView.getMeasuredHeight();

            //根据模式设置宽高
            adjustWithAndHeight(widthMeasureSpec,heightMeasureSpec);
            resetContentLayoutParams();
            resetIconViewLayoutParams();

            //初始化旋转角度和旋转角度比
            rotateDegrees = degrees;

            //矩形右边x坐标
            if (!inited) {
                initCirclePositions();
                inited = true;
            }
            gapBetweenCircles = Math.abs(mRightCircle.x - mLeftCircle.x);

            //初始化收缩、伸展的步长
            step = (int)((width - iconWidth) * stepRatio);
        }
        setMeasuredDimension(width,height);

    }

    private void adjustWithAndHeight(int widthMeasureSpec, int heightMeasureSpec) {
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);

        if (hMode == MeasureSpec.AT_MOST) {
            int defaultHeight = (int) Math.ceil(2 * (radius + borderWidth));
            int childMaxHeight = (int) Math.ceil(Math.max((int)textHeight, (int) Math.ceil(iconHeight)));
            height = Math.max(defaultHeight, childMaxHeight) + 2 * borderWidth;
        } else {
            height = MeasureSpec.getSize(heightMeasureSpec);
            if (height< 2 * (radius + borderWidth)){
                height = 2 * (radius + borderWidth);
            }
        }
        //获取圆的半径
        radius = (int)(height/ 2 - borderWidth);
        //获取整组件的圆角
        if (borderCornerRadius == -1) {
            borderCornerRadius = radius;
        }
        //图片宽度
        iconWidth = 2 * (radius- borderWidth);
        iconHeight = 2 * (radius - borderWidth);



        if (wMode == MeasureSpec.AT_MOST || wMode == MeasureSpec.UNSPECIFIED) {
            //文本宽度 + 左(右)Padding一个半径 + iconView的宽度 + 组件间距 + 两边都有的边框宽度
            int totalWidth = textWidth + radius + iconWidth + spaceBetweenContentAndIcon + 2 * borderWidth;
            if (maxWidth > 0 && totalWidth < maxWidth){
                width = totalWidth;
            } else {
                width = maxWidth;
            }
        } else {
            width = MeasureSpec.getSize(widthMeasureSpec);
            if ( width > maxWidth) {
                width = maxWidth;
            }
        }

    }
    private void initCirclePositions() {
        if (!initWithFolded) {  //展开式
            mLeftCircle.x = radius + borderWidth;
            mLeftCircle.y = radius + borderWidth;
            mLeftCircle.radius = radius;

            mRightCircle.x = width - radius - borderWidth;
            mRightCircle.y =radius  + borderWidth;
            mRightCircle.radius = radius;

        } else {                //折叠式
            if (leftStart) {
                mLeftCircle.x = radius + borderWidth;
                mLeftCircle.y = radius + borderWidth;
                mLeftCircle.radius = radius;

                mRightCircle.x = radius + borderWidth;
                mRightCircle.y = radius + borderWidth;
                mRightCircle.radius = radius;
            } else {
                mLeftCircle.x = width - radius - borderWidth;
                mLeftCircle.y = radius + borderWidth;
                mLeftCircle.radius = radius;

                mRightCircle.x = width - radius - borderWidth;
                mRightCircle.y = radius + borderWidth;
                mRightCircle.radius = radius;
            }
        }
    }

    private void resetContentLayoutParams() {
        ViewGroup.LayoutParams vgParams = contentView.getLayoutParams();
        LinearLayout.LayoutParams params = null;
        if (vgParams != null && vgParams instanceof LinearLayout.LayoutParams) {
            params = (LinearLayout.LayoutParams)vgParams;
        } else {
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }

        params.width = width - borderCornerRadius - 2 * radius - spaceBetweenContentAndIcon - 2 * borderWidth;
        contentView.setLayoutParams(params);
    }

    private void resetIconViewLayoutParams() {
        ViewGroup.LayoutParams vgParams = iconView.getLayoutParams();
        LinearLayout.LayoutParams params = null;
        if (vgParams != null && vgParams instanceof LinearLayout.LayoutParams) {
            params = (LinearLayout.LayoutParams)vgParams;
        } else {
            params = new LinearLayout.LayoutParams((int)(iconWidth), (int)(iconHeight));
        }

        params.width = (height - 4 * borderWidth) / 2;
        params.height = (height - 4 * borderWidth) / 2;
        //Color.parseColor("#4CAF50")
        iconView.setBackground(createRoundCornorDrawable(borderCornerRadius, iconBackgroundColor));
        iconView.setLayoutParams(params);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        paint.setColor(contentBackgroundColor);
        //画背景矩形
        drawBackgroundRect(canvas);
        super.dispatchDraw(canvas);
    }

    private void drawBackgroundRect(Canvas canvas) {
        RectF rectOutter;
        RectF rectInner;
        //绘制外层边框
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setColor(borderColor);
        rectOutter = new RectF(
                mLeftCircle.x - radius,
                borderWidth,
                mRightCircle.x + radius,
                height-borderWidth
        );
        canvas.drawRoundRect(rectOutter, borderCornerRadius, borderCornerRadius, paint);
        //绘制里层背景色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(contentBackgroundColor);
        rectInner = new RectF(
                mLeftCircle.x - radius ,
                 borderWidth,
                mRightCircle.x + radius,
                height - borderWidth
        );

        canvas.drawRoundRect(rectInner,borderCornerRadius, borderCornerRadius, paint);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (leftStart) {
            contentView.layout(
                    mLeftCircle.x + radius + spaceBetweenContentAndIcon,
                    radius - textHeight / 2,
                    mRightCircle.x + radius - borderWidth - borderCornerRadius,
                    radius + textHeight / 2
            );
            iconView.layout(
                    mLeftCircle.x - radius,
                    mLeftCircle.y - radius,
                    mLeftCircle.x + radius,
                    mLeftCircle.y + radius
            );
        } else {
            contentView.layout(
                    mLeftCircle.x - radius + borderCornerRadius +  borderWidth,
                    radius - textHeight / 2,
                    mRightCircle.x - spaceBetweenContentAndIcon,
                    radius + textHeight / 2
            );
            iconView.layout(
                    mRightCircle.x - radius,
                    borderWidth,
                    mRightCircle.x + radius,
                    mRightCircle.y + radius
            );
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canClick = judgeClickable(event.getX(), event.getY());
                if (!canClick)
                    return super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                if (canClick && onClickListener != null && isEnabled()) {
                    onClickListener.onClick(sfb);
                }
                break;
        }
        return true;
    }

    private boolean judgeClickable(float x, float y) {
        boolean canClick;
        if (!isFolded) {   //伸展状态
            if (x < width && y < height) {
                canClick = true;
            }else {
                canClick = false;
            }
        }else {
            if (leftStart) {
                if (x < radius * 2 && y < radius * 2) {  //在圆内
                    canClick = true;
                }else {
                    canClick = false;
                }
            } else {
                if (width - radius * 2 < x && x < width && y < radius * 2) {  //在圆内
                    canClick = true;
                }else {
                    canClick = false;
                }
            }

        }

        return canClick;
    }


    //递减状态
    public void switchToFold() {
        setEnabled(false);  //滑动时不给点击事件
        isFolded = false; //记录递增还是递减状态
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_DECREASE, 40);
    }

    //递增状态
    public void switchToUnFold() {
        setEnabled(false);  //滑动时不给点击事件
        isFolded = true;  //记录递增还是递减状态
        mHandler.sendEmptyMessageDelayed(IS_SLIDE_INCREASE, 40);
    }

    //外部调用
    public void switchFoldStatus() {
        if (!isFolded) {    //判断是否是递增状态
            switchToFold();
        } else {
            switchToUnFold();
        }
    }
    public boolean isFolded() {
        return isFolded;
    }

    public void setFoldListener(FoldListener foldListener) {
        this.foldListener = foldListener;
    }

    public interface FoldListener {
        void onFold(boolean isFolded, ExpandableButton sfb);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    /**
     *
     */
    public interface OnClickListener {
        void onClick(ExpandableButton sfb);
    }

    private void calculateFieldsWhenDecrease() {
        isFolded = true;
        gapBetweenCircles = Math.abs(mRightCircle.x - mLeftCircle.x);
        if (gapBetweenCircles > step + radius +  borderWidth) {
            if (leftStart) {
                mRightCircle.x -= step;
            } else {
                mLeftCircle.x += step;
            }

            //旋转icon
            if (iconView != null) {
                rotateDegrees = (int)(degrees * ((double)gapBetweenCircles/ (double)(width - 2 * radius)));
                iconView.setRotation(rotateDegrees);
            }
            calculateContentToShow(false , true);

            mHandler.sendEmptyMessageDelayed(IS_SLIDE_DECREASE, 16);
        } else {
            calculateContentToShow(true, true);
            //动画结束 恢复默认状态
            if (leftStart) {
                mLeftCircle.x = radius + borderWidth;
                mRightCircle.x = radius + borderWidth;
            } else {
                mRightCircle.x = width - radius - borderWidth;
                mLeftCircle.x = mRightCircle.x ;
            }
            gapBetweenCircles = 0;

            //旋转icon
            if (iconView != null) {
                rotateDegrees = 0;
                iconView.setRotation(rotateDegrees);
            }

            setEnabled(true);
            isFolded = true;
            //折叠回调
            if (foldListener != null) {
                foldListener.onFold(isFolded, sfb);
            }
        }
    }

    private void calculateFieldsWhenIncrease() {
        isFolded = false;
        gapBetweenCircles = Math.abs(mRightCircle.x - mLeftCircle.x);
        if (gapBetweenCircles <= width - 3 * radius) {
            //修改两个圆的位置
            if (leftStart) {
                mRightCircle.x += step;
            } else {
                mLeftCircle.x -= step;
            }


            calculateContentToShow(false, false);
            //iconView旋转控制
            if(iconView != null ){
                rotateDegrees = (int)(degrees * ((double)gapBetweenCircles/ (double)(width - 2 * radius)));
                iconView.setRotation(rotateDegrees);
            }
            mHandler.sendEmptyMessageDelayed(IS_SLIDE_INCREASE, 16);
        } else {
            //动画结束 恢复默认状态
            if (leftStart) {
                mRightCircle.x = width - radius - borderWidth;
            } else {
                mLeftCircle.x = radius + borderWidth;
            }
            calculateContentToShow(true, false);

            gapBetweenCircles = width - radius;
            //动画结束 恢复默认状态
            rotateDegrees = degrees;
            if (iconView != null) {
                iconView.setRotation(rotateDegrees);
            }


            setEnabled(true);
            isFolded = false;
            //折叠回调
            if (foldListener != null) {
                foldListener.onFold(isFolded, sfb);
            }
        }
    }


    /**
     * 根据宽度决定显示的内容，比如裁剪文字，拼接符号等等
     * @param hasReached
     * @param folded
     */
    private void calculateContentToShow(boolean hasReached, boolean folded) {

    }

    private class FoldableButtonHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_SLIDE_DECREASE: //递减状态, degrees --> 0
                    calculateFieldsWhenDecrease();
                    break;
                case IS_SLIDE_INCREASE: //递增状态, 0 --> degrees
                    calculateFieldsWhenIncrease();
                    break;
            }
            resetContentLayoutParams();
            invalidate();
        }
    }
}
