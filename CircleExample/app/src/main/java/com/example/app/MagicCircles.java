package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import java.util.ArrayList;
import java.util.List;

public class MagicCircles implements View.OnTouchListener,Animation.AnimationListener,View.OnClickListener{

/** ViewGroup to Make magic circles **/
    private ViewGroup mParentLayout;
    private RelativeLayout mPopUpLayout;

/** springs  to produce touch effects **/
    private Spring springTouch,springAppear;

    private static WindowManager windowManager;
    private static WindowManager.LayoutParams  params;

    private OnMagicCircleClick onMagicCircleClick;

/** cancelable flag make it cancel on touch outside , movable flag makes it movable **/
    private int type ,DP_5,DP_3;
    private boolean cancelable=true,movable=false,dragging=false;
    private static int nCircle=0;

/** List of view group ( mPopUpLayout ) used for magic circle **/

    private final static List<ViewGroup> viewGroupList=new ArrayList<ViewGroup>();

/** params for movement of circle **/
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    /**
     Constructor takes
     @param context
     @param x is the x coordinate in dp
     @param y id the y coordinate of circle in dp
     @param types is the resource id for the drawable
     @param movable decides weather the circle will move on screen or not as the user drag
     **/

    public MagicCircles(Context context,int x,int y,int types,boolean movable){

    this.type=types;

    this.movable=movable;

     if(params==null)
     params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);

