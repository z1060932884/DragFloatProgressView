package com.zzj.dragfloatprograssview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.zzj.dragfloatprograssview.R;


/**
 * @author : zzj
 * @e-mail : zhangzhijun@pansoft.com
 * @date : 2019/6/4 9:44
 * @desc :  学习详情页的拖拽浮动布局
 * @version: 1.0
 */
public class DragFloatActionView extends FrameLayout {
    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;
    private int virtualHeight;
    private ImageView circleImageView;
    private QMUIProgressBar progressBar;
    /**
     * 积分布局
     */
    private FrameLayout fl_integral;
    /**
     * 积分文本
     */
    private TextView tv_integral,tv_ios_integral;
    /**
     * 动画时间
     */
    private int AnimalDuration = 5000;

    private int lastX;
    private int lastY;

    private boolean isDrag;
    public DragFloatActionView(Context context) {
        super(context);
        init();
    }

    public DragFloatActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.study_news_details_drag_layout,this,true);
        progressBar = findViewById(R.id.pb_time);
        circleImageView = findViewById(R.id.fab_view);
        tv_integral = findViewById(R.id.tv_integral);
        tv_ios_integral = findViewById(R.id.tv_ios_integral);
        fl_integral = findViewById(R.id.fl_integral);
        screenWidth = ScreenUtils.getScreenWidth();
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtils.getScreenHeight();
        statusHeight = BarUtils.getStatusBarHeight();
        virtualHeight= BarUtils.getNavBarHeight();
        initListener();
    }

    /**
     * 事件监听
     */
    private void initListener() {

        progressBar.setFinishListener(new QMUIProgressBar.ProgressFinishListener() {
            @Override
            public void onFinishListener() {
//               startAndroidAnimation();
                startIosAnimation();
            }
        });
    }

    /**
     * 仿IOS动画效果
     */
    private void startIosAnimation() {

        //1、中间图片向下平移 2、中间积分由小到大显示出现来，然后积分由大到小消失，中间图片向上平移出现
        float translationY = circleImageView.getTranslationY();
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(circleImageView, "translationY", translationY, 130f);
        translationYAnimator.setDuration(800);
        translationYAnimator.start();
        translationYAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tv_ios_integral.setVisibility(VISIBLE);
                ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(tv_ios_integral, "scaleX", 0, 1,1,1,1,1,0);
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(tv_ios_integral, "scaleY", 0, 1,1,1,1,1,0);
                //动画集合
                AnimatorSet set = new AnimatorSet();
                //添加动画
                set.play(objectAnimator1).with(objectAnimator2);
                //设置时间等
                set.setDuration(2000);
                set.start();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progressBar.setOpenAnim(true);
                        progressBar.setProgress(0,false);
                        progressBar.setProgress((progressBar.getMaxValue())/5,true);
                        float translationY = circleImageView.getTranslationY();
                        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(circleImageView, "translationY", translationY, 0f);
                        translationYAnimator.setDuration(800);
                        translationYAnimator.start();

                    }
                });
            }
        });
    }

    /**
     * 仿android动画效果
     */
    private void startAndroidAnimation() {
        //缩放---ofFloat用4个参数的ofFloat
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(circleImageView, "scaleX", 1, 3);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(circleImageView, "scaleY", 1, 3);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.play(objectAnimator1).with(objectAnimator2);
        //设置时间等
        set.setDuration(500);
        set.start();
        //动画监听
        //设置动画结束后消失
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                timer.start();
                /**
                 * 显示积分动画
                 */
                startIntegralAnimation();
//                        iv_anim.setVisibility(VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

    }

    /**
     * 创建积分动画
     */
    private void startIntegralAnimation() {
        fl_integral.setVisibility(VISIBLE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tv_integral, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tv_integral, "scaleY", 0f, 1f);
        float translationY = tv_integral.getTranslationY();
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(tv_integral, "translationY", translationY, -30f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(tv_integral, "alpha", 0, 1);
        //动画集合
        AnimatorSet set = new AnimatorSet();
        //添加动画
        set.play(scaleX).with(scaleY).with(translationYAnimator).with(alpha);
        //设置时间等
        set.setDuration(500);
        set.start();
        //动画监听
        //设置动画结束后消失
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(tv_integral, "scaleX", 1f, 0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(tv_integral, "scaleY", 1f, 0f);
                float translationY = tv_integral.getTranslationY();
                ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(tv_integral, "translationY", translationY, -30f);
                //动画集合
                AnimatorSet set = new AnimatorSet();
                //添加动画
                set.play(scaleX).with(scaleY).with(translationYAnimator);
                //设置时间等
                set.setDuration(1000);
                set.setStartDelay(800);
                set.start();
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fl_integral.setVisibility(GONE);
                        tv_integral.setTranslationY(tv_integral.getTranslationY()+60f);
                    }
                });
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                Log.e("down---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些手机无法触发点击事件的问题
                int distance= (int) Math.sqrt(dx*dx+dy*dy);
//                Log.e("distance---->",distance+"");
                if(distance<3){//给个容错范围，不然有部分手机还是无法点击
                    isDrag=false;
                    break;
                }

                float x = getX() + dx;
                float y = getY() + dy;

                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                // y = y < statusHeight ? statusHeight : (y + getHeight() >= screenHeight ? screenHeight - getHeight() : y);
                if (y<0){
                    y=0;
                }
                if (y>screenHeight-statusHeight-getHeight()-virtualHeight- ConvertUtils.dp2px(40)){
                    y=screenHeight-statusHeight-getHeight()-virtualHeight-ConvertUtils.dp2px(40);
                }
                setX(x);
                setY(y);

                lastX = rawX;
                lastY = rawY;
//                Log.e("move---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf + " " + isDrag+"  statusHeight="+statusHeight+ " virtualHeight"+virtualHeight+ " screenHeight"+ screenHeight+"  getHeight="+getHeight()+" y"+y);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
//                    Log.e("ACTION_UP---->", "getX=" + getX() + "；screenWidthHalf=" + screenWidthHalf);
                    if (rawX >= screenWidthHalf) {
                        animate().setInterpolator(new DecelerateInterpolator())
                                .setDuration(500)
                                .xBy(screenWidth - getWidth() - getX())
                                .start();
                    } else {
                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                        oa.setInterpolator(new DecelerateInterpolator());
                        oa.setDuration(500);
                        oa.start();
                    }
                }
