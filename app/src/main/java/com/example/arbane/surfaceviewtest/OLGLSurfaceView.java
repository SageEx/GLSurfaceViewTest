package com.example.arbane.surfaceviewtest;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by arbane on 5/22/2018.
 */

public final class OLGLSurfaceView extends GLSurfaceView {

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
    }

    public void setBitmap(Bitmap bmp) {
        mRenderer.setImageBitmap(bmp);
        requestRender();
    }

    public void rotate(float degrees, int duration) {
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        this.animate().rotation(degrees).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                requestRender();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

}
