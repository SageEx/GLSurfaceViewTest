package com.example.arbane.surfaceviewtest;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.security.spec.MGF1ParameterSpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final OLGLSurfaceView mgsv = findViewById(R.id.glsv);
        mgsv.init(new OLSurfaceViewRenderer(BitmapFactory.decodeResource(getResources(), R.drawable.wall_wall),  OLSurfaceViewRenderer.RenderMode.FULL_VIEW_MODE,this, mgsv));

        setEffectButton(R.id.b1, ImageEffect.EFFECT_MONO, mgsv);
        setEffectButton(R.id.b2, ImageEffect.EFFECT_LOMOISH, mgsv);
        setEffectButton(R.id.b3, ImageEffect.EFFECT_SEPIA, mgsv);
        setEffectButton(R.id.b4, ImageEffect.EFFECT_POSTER, mgsv);
        setEffectButton(R.id.orig_img, ImageEffect.EFFECT_ORIGINAL, mgsv);
//        mgsv.setEffect(ImageEffect.EFFECT_ORIGINAL);

//        mgsv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Log.e("ARBANE", mgsv.getRotation() + "");
//            }
//        });

//        mgsv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Log.e("ARBANE", mgsv.getRotation() + "");
//
//            }
//        });



        findViewById(R.id.b5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rotation = mgsv.getRotation();
                rotation = (rotation + 90);
//                mgsv.setRotation(rotation);
//                mgsv.setEffect(ImageEffect.EFFECT_ROTATE);

                mgsv.animate().scaleXBy(-0.04f).scaleYBy(-0.04f).setDuration(600);
//                mgsv.animate().rotation(rotation).setDuration(1000);
//                mgsv.rotate(rotation, 2000);
            }
        });

    }

    private void setEffectButton(int id, final ImageEffect imageEffect, final OLGLSurfaceView mgsv) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgsv.setEffect(imageEffect);
            }
        });
    }
}

