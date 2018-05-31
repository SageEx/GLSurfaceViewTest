package com.example.arbane.surfaceviewtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OLSurfaceViewRenderer implements GLSurfaceView.Renderer {

    private OLGLSurfaceView mGLSurfaceView;
    private final int[] texture = new int[2];
    private float vertices[];
    private Bitmap mBitmap;
    private Context mContext;
    private float w, h;
    private EffectContext effectContext;
    private int viewWidth, viewHeight;
    private long time;
    public volatile boolean isAnimationActive = false;
    Random rand = new Random(713);

    long startTime = 0L;
    long elapsedTime = 0L;


    // create a model matrix for the triangle
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] scratch = new float[16];

    private float[] mRotationMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];
    private int mMVPMatrixHandle = 0;


    private FloatBuffer verticesBuffer;
    private FloatBuffer textureBuffer;
    private int vertexShader;
    private int fragmentShader;
    private int program;
    private float textureVertices[] = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private static final String vertexShaderCode =
            "attribute vec4 aPosition;" +
            "attribute vec2 aTexPosition;" +
            "uniform mat4 uMVPMatrix;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "  vTexPosition = aTexPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D uTexture;" +
            "varying vec2 vTexPosition;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, vTexPosition);" +
            "}";

    private ImageEffect mImageEffect = null;
    private RenderMode mRenderMode;

    public OLSurfaceViewRenderer(Bitmap bitmap, RenderMode renderMode, Context context, OLGLSurfaceView olglSurfaceView) {
        mBitmap = bitmap;
        mRenderMode = renderMode;
        mContext = context;
        mGLSurfaceView = olglSurfaceView;
        mImageEffect = ImageEffect.EFFECT_ORIGINAL;
    }

    private void setDimens(RenderMode renderMode) {
        int w_ = mBitmap.getWidth();
        int h_ = mBitmap.getHeight();
        float limit = 2f;
        switch (renderMode) {
            case FULL_VIEW_MODE:
                w = limit;
                h = (limit * h_ * viewWidth) / (viewHeight * w_);
                if (h > limit) {
                    h = limit;
                    w = (limit * w_ * viewHeight) / (viewWidth * h_);
                }
//                w = limit;
//                h = (limit * viewHeight) / viewHeight;
                break;
            case THUMB_VIEW_MODE:
                viewWidth = dpToPx(mContext, 56);
                viewHeight = viewWidth;
                if (w_ > h_) {
                    h = limit;
                    w = (limit * w_ * viewHeight) / (viewWidth * h_);
                } else {
                    w = limit;
                    h = (limit * h_ * viewWidth) / (viewHeight * w_);
                }
                break;
            case SAVE_MODE:
                viewWidth = w_;
                viewHeight = h_;
                w = limit;
                h = limit;
                break;
        }
    }

    // TODO: replace this with commonUtils one
    private int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round((float) dp * ((float) displayMetrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0f, 0f, 0f, 255f);
        Log.e("ARBANE", "Hi");
        viewWidth = width;
        viewHeight = height;

        float ratio = (float) width / height;
//        ratio = 1;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);


        setDimens(mRenderMode);

        generateCoordinates();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        if (effectContext == null) {
            effectContext = EffectContext.createWithCurrentGlContext();
        }
        applyEffect();
//        Log.e("ARBANE", mGLSurfaceView.getRotation() + "");

//        Log.e("ARBANE", "DRAW");
        draw(mImageEffect == null || mImageEffect == ImageEffect.EFFECT_ORIGINAL ? texture[0] : texture[1]);
    }

    private void draw(int texture) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glUseProgram(program);
        GLES20.glDisable(GLES20.GL_BLEND);



        // Matrix manipulations ...
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 6, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//        Matrix.setIdentityM(mMVPMatrix, 0);
//        Matrix.rotateM(mMVPMatrix, 0, 0, 0f, 0f, 1f);


        boolean animationActive = isAnimationActive;

        if (animationActive ) {
            long time = SystemClock.uptimeMillis() % 4000L;
            float angle = 0.090f * ((int) time);

            Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            drawTexture(scratch, texture);
        } else {
            drawTexture(mMVPMatrix, texture);
        }

    }

    private void drawTexture(float[] matrix, int texture) {
        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");
        int texturePositionHandle = GLES20.glGetAttribLocation(program, "aTexPosition");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        checkGlError("SC", "handleGet");

        // from here, we just draw w.r.t matrixes
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);


        GLES20.glVertexAttribPointer(texturePositionHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(texturePositionHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void applyEffect() {
        if (mImageEffect == ImageEffect.EFFECT_ORIGINAL)
            return;
        EffectFactory factory = effectContext.getFactory();
        Effect effect = factory.createEffect(EffectFactory.EFFECT_AUTOFIX);
        switch (mImageEffect) {
            case EFFECT_MONO:
                effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
                break;
            case EFFECT_SEPIA:
                effect = factory.createEffect(EffectFactory.EFFECT_SEPIA);
                break;
            case EFFECT_LOMOISH:
                effect = factory.createEffect(EffectFactory.EFFECT_LOMOISH);
                break;
            case EFFECT_POSTER:
                effect = factory.createEffect(EffectFactory.EFFECT_POSTERIZE);
                break;
            case EFFECT_ROTATE:
                effect = factory.createEffect(EffectFactory.EFFECT_ROTATE);
                int r = rand.nextInt(4);
                effect.setParameter("angle", 90*r);
                break;
//                effect.setParameter("angle", 90);
        }
        if (effect != null) {
            try {
                effect.apply(texture[0], viewWidth, viewHeight, texture[1]);
                checkGlError("Effect", "Writing to texture 1");
            } catch (Exception e) {
            }
        }
    }

    public void setImageEffect(ImageEffect imageEffect) {
        mImageEffect = imageEffect;
    }

    public void setImageBitmap(Bitmap bmp) {
        mBitmap = bmp;
        generateCoordinates();
    }

    private void generateCoordinates() {
        if (vertices == null) {
            GLES20.glGenTextures(2, texture, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // I don't know what this line does. But this really helped me.
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0); // TODO: revert
        }

        vertices = new float[8];
        vertices[0] = -w / 2f;
        vertices[1] = -h / 2f;
        vertices[2] = w / 2f;
        vertices[3] = -h / 2f;
        vertices[4] = -w / 2f;
        vertices[5] = h / 2f;
        vertices[6] = w / 2f;
        vertices[7] = h / 2f;
        initializeBuffers();
        initializeProgram();
    }

    private void initializeBuffers() {
        ByteBuffer buff = ByteBuffer.allocateDirect(vertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        verticesBuffer = buff.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);

        buff = ByteBuffer.allocateDirect(textureVertices.length * 4);
        buff.order(ByteOrder.nativeOrder());
        textureBuffer = buff.asFloatBuffer();
        textureBuffer.put(textureVertices);
        textureBuffer.position(0);
    }

    public void initializeProgram() {
        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        checkGlError("InitializeP","createVertexShader");

        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        checkGlError("InitializeP","makeVertexShader");

        GLES20.glCompileShader(vertexShader);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        program = GLES20.glCreateProgram();
        checkGlError("InitializeP","createProgram");
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("InitializeP","vertexShader");

        GLES20.glAttachShader(program, fragmentShader);
        checkGlError("InitializeP","fragShader");

        GLES20.glLinkProgram(program);
    }

    public enum RenderMode {
        FULL_VIEW_MODE,
        THUMB_VIEW_MODE,
        SAVE_MODE
    }

    protected void checkGlError(String TAG, String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
