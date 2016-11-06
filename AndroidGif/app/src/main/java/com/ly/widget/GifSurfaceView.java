package com.ly.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.ly.gif.R;

/**
 * Created by LY on 2016-09-04.
 */
public class GifSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    public static final String TAG = "GifSurfaceView";
    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private Movie mMovie;

    private long mMovieStart;
    private int mCurrentAnimationTime = 0;

    /**
     * Position for drawing animation frames in the center of the view.
     */
    private float mLeft;
    private float mTop;

    /**
     * Scaling factor to fit the animation within view bounds.
     */
    private float mScale;

    /**
     * Scaled movie frames width and height.
     */
    private int mMeasuredMovieWidth;
    private int mMeasuredMovieHeight;

    private volatile boolean mPaused = false;
    private boolean mVisible = true;

    private PlayMovieThread mPlayMovieThread;
    private SurfaceHolder mSurfaceHolder;
    private boolean mThreadFlag = false;
    public GifSurfaceView(Context context) {
        this(context,null);
    }

    public GifSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CustomTheme_gifMoviewViewStyle);
    }

    public GifSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mPlayMovieThread = new PlayMovieThread();
    }
    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle) {

        /**
         * Starting from HONEYCOMB have to turn off HW acceleration to draw
         * Movie on Canvas.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GifMoviewView, defStyle,
                R.style.Widget_GifMoviewView);

        mMovieResourceId = array.getResourceId(R.styleable.GifMoviewView_gif, -1);
        mPaused = array.getBoolean(R.styleable.GifMoviewView_paused, false);

        array.recycle();

        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        }
    }
    public synchronized void setMovieResource(int movieResId) {
        this.mMovieResourceId = movieResId;
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
    }
    public synchronized void setMovie(Movie movie) {
        this.mMovie = movie;
    }

    public synchronized Movie getMovie() {
        return mMovie;
    }

    public synchronized void setMovieTime(int time) {
        mCurrentAnimationTime = time;
    }

    public synchronized void setPaused(boolean paused) {
        this.mPaused = paused;

        /**
         * Calculate new movie start time, so that it resumes from the same
         * frame.
         */
        if (!paused) {
            mMovieStart = android.os.SystemClock.uptimeMillis() - mCurrentAnimationTime;
            if(mPlayMovieThread != null){
                synchronized (mPlayMovieThread){
                    mPlayMovieThread.notifyAll();
                }
            }
        }
    }

    public synchronized boolean isPaused() {
        return this.mPaused;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: changed " + changed);
        Log.d(TAG, "onLayout: l " + left);
        Log.d(TAG, "onLayout: t " + top);
        Log.d(TAG, "onLayout: r " + right);
        Log.d(TAG, "onLayout: b " + bottom);
		/*
		 * Calculate left / top for drawing in center
		 */
        mLeft = (getWidth() - mMeasuredMovieWidth) / 2f;
        mTop = (getHeight() - mMeasuredMovieHeight) / 2f;
        Log.d(TAG, "onLayout: mLeft " + mLeft);
        Log.d(TAG, "onLayout: mTop " + mTop);
        mVisible = getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: widthMeasureSpec " + widthMeasureSpec);
        Log.d(TAG, "onMeasure: heightMeasureSpec " + heightMeasureSpec);
        if (mMovie != null) {
            int movieWidth = mMovie.width();
            int movieHeight = mMovie.height();
            Log.d(TAG, "onMeasure: movieWidth " + movieWidth);
            Log.d(TAG, "onMeasure: movieHeight " + movieHeight);

			/*
			 * Calculate horizontal scaling
			 */
            float scaleH = 1f;
            int measureModeWidth = MeasureSpec.getMode(widthMeasureSpec);
            logMeasureMode(measureModeWidth,"measureModeWidth");
            if (measureModeWidth != MeasureSpec.UNSPECIFIED) {
                int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
                Log.d(TAG, "onMeasure: maximumWidth " + maximumWidth);
                if (movieWidth > maximumWidth) {
                    scaleH = (float) movieWidth / (float) maximumWidth;
                }
            }

			/*
			 * calculate vertical scaling
			 */
            float scaleW = 1f;
            int measureModeHeight = MeasureSpec.getMode(heightMeasureSpec);
            logMeasureMode(measureModeHeight,"measureModeHeight");
            if (measureModeHeight != MeasureSpec.UNSPECIFIED) {
                int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
                Log.d(TAG, "onMeasure: maximumHeight " + maximumHeight);
                if (movieHeight > maximumHeight) {
                    scaleW = (float) movieHeight / (float) maximumHeight;
                }
            }

			/*
			 * calculate overall scale
			 */
            mScale = 1f / Math.max(scaleH, scaleW);

            mMeasuredMovieWidth = (int) (movieWidth * mScale);
            mMeasuredMovieHeight = (int) (movieHeight * mScale);
            Log.d(TAG, "onMeasure: mMeasuredMovieWidth " + mMeasuredMovieWidth);
            Log.d(TAG, "onMeasure: mMeasuredMovieHeight " + mMeasuredMovieHeight);
            setMeasuredDimension(mMeasuredMovieWidth, mMeasuredMovieHeight);

        } else {
			/*
			 * No movie set, just set minimum available size.
			 */
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }
    private void logMeasureMode(int mode,String des){
        switch (mode){
            case MeasureSpec.AT_MOST:
                Log.d(TAG, "logMeasureMode: "+ des + "  AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.d(TAG, "logMeasureMode: "+ des + "  EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.d(TAG, "logMeasureMode: "+ des + "  UNSPECIFIED");
                break;
        }
    }

    private void invalidateView() {
        if(mVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }
    /**
     * Calculate current animation time
     */
    long mMovieStartSync = 0;
    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
            mMovieStartSync = 0;
        }
//        Log.d(TAG, "updateAnimationTime: " + (now - mMovieStartSync));
        mMovieStartSync = now;
        int dur = mMovie.duration();
//        Log.d(TAG, "updateAnimationTime: duration " + dur);

        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }

        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    /**
     * Draw current GIF frame
     */
    private void drawMovieFrame(Canvas canvas) {
        if(canvas == null){
            return;
        }
//        Log.d(TAG, "drawMovieFrame: mCurrentAnimationTime " + mCurrentAnimationTime);
        mMovie.setTime(mCurrentAnimationTime);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mScale, mScale);
        mMovie.draw(canvas, mLeft / mScale, mTop / mScale);
        canvas.restore();
    }


    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        mVisible = screenState == SCREEN_STATE_ON;
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        mVisible = visibility == View.VISIBLE;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == View.VISIBLE;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        mThreadFlag = true;
        mPlayMovieThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
        mThreadFlag = false;

    }

    class PlayMovieThread extends Thread{

        @Override
        public void run() {
            while(mThreadFlag){
                if (mMovie != null) {
                    Canvas canvas = mSurfaceHolder.lockCanvas(new Rect((int)mLeft,(int)mTop,mMeasuredMovieWidth,mMeasuredMovieHeight));
                    if(canvas != null){
                        if (!mPaused) {
                            updateAnimationTime();
                            drawMovieFrame(canvas);
                        } else {
                            drawMovieFrame(canvas);
                        }
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    if(mPaused){
                        try {
                            synchronized (this){
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
