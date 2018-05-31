package com.example.arbane.surfaceviewtest;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arbane on 5/22/2018.
 */

public final class OLGLSurfaceView extends GLSurfaceView {

    int a = 0;

    OLSurfaceViewRenderer mRenderer;

    public OLGLSurfaceView(Context context) {
        super(context);
    }

    public OLGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(OLSurfaceViewRenderer renderer) {
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        mRenderer = renderer;
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

    public void setEffect(ImageEffect imageEffect) {
        mRenderer.setImageEffect(imageEffect);
        requestRender();
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setBitmap(Bitmap bmp) {
        mRenderer.setImageBitmap(bmp);
        requestRender();
    }

    public void rotate(View v1, float degrees, int duration) {
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mRenderer.isAnimationActive = true;
//        a++;
//        float scale = (a%2==1) ? 1.1f : -1.1f;
//        v1.animate().rotationBy(90).setDuration(duration).setListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//                requestRender();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        }).scaleYBy(scale).scaleXBy(scale);

//                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRenderer.isAnimationActive = false;
            }
        }, 2000);

    }

}
