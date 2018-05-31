package com.example.arbane.surfaceviewtest;

import android.graphics.Bitmap;
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

    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.wall_wall);

        final OLGLSurfaceView mgsv = findViewById(R.id.glsv);
//        float imgWeight = getResources().getDimension(R.dimen.image_width);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)imgWeight, (int)((imgWeight*1080f)/1920f));
//        params.gravity = Gravity.CENTER;
//        mgsv.setLayoutParams(params);
        mgsv.init(new OLSurfaceViewRenderer(bmp,  OLSurfaceViewRenderer.RenderMode.FULL_VIEW_MODE,this, mgsv));

        setEffectButton(R.id.b1, ImageEffect.EFFECT_MONO, mgsv);
        setEffectButton(R.id.b2, ImageEffect.EFFECT_LOMOISH, mgsv);
        setEffectButton(R.id.b3, ImageEffect.EFFECT_SEPIA, mgsv);
        setEffectButton(R.id.b4, ImageEffect.EFFECT_POSTER, mgsv);
        setEffectButton(R.id.b6, ImageEffect.EFFECT_ROTATE, mgsv);
        setEffectButton(R.id.orig_img, ImageEffect.EFFECT_ORIGINAL, mgsv);


        final View v1 = findViewById(R.id.free_view);

        findViewById(R.id.b5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                float rotation = mgsv.getRotation();
//                rotation = (rotation + 90);
//                mgsv.setRotation(rotation);
//                mgsv.setEffect(ImageEffect.EFFECT_ROTATE);

//                mgsv.animate().scaleXBy(-0.04f).scaleYBy(-0.04f).setDuration(600);
//                mgsv.animate().rotation(rotation).setDuration(1000);
                mgsv.rotate(mgsv, 0/*rotation*/, 200);
            }
        });

//        findViewById(R.id.b6).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                a++;
//                if ( a%2==0)
//                    mgsv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wall_wall));
//                else
//                    mgsv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.boy));
//            }
//        });


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

