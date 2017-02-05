package com.ly.nativegifsurfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDecoder;
import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;

/**
 * Created by LY on 2016-12-24.
 */
public class GifSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    HandlerThread mDrawThread;
    Handler mHanlder;

    GifDecoder mGifDecoder;
    SurfaceHolder mSurfaceHolder;
    public GifSurfaceView(Context context) {
        super(context);

        mDrawThread = new HandlerThread("gif");
        mDrawThread.start();
        mHanlder = new Handler(mDrawThread.getLooper());
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        InputSource.FileSource fileSource = new InputSource.FileSource(Environment.getExternalStorageDirectory() + "/1.gif");
        try {
            mGifDecoder = new GifDecoder(fileSource);
            mHanlder.post(new Runnable() {
                @Override
                public void run() {
                    Canvas canvas = mSurfaceHolder.lockCanvas();

                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is called immediately after any structural changes (format or
     * size) have been made to the surface.  You should at this point update
     * the imagery in the surface.  This method is always called at least
     * once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
