package com.example.reige.switchview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by REIGE on 2017/3/11.
 */

public class SwitchView extends View {
    //画笔
    private Paint paint;
    private Bitmap mButton;
    private Bitmap mSwitchBackground;
    private Bitmap mSliderBackground;
    private Bitmap mMask;
    private PorterDuffXfermode mfermode;
    //开关状态
    private boolean mState;
    private float mCurrentX;

    private OnStateChangeListener mOnStateChangeListener;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSwitchBackground.getWidth(), mSliderBackground.getHeight());
    }

    public SwitchView(Context context) {
        super(context);
        init();
    }

    public SwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //命名空间
        String namespace = "http://schemas.android.com/apk/res-auto";
        //默认的开关状态
        boolean state = attrs.getAttributeBooleanValue(namespace, "state", true);
        //滑动背景
        int slider_background = attrs.getAttributeResourceValue(namespace, "slider_background", -1);
        //开关背景
        int switch_background = attrs.getAttributeResourceValue(namespace, "switch_background", -1);
        //按钮
        int button = attrs.getAttributeResourceValue(namespace, "button", -1);
        //遮罩
        int mask = attrs.getAttributeResourceValue(namespace, "mask", -1);


        setSwitchBackground(switch_background);
        setSliderBackground(slider_background);
        setButton(button);
        setMask(mask);
        init();
    }

    public SwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //初始化画笔
        paint = new Paint();

        mfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int saveFlags = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas
                .HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas
                .CLIP_TO_LAYER_SAVE_FLAG;
        canvas.saveLayer(0, 0, mSwitchBackground.getWidth(), mSwitchBackground.getHeight(), null,
                saveFlags);
        canvas.drawBitmap(mMask, 0, 0, paint);
        paint.setXfermode(mfermode);



        //定义滑块可到达的最大值 最小值
        float btnMaxLeft = - mButton.getWidth() / 2.0f + (mButton.getHeight()-15)/2.0f;
        float btnMaxRight = 0;
        float sliderMaxLeft = - mSliderBackground.getWidth() / 2.0f + (mButton.getHeight()-15)/2.0f;
        float sliderMaxRight = 0;

        if(isSlideMode){
            //在滑动状态下面 计算当前位置
            float btnNewLeft = mCurrentX - mButton.getWidth() / 2.0f;
            float sliderNewLeft = mCurrentX - mSliderBackground.getWidth() / 2.0f;
            // 限定滑块范围
            if(btnNewLeft <  btnMaxLeft){
                btnNewLeft =  btnMaxLeft; // 左边范围
            }else if (btnNewLeft > btnMaxRight) {
                btnNewLeft = btnMaxRight; // 右边范围
            }
            if(sliderNewLeft <  sliderMaxLeft){
                sliderNewLeft =  sliderMaxLeft; // 左边范围
            }else if (sliderNewLeft > sliderMaxRight) {
                sliderNewLeft = sliderMaxRight; // 右边范围
            }


            canvas.drawBitmap(mSliderBackground, sliderNewLeft, 0, paint);
            paint.setXfermode(null);
            canvas.drawBitmap(mSwitchBackground,0,0,paint);
            //最后画开关
            canvas.drawBitmap(mButton, btnNewLeft, 0, paint);
        }else {
            if(mState){
                canvas.drawBitmap(mSliderBackground, sliderMaxLeft, 0, paint);
                paint.setXfermode(null);
                canvas.drawBitmap(mSwitchBackground,0,0,paint);
                canvas.drawBitmap(mButton, btnMaxLeft, 0, paint);
            }else {
                canvas.drawBitmap(mSliderBackground, sliderMaxRight, 0, paint);
                paint.setXfermode(null);
                canvas.drawBitmap(mSwitchBackground,0,0,paint);
                canvas.drawBitmap(mButton, btnMaxRight, 0, paint);
            }
        }

        canvas.restore();

    }

    private boolean isSlideMode = false;
    private float upX ;
    private float startX ;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                mCurrentX = event.getX();
                startX = mCurrentX;

                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(startX-event.getX())>2){


                isSlideMode = true;
                mCurrentX = event.getX(); }
                break;

            case MotionEvent.ACTION_UP:
                isSlideMode = false;
                float upX = event.getX();
                if(upX == startX){
                    mState = !mState;
                    mOnStateChangeListener.onStateChange(mState);
                    break;
                }
                float center = mSwitchBackground.getWidth() / 2.0f;
                boolean oldState = mState;
                mState = center > mCurrentX;
                if(oldState != mState){
                    mOnStateChangeListener.onStateChange(mState);
                }

                break;
        }
        //重绘界面
        invalidate();
        //消费掉该事件 才可以接收其它事件
        return true;
    }

    /**
     * 设置switch背景
     * @param id
     */
    public void setSwitchBackground(int id) {
        mSwitchBackground = BitmapFactory.decodeResource(getResources(), id);
    }

    /**
     * 设置滑动条背景
     * @param id
     */
    public void setSliderBackground(int id) {
        mSliderBackground = BitmapFactory.decodeResource(getResources(), id);
    }

    /**
     * 设置按钮
     * @param id
     */
    public void setButton(int id) {
        mButton = BitmapFactory.decodeResource(getResources(), id);
    }

    /**
     * 设置遮罩
     * @param id
     */
    public void setMask(int id) {
        mMask = BitmapFactory.decodeResource(getResources(), id);
    }

    public interface OnStateChangeListener{
        void onStateChange(boolean state);
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener){
        this.mOnStateChangeListener = onStateChangeListener;
    }

}