//                Log.e("up---->",isDrag+"");
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);

    }

    /**
     * 获取进度时间
     * @return
     */
    public int getTotalAnimalDuration() {

        return AnimalDuration;
    }

    /**
     * 设置进度时间
     * @param animalDuration
     */
    public void setTotalAnimalDuration(int animalDuration) {
        progressBar.setTotalDuration(animalDuration);
        AnimalDuration = animalDuration;
    }

    public ImageView getFloatingActionButton() {
        return circleImageView;
    }


    /**
     * 获取ProgressBar控件
     * @return
     */
    public QMUIProgressBar getProgressBar() {
        return progressBar;
    }


    /**
     * 设置最大进度
     * @param progress
     */
    public void setMaxProgress(int progress){
        if(progressBar!=null){
            progressBar.setMaxValue(progress);
        }
    }

    /**
     * 设置当前进度
     * @param progress
     */
    public void setCurrentProgress(int progress){
        if(progressBar!=null){
//            Math.abs(TOTAL_DURATION * (end - start) / mMaxValue)
            setCurrentProgress(progress,false);
        }
    }

    /**
     *  设置当前进度
     * @param progress
     * @param animated 是否
     */
    public void setCurrentProgress(int progress,boolean animated){
        if(progressBar!=null){
            progressBar.setProgress(progress,animated);
        }
    }

    public int getCurrentPosition() {
        if(progressBar!=null){
            return  progressBar.getProgress();
        }
        return 0;
    }

    /**
     * 倒数计时器
     * 用来处理动画暂停的效果
     */
    private CountDownTimer timer = new CountDownTimer(2000, 1000) {
        /**
         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {

        }

        /**
         * 倒计时完成时被调用
         */
        @Override
        public void onFinish() {
            progressBar.setOpenAnim(true);
            progressBar.setProgress(0,false);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(circleImageView, "scaleX", 3, 2f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(circleImageView, "scaleY", 3, 2f);
            float translationY = circleImageView.getTranslationY();
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(circleImageView, "translationY", translationY, -30f);
            //动画集合
            AnimatorSet set = new AnimatorSet();
            //添加动画
            set.play(objectAnimator1).with(objectAnimator2).with(translationYAnimator);
            //设置时间等
            set.setDuration(300);
            set.start();
            //动画监听
            //设置动画结束后消失
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(circleImageView, "scaleX", 2f, 1f);
                    ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(circleImageView, "scaleY", 2f, 1f);
                    float translationY = circleImageView.getTranslationY();
                    ObjectAnimator translationYAnimator1 = ObjectAnimator.ofFloat(circleImageView, "translationY", translationY, 0);
                    //动画集合
                    AnimatorSet set = new AnimatorSet();
                    //添加动画
                    set.play(objectAnimator1).with(objectAnimator2).with(translationYAnimator1);
                    //设置时间等
                    set.setDuration(500);
                    set.start();
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                }
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
        }
    };

    /**
     *获取进度条是否在滚动中
     * @return
     */
    public boolean isProgressAnimating() {
        return progressBar.isAnimating();
    }
}
