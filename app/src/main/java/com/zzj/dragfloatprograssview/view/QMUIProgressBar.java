package com.zzj.dragfloatprograssview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.blankj.utilcode.util.ConvertUtils;
import com.zzj.dragfloatprograssview.R;


/**
 * 一个进度条控件，通过颜色变化显示进度，支持环形和矩形两种形式，主要特性如下：
 * <ol>
 * <li>支持在进度条中以文字形式显示进度，支持修改文字的颜色和大小。</li>
 * <li>可以通过 xml 属性修改进度背景色，当前进度颜色，进度条尺寸。</li>
 * <li>支持限制进度的最大值。</li>
 * </ol>
 *
 * @author cginechen
 * @date 2015-07-29
 */
public class QMUIProgressBar extends View {

    public static int TYPE_RECT = 0;
    public static int TYPE_CIRCLE = 1;
    public static int TOTAL_DURATION = 5000;
    public static int DEFAULT_PROGRESS_COLOR = Color.BLUE;
    public static int DEFAULT_BACKGROUND_COLOR = Color.GRAY;
    public static int DEFAULT_TEXT_SIZE = 20;
    public static int DEFAULT_TEXT_COLOR = Color.BLACK;
    /*circle_progress member*/
    public static int DEFAULT_STROKE_WIDTH = ConvertUtils.dp2px(40);
    QMUIProgressBarTextGenerator mQMUIProgressBarTextGenerator;
    /*rect_progress member*/
    RectF mBgRect;
    RectF mProgressRect;
    /*common member*/
    private int mWidth;
    private int mHeight;
    private int mType;
    private int mProgressColor;
    private int mBackgroundColor;
    private boolean isAnimating = false;
    private int mMaxValue;
    private int mValue;
    private ValueAnimator mAnimator;
    private Paint mBackgroundPaint = new Paint();
    private Paint mPaint = new Paint();
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mArcOval = new RectF();
    private String mText = "";
    private int mStrokeWidth;
    private int mCircleRadius;
    private Point mCenterPoint;
    private int startColor; // 渐变色起始色
    private int midColor; // 渐变色中间色
    private int endColor; // 渐变色起始色

    /**
     * 开启动画
     */
    private boolean isOpenAnim = true;

    public QMUIProgressBar(Context context) {
        super(context);
        setup(context, null);
    }

    public QMUIProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public QMUIProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    public void setup(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StudyQMUIProgressBar);
        mType = array.getInt(R.styleable.StudyQMUIProgressBar_qmui_type, TYPE_RECT);
        mProgressColor = array.getColor(R.styleable.StudyQMUIProgressBar_qmui_progress_color, DEFAULT_PROGRESS_COLOR);
        mBackgroundColor = array.getColor(R.styleable.StudyQMUIProgressBar_qmui_background_color, DEFAULT_BACKGROUND_COLOR);
        startColor = array.getColor(R.styleable.StudyQMUIProgressBar_qmui_progress_startColor, Color.GREEN);
        midColor = array.getColor(R.styleable.StudyQMUIProgressBar_qmui_progress_midColor, Color.GREEN);
        endColor = array.getColor(R.styleable.StudyQMUIProgressBar_qmui_progress_endColor, Color.GREEN);

        mMaxValue = array.getInt(R.styleable.StudyQMUIProgressBar_qmui_max_value, 100);
        mValue = array.getInt(R.styleable.StudyQMUIProgressBar_qmui_value, 0);

        boolean isRoundCap = array.getBoolean(R.styleable.StudyQMUIProgressBar_qmui_stroke_round_cap, false);

        int textSize = DEFAULT_TEXT_SIZE;
        if (array.hasValue(R.styleable.StudyQMUIProgressBar_android_textSize)) {
            textSize = array.getDimensionPixelSize(R.styleable.StudyQMUIProgressBar_android_textSize, DEFAULT_TEXT_SIZE);
        }
        int textColor = DEFAULT_TEXT_COLOR;
        if (array.hasValue(R.styleable.StudyQMUIProgressBar_android_textColor)) {
            textColor = array.getColor(R.styleable.StudyQMUIProgressBar_android_textColor, DEFAULT_TEXT_COLOR);
        }

        if (mType == TYPE_CIRCLE) {
            mStrokeWidth = array.getDimensionPixelSize(R.styleable.StudyQMUIProgressBar_qmui_stroke_width, DEFAULT_STROKE_WIDTH);
        }
        array.recycle();
        configPaint(textColor, textSize, isRoundCap);