     if(windowManager==null)
     windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);


     Animation fade= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
     fade.setDuration(500);
     fade.setAnimationListener(this);

     mParentLayout =new FrameLayout(context);
     mParentLayout.setOnTouchListener(this);

     mPopUpLayout  = new RelativeLayout(context);
     mPopUpLayout.setOnTouchListener(this);
     mPopUpLayout.setOnClickListener(this);
     mPopUpLayout.setAnimation(fade);
     fade.cancel();
     fade.reset();
     mParentLayout.addView(mPopUpLayout);


     ImageView imageView=new ImageView(context);
     DP_5=dpToPx(context,5);
     DP_3=dpToPx(context,3);
     Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),types);
     imageView.setImageBitmap(getCroppedBitmap(bitmap,bitmap.getWidth(),true));

     mPopUpLayout.addView(imageView);

     SpringSystem springSystem=SpringSystem.create();
     springTouch =springSystem.createSpring();
     SpringConfig springConfig =new SpringConfig(1000.0,15.0);
     springTouch.setSpringConfig(springConfig);
     springTouch.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (0.5f * value);
                mPopUpLayout.setScaleX(scale);
                mPopUpLayout.setScaleY(scale);
            }
     });

     springAppear=springSystem.createSpring();
     springAppear.setSpringConfig(new SpringConfig(1000.0,35.0));

     springAppear.addListener(new SimpleSpringListener(){
            @Override
            public void onSpringUpdate(Spring spring) {
                mPopUpLayout.setScaleX((float)(1-spring.getCurrentValue()));
                mPopUpLayout.setScaleY((float)(1-spring.getCurrentValue()));
            }
        });


    params.y=dpToPx(context,y);
    params.x=dpToPx(context,x);



    try
    {
        windowManager.addView(mParentLayout,params);
    }
    catch (Exception ex)
    {
        ex.printStackTrace();
    }

    mParentLayout.setVisibility(View.GONE);

    viewGroupList.add(mPopUpLayout);
}

    /**
     To give Touch feedback and Handle dragging
     **/
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                 springTouch.setVelocity(5.0);
                 springTouch.setEndValue(0.3f);
                 if(movable)
                 {
                 dragging=false;
                 initialX = params.x;
                 initialY = params.y;
                 initialTouchX = event.getRawX();
                 initialTouchY = event.getRawY();
                 }
                 return false;
            case MotionEvent.ACTION_UP:
                 springTouch.setVelocity(5.0);
                 springTouch.setEndValue(0.0f);

                 return false;
            case MotionEvent.ACTION_OUTSIDE:
                if (!viewGroupList.isEmpty())
                {
                    if(mPopUpLayout.equals(viewGroupList.get(0))&&cancelable)
                    {
                       destroy();
                    }
                }
                 return false;
            case MotionEvent.ACTION_MOVE:
                if(movable)
                {
                    int moveX=(int) (event.getRawX() - initialTouchX);
                    int moveY=(int) (event.getRawY() - initialTouchY);

                    if(moveX>10||moveY>10)
                    {
                       dragging=true;
                    }

                    params.x = initialX + moveX ;
                    params.y = initialY + moveY ;

                    windowManager.updateViewLayout(mParentLayout, params);
                }
                 return false;
        }

        return false;
    }
    /** fade Animation  callbacks   **/

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    try
    {
        springTouch.removeAllListeners();
        springAppear.removeAllListeners();
        springAppear.destroy();
        springTouch.destroy();

        mPopUpLayout.clearAnimation();
        mPopUpLayout.removeAllViews();
        mPopUpLayout.setOnClickListener(null);
        mPopUpLayout.setOnTouchListener(null);
        viewGroupList.remove(mPopUpLayout);

        mParentLayout.removeAllViews();
        mParentLayout.setOnTouchListener(null);
        mParentLayout.setVisibility(View.GONE);
        windowManager.removeView(mParentLayout);

    }
    catch (Exception ex)
    {
        ex.printStackTrace();
    }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /** Circle On Click  **/
    @Override
    public void onClick(View view)
    {
        if(onMagicCircleClick!=null&&!dragging)
        {
        destroy();
        onMagicCircleClick.onMagicCircleClick(mPopUpLayout,type);
        }
    }
    /** sets OnClick for magic circle  **/
    public void setOnMagicCircleClick(OnMagicCircleClick onMagicCircleClick) {
        this.onMagicCircleClick = onMagicCircleClick;
    }

    /** sets
      @param cancelable flag **/
    public void setCancelableOnTouchOutside(boolean cancelable){
        this.cancelable=cancelable;
    }

    /** sets
       @param movable flag **/
    public void setMovable(boolean movable){
        this.movable=movable;
        }
    /**
     sets image of circle
     @param context
     @param id resource id of the image
     **/
    public void setCircleImage(Context context,int id){
        ImageView imageView=(ImageView)mPopUpLayout.getChildAt(0);
        if(imageView!=null)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),id);
            int w = bitmap.getWidth();
            imageView.setImageBitmap(getCroppedBitmap(bitmap,w,true));
            imageView.invalidate();
        }
    }

    /**
     * show the circle after delay
     * @param milli delay in milliseconds
     */
    void show(int milli)
    {
        nCircle++;
        new Handler(){
            @Override
            public void handleMessage(Message msg) {
               try
               {
                springAppear.setCurrentValue(1.0f);
                springAppear.setEndValue(0.0f);
                mParentLayout.setVisibility(View.VISIBLE);
               }
               catch (Exception ex)
               {
                ex.printStackTrace();
               }

            }
        }.sendEmptyMessageDelayed(0,milli);

    }

    /**
     * adds a spin circle to have spinning effects
     * @param context
     */
    void addSpinAnimationView(Context context)
    {
        ImageView search=new ImageView(context);
        search.setTag("SpinImage");

        int SPIN_HEIGHT,SPIN_WIDTH;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),type);
        SPIN_WIDTH= bitmap.getWidth()/2;
        SPIN_HEIGHT= bitmap.getHeight()/2;
        search.setImageBitmap(getSpinningImage(bitmap,2*SPIN_WIDTH,90,120));

        Animation a = new RotateAnimation(0.0f, 360.0f,SPIN_WIDTH,SPIN_HEIGHT);
        a.setInterpolator(new LinearInterpolator());
        a.setRepeatCount(Animation.INFINITE);
        a.setDuration(500);

        mPopUpLayout.addView(search);

        search.startAnimation(a);
    }

    /**
     * removes the spin animation added above
     */
    void removeSpinAnimation()
    {
        View view;
        if((view=mPopUpLayout.getChildAt(1))!=null&&view.getTag().equals("SpinImage"))
        {
            view.clearAnimation();
            mPopUpLayout.removeView(view);
        }
    }

    /**
     *
     * @return no of circle currently displayed on screen
     */
    static int nCircleDisplayed()
    {
        return nCircle;
    }

    /**
     * add a view to mPopUpLayout
     * @param view to be added
     */

    void addExtraView(View view)
    {
        mPopUpLayout.addView(view);
    }

    /**
     * updates current position of circle on the screen
     * @param x position on screen
     * @param y position on screen
     */

    void updateMagicCirclePosition(int x,int y)
    {
        params.y=y;
        params.x=x;
        try
        {

        windowManager.updateViewLayout(mParentLayout,params);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * this function initiates fade animation on all magic circle
     * all circle will automatically destroy if one of the circle is clicked
     */

    void destroy()
    {
        for(ViewGroup vg : viewGroupList)
        {
            try
            {
                vg.startAnimation(vg.getAnimation());
                nCircle--;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
    }

    /**
     * to draw magic circle tinker this if you want to customise shadow and transparency
     * @param bmp bitmap of the circle image
     * @param radius width of the image
     * see the sample image in the resource try to keep canvas to actual image ratio same as sample image
     * @param drawImage when false it will draw the circle with image
     * @return bitmap to set image
     */

    private Bitmap getCroppedBitmap(Bitmap bmp, int radius,boolean drawImage) {
        Paint translucentWhite =new Paint();
        Paint  paint=new Paint();
        Bitmap sBmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sBmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sBmp = bmp;
        Bitmap output = Bitmap.createBitmap(sBmp.getWidth(),
                sBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        translucentWhite.setAntiAlias(true);
        translucentWhite.setColor(0xDFF5F5F5);
        translucentWhite.setShadowLayer((float) DP_3, 0.1f, 0.1f, Color.GRAY);
        translucentWhite.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawCircle(sBmp.getWidth() / 2, sBmp.getHeight() / 2 ,
                sBmp.getWidth() / 2 - DP_5, translucentWhite);
        paint.setAntiAlias(true);
        if(drawImage)
            canvas.drawBitmap(sBmp,0,0, paint);

        return output;
    }

    /**
     * produces spin image
     * tinker this if you want to customize the spinner (like add a sweep gradient)
     */
    private Bitmap getSpinningImage(Bitmap bmp,int radius,int from,int to) {
        Paint barPaint=new Paint();
        Bitmap sBmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sBmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sBmp = bmp;
        Bitmap output = Bitmap.createBitmap(sBmp.getWidth(),
                sBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        barPaint.setColor(0xFF339BB9);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.STROKE);
        barPaint.setStrokeWidth(4);
        barPaint.setShadowLayer((float)DP_5, 0.1f, 0.1f, 0xAA10E6C5);
        canvas.drawArc(new RectF(DP_5,DP_5, sBmp.getWidth()-DP_5, sBmp.getHeight()-DP_5), from, to, false, barPaint);
        return output;
    }

    /*
    changes dp value to px value
     */
    private int dpToPx(Context context,int dp)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}