package rippleview.huang.com;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 23763 on 2017/2/27.
 */

public class RippleView extends FrameLayout {
    private static final String TAG = "RipplePlayer";

    private static final int DEFAULT_COLOR = 0xffffffff;
    private static final int DEFAULT_AMOUNT = 3;
    private static final int DEFAULT_DURATION = 3000;
    private static final float DEFAULT_SCALE = 4.0f;
    private static final int DEFAULT_SIZE = 240;
    private static final float DEFAULT_STROKE_WIDTH = 2.0f;

    private int mRippleColor;
    private float mRippleStrokeWidth;//记录初始设置的
    private float mTempStrokeWidth;//实际使用的
    private int mRippleDiameter;//子view宽高
    private int mCurrentSize;//父view大小
    private int mRippleDuration;
    private int mRippleAmount;
    private int mRippleDelay;
    private float mRippleScale;
    private RippleStyle mRippleStyle;

    private Paint mPaint;

    private AnimatorSet mAnimatorSet;
    private List<Animator> mAnimatorList;
    private List<Ripple> mRippleViewList = new ArrayList<>();

    private OnGetViewSizeListener onGetViewSizeListener;

    /**
     * 动画是否运行中
     */
    private boolean isRippleRunning;

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        /**
         * 在自定义控件的构造函数或其他绘制相关地方使用系统依赖的代码，
         * 会导致可视化编辑器无法报错并提示：
         * Use View.isInEditMode() in your custom views to skip code when shownin Eclipse
         */
        if (isInEditMode()) {
            return;
        }
        if (attrs == null) {
            throw new IllegalArgumentException("Attributes should be provided to this view");
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        mRippleColor = ta.getColor(R.styleable.RippleView_ripple_color, DEFAULT_COLOR);
        mRippleStrokeWidth = ta.getFloat(R.styleable.RippleView_ripple_strokeWidth, DEFAULT_STROKE_WIDTH);
        mRippleDuration = ta.getInt(R.styleable.RippleView_ripple_duration, DEFAULT_DURATION);
        mRippleAmount = ta.getInt(R.styleable.RippleView_ripple_amount, DEFAULT_AMOUNT);
        mRippleScale = ta.getFloat(R.styleable.RippleView_ripple_scale, DEFAULT_SCALE);

        int style = ta.getInt(R.styleable.RippleView_ripple_style, RippleStyle.FILL.ordinal());
        mRippleStyle = style == RippleStyle.FILL.ordinal() ? RippleStyle.FILL : RippleStyle.STROKE;
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getProperSize(DEFAULT_SIZE, widthMeasureSpec);
        int height = getProperSize(DEFAULT_SIZE, heightMeasureSpec);
        setDimension(width > height ? height : width);
        initPaint();
    }

    /**
     * 计算子View的宽高，并设置
     *
     * @param size
     */
    private void setDimension(int size) {
        mCurrentSize = size;
        mRippleDiameter = (int) (mCurrentSize / mRippleScale);
        setMeasuredDimension(mCurrentSize, mCurrentSize);
        if (onGetViewSizeListener != null) {
            onGetViewSizeListener.onGetSize(mCurrentSize);
        }
    }