        setProgress(mValue);
    }

    private void configShape() {
        if (mType == TYPE_RECT) {
            mBgRect = new RectF(getPaddingLeft(), getPaddingTop(), mWidth + getPaddingLeft(), mHeight + getPaddingTop());
            mProgressRect = new RectF();
        } else {
            mCircleRadius = (Math.min(mWidth, mHeight) - mStrokeWidth) / 2;
            mCenterPoint = new Point(mWidth / 2, mHeight / 2);
        }
    }

    private void configPaint(int textColor, int textSize, boolean isRoundCap) {
        mPaint.setColor(mProgressColor);
        mBackgroundPaint.setColor(mBackgroundColor);
        if (mType == TYPE_RECT) {
            mPaint.setStyle(Paint.Style.FILL);
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setAntiAlias(true);
            if (isRoundCap) {
                mPaint.setStrokeCap(Paint.Cap.ROUND);
            }
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setStrokeWidth(mStrokeWidth);
            mBackgroundPaint.setAntiAlias(true);
        }
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

//        if(){
//
//        }
    }

    /**
     * 设置进度文案的文字大小
     *
     * @see #setTextColor(int)
     * @see #setQMUIProgressBarTextGenerator(QMUIProgressBar.QMUIProgressBarTextGenerator)
     */
    public void setTextSize(int textSize) {
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * 设置进度文案的文字颜色
     *
     * @see #setTextSize(int)
     * @see #setQMUIProgressBarTextGenerator(QMUIProgressBar.QMUIProgressBarTextGenerator)
     */
    public void setTextColor(int textColor) {
        invalidate();
        mTextPaint.setColor(textColor);
    }

    /**
     * 设置环形进度条的两端是否有圆形的线帽，类型为{@link #TYPE_CIRCLE}时生效
     */
    public void setStrokeRoundCap(boolean isRoundCap) {
        mPaint.setStrokeCap(isRoundCap ? Paint.Cap.ROUND : Paint.Cap.BUTT);
        invalidate();
    }

    /**
     * 通过 {@link com.qmuiteam.qmui.widget.QMUIProgressBar.QMUIProgressBarTextGenerator} 设置进度文案
     */
    public void setQMUIProgressBarTextGenerator(QMUIProgressBarTextGenerator QMUIProgressBarTextGenerator) {
        mQMUIProgressBarTextGenerator = QMUIProgressBarTextGenerator;
    }

    public QMUIProgressBar.QMUIProgressBarTextGenerator getQMUIProgressBarTextGenerator() {
        return mQMUIProgressBarTextGenerator;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mQMUIProgressBarTextGenerator != null) {
            mText = mQMUIProgressBarTextGenerator.generateText(this, mValue, mMaxValue);
        }
        if (mType == TYPE_RECT) {
            drawRect(canvas);
        } else {
            drawCircle(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        configShape();
        setMeasuredDimension(mWidth, mHeight);
    }

    private void drawRect(Canvas canvas) {
        canvas.drawRect(mBgRect, mBackgroundPaint);
        mProgressRect.set(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + parseValueToWidth(), getPaddingTop() + mHeight);
        canvas.drawRect(mProgressRect, mPaint);
        if (mText != null && mText.length() > 0) {
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            float baseline = mBgRect.top + (mBgRect.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(mText, mBgRect.centerX(), baseline, mTextPaint);
        }
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mCircleRadius, mBackgroundPaint);
        mArcOval.left = mCenterPoint.x - mCircleRadius;
        mArcOval.right = mCenterPoint.x + mCircleRadius;
        mArcOval.top = mCenterPoint.y - mCircleRadius;
        mArcOval.bottom = mCenterPoint.y + mCircleRadius;
        // 根据进度画圆弧
        mPaint.setShader(new SweepGradient(mCenterPoint.x, mCenterPoint.x, new int[]{startColor, midColor, endColor}, null));
        canvas.drawArc(mArcOval, 270, 360 * mValue / mMaxValue, false, mPaint);
        mPaint.setShader(null);
        if (mText != null && mText.length() > 0) {
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            float baseline = mArcOval.top + (mArcOval.height() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(mText, mCenterPoint.x, baseline, mTextPaint);
        }
    }

    private int parseValueToWidth() {
        return mWidth * mValue / mMaxValue;
    }

    public int getProgress() {
        return mValue;
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public void setProgress(int progress, boolean animated) {
        if (progress > mMaxValue || progress < 0) {
            return;
        }

        if (isAnimating) {
            isAnimating = false;
            mAnimator.cancel();
        }
        int oldValue = mValue;
        mValue = progress;
        if (animated) {
            startAnimation(oldValue, progress);
        } else {
            invalidate();
        }

    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
    }

    private void startAnimation(int start, int end) {
        mAnimator = ValueAnimator.ofInt(start, end);
        int duration =  Math.abs(TOTAL_DURATION * (end - start) / mMaxValue);

//        if((end-start) == 0||(mMaxValue/TOTAL_DURATION) == 0){
//            return;
//        }
//        LogUtils.e("startAnimation-->"+"end"+end+"start"+start+"mMaxValue"+mMaxValue);
        //时间 = 位移除以速度
//        int duration = (end-start)/(mMaxValue/TOTAL_DURATION);
        mAnimator.setDuration(duration);
        //匀速插值器
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (int) animation.getAnimatedValue();
                invalidate();
                if(mValue == mMaxValue){
                    if(finishListener!=null&&isOpenAnim){
                        isOpenAnim = false;
                        finishListener.onFinishListener();
                    }
                }
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                isAnimating = false;

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mAnimator.start();
    }


    public interface QMUIProgressBarTextGenerator {
        /**
         * 设置进度文案, {@link QMUIProgressBar} 会在进度更新时调用该方法获取要显示的文案
         * @param value 当前进度值
         * @param maxValue 最大进度值
         * @return 进度文案
         */
        String generateText(QMUIProgressBar progressBar, int value, int maxValue);
    }

    public void setTotalDuration(int totalDuration){
        TOTAL_DURATION = totalDuration;
    }

    public boolean isOpenAnim() {
        return isOpenAnim;
    }

    public void setOpenAnim(boolean openAnim) {
        isOpenAnim = openAnim;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * 进度完成监听接口
     */
    private ProgressFinishListener finishListener;

    public void setFinishListener(ProgressFinishListener finishListener) {
        this.finishListener = finishListener;
    }

    public interface ProgressFinishListener{
        /**
         * 进度完成监听
         */
        void onFinishListener();
    }
}