    /**
     * 初始画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(mRippleColor);

        setStrokeStyle();
    }

    /**
     * 设置圆形样式
     */
    private void setStrokeStyle() {
        switch (mRippleStyle) {
            case FILL:
                mTempStrokeWidth = 0;
                mPaint.setStyle(Paint.Style.FILL);
                break;
            case STROKE:
                mTempStrokeWidth = mRippleStrokeWidth;
                mPaint.setStrokeWidth(mTempStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    /**
     * 创建波纹
     *
     * @param amount
     */
    private void createRipples(int amount) {
        LayoutParams layoutParams = new LayoutParams(mRippleDiameter, mRippleDiameter);
        layoutParams.gravity = Gravity.CENTER;

        for (int i = 0; i < amount; i++) {
            Ripple ripple = new Ripple(getContext());

            mAnimatorList.add(createAnimator(ripple, "ScaleX", 1.0f, mRippleScale, i));
            mAnimatorList.add(createAnimator(ripple, "ScaleY", 1.0f, mRippleScale, i));
            mAnimatorList.add(createAnimator(ripple, "Alpha", 2.0f, 0f, i));

            this.addView(ripple, layoutParams);
            mRippleViewList.add(ripple);
        }
    }

    private ObjectAnimator createAnimator(View view, String type, float from, float to, int count) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, type, from, to);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setStartDelay(count * mRippleDelay);
        animator.setDuration(mRippleDuration);
        return animator;
    }

    /**
     * 绑定动画
     */
    private void setAnim() {
        if (mAnimatorSet == null) {
            //Animator 若不重新创建，动画运行会携带旧动画
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.setInterpolator(new LinearInterpolator());
        }
        mAnimatorSet.playTogether(mAnimatorList);
    }

    /**
     * 获取尺寸
     *
     * @param defaultSize
     * @param measureSize
     * @return
     */
    private int getProperSize(int defaultSize, int measureSize) {
        int size = MeasureSpec.getSize(measureSize);
        switch (MeasureSpec.getMode(measureSize)) {
            case MeasureSpec.UNSPECIFIED:
                size = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                size = size / 2;
                break;
        }
        return size;
    }

    /**
     * 开始动画
     */
    private void startAnim() {
        for (Ripple rippleView : mRippleViewList) {
            if (rippleView.getVisibility() != View.VISIBLE) {
                rippleView.setVisibility(View.VISIBLE);
            }
        }
        mAnimatorSet.start();
        isRippleRunning = true;
    }

    /**
     * 动画播放开关
     */
    public void toggle() {
        if (isRippleRunning) {
            mAnimatorSet.end();
            isRippleRunning = false;
        } else {
            if (mAnimatorSet == null) {
                firstStart();
            } else
                startAnim();
        }
    }

    /**
     * 第一次开启动画
     */
    public void firstStart() {
        mAnimatorList = new ArrayList<>();
        setRippleAmount(mRippleAmount);
    }

    /**
     * 获取当前动画是否在运行
     *
     * @return
     */
    public boolean isRippleRunning() {
        return isRippleRunning;
    }

    /**
     * 设置水波纹颜色
     *
     * @param rippleColor
     */
    public void setRippleColor(int rippleColor) {
        mRippleColor = rippleColor;
        mPaint.setColor(ContextCompat.getColor(getContext(), mRippleColor));
        invalidateRipple();
    }

    /**
     * 设置圆形样式
     *
     * @param style
     */
    public void setRippleStyle(RippleStyle style) {
        mRippleStyle = style;
        setStrokeStyle();
        invalidateRipple();
    }

    public RippleStyle getRippleStyle() {
        return mRippleStyle;
    }

    /**
     * 设置波纹数量
     *
     * @param amount
     */
    public void setRippleAmount(int amount) {
        mRippleAmount = amount;
        setRippleDelay();
        addRippleView();
    }

    /**
     * 设置缩放比
     *
     * @param scale
     */
    public void setRippleScale(float scale) {
        mRippleScale = scale;
        mRippleDiameter = (int) (mCurrentSize / mRippleScale);
        addRippleView();
    }

    /**
     * 设置动画时间
     *
     * @param duration
     */
    public void setRippleDuration(int duration) {
        mRippleDuration = duration;
        setRippleDelay();
        addRippleView();
    }

    /**
     * 设置边框线条粗细，只在Style为STROKE下起作用
     *
     * @param strokeWidth
     */
    public void setRippleStrokeWidth(float strokeWidth){
        mRippleStrokeWidth = strokeWidth;
        mTempStrokeWidth = mRippleStrokeWidth;
        mPaint.setStrokeWidth(mTempStrokeWidth);
        addRippleView();
    }

    /**
     * 设置波纹间隔时间
     */
    private void setRippleDelay() {
        mRippleDelay = mRippleDuration / mRippleAmount;
    }

    /**
     * 返回当前view size
     *
     * @return
     */
    public int getViewSize() {
        return mCurrentSize;
    }

    public void rippleReset(){
        setRippleDelay();
        addRippleView();
    }

    public void addRippleView() {
        if (mAnimatorList == null) {
            return;
        }
        clearRipples();
        createRipples(mRippleAmount);
        setAnim();
        startAnim();

        invalidateRipple();
    }

    private void clearRipples() {
        if (mAnimatorList.size() > 0) {
            mAnimatorList.clear();
        }
        if (mRippleViewList.size() > 0) {
            mRippleViewList.clear();
        }

        this.removeAllViews();
        mAnimatorSet = null;
    }

    /**
     * 刷新水波纹
     */
    public void invalidateRipple() {
        for (Ripple ripple : mRippleViewList) {
            ripple.invalidate();
        }
    }

    class Ripple extends View {
        public Ripple(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int radius = getWidth() / 2;
            canvas.drawCircle(radius, radius, radius - mTempStrokeWidth, mPaint);
        }
    }

    public interface OnGetViewSizeListener {
        void onGetSize(int size);
    }

    public void setOnGetViewSizeListener(OnGetViewSizeListener onGetViewSizeListener) {
        this.onGetViewSizeListener = onGetViewSizeListener;
    }

    public OnGetViewSizeListener getOnGetViewSizeListener() {
        return onGetViewSizeListener;
    }
}
