package com.androidexperiments.shadercam.example.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import com.androidexperiments.shadercam.gl.CameraRenderer;

/**
 * Our super awesome shader. It calls its super constructor with the new
 * glsl files we've created for this. Then it overrides {@link #setUniformsAndAttribs()}
 * to pass in our global time uniform
 */
public class SuperAwesomeRenderer extends CameraRenderer {
    private float mHeightStretch_x_strength_input = 0.2f;
    private float mHeightStretch_x_strength = 0.1f;
    private float mHeightStretch_x_point = 0.5f;

    private int mSurfaceWidth, mSurfaceHeight;

    private float point_shoulder_left_x = 0.0f, point_shoulder_left_y = 0.0f;
    private float point_shoulder_right_x = 0.0f, point_shoulder_right_y = 0.0f;
    private float mChest_left_x = 0.0f, mChest_left_y = 0.0f;
    private float mChest_right_x = 0.0f, mChest_right_y = 0.0f;
    private float mFace_x = 500.0f, mFace_y = 500.0f;

    private float mRadiusShoulder = 70.0f;
    private float mRadiusChest = 80.0f;
    private float mRadiusFace = 300.0f;

    private float mStrengthShoulder = 0.0f;
    private float mStrengthChest = 0.0f;
    private float mStrengthFace = 0.3f;


    public SuperAwesomeRenderer(Context context, SurfaceTexture texture, int width, int height) {
        super(context, texture, width, height, "superawesome.frag.glsl", "superawesome.vert.glsl");
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    protected void setUniformsAndAttribs() {
        //always call super so that the built-in fun stuff can be set first
        super.setUniformsAndAttribs();

        int surfaceWidthHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "width");
        GLES20.glUniform1f(surfaceWidthHandle, mSurfaceWidth);

        int surfaceHeightHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "height");
        GLES20.glUniform1f(surfaceHeightHandle, mSurfaceHeight);


        int globalTimeHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "iGlobalTime");
        GLES20.glUniform1f(globalTimeHandle, SystemClock.currentThreadTimeMillis() / 100.0f);

//        int resolutionHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "iResolution");
//        GLES20.glUniform3f(resolutionHandle, mTileAmount, mTileAmount, 1.f);

        int heightStretchStrengthHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "stretch_x_strenth");
        GLES20.glUniform1f(heightStretchStrengthHandle, mHeightStretch_x_strength);

        int heightStretchPointHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "stretch_x_point");
        GLES20.glUniform1f(heightStretchPointHandle, mHeightStretch_x_point);


        int shoulderPointLeftHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_shoulder_left");
        GLES20.glUniform2f(shoulderPointLeftHandle, point_shoulder_left_y, point_shoulder_left_x);

        int shoulderPointRightHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_shoulder_right");
        GLES20.glUniform2f(shoulderPointRightHandle, point_shoulder_right_y, point_shoulder_right_x);

        int chestPointLeftHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_chest_left");
        GLES20.glUniform2f(chestPointLeftHandle, mChest_left_y, mChest_left_x);

        int chestPointRightHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_chest_right");
        GLES20.glUniform2f(chestPointRightHandle, mChest_right_y, mChest_right_x);

        int facePointHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_face");
        GLES20.glUniform2f(facePointHandle, mFace_y, mFace_x);

        int radiusShoulderHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "radius_shoulder");
        GLES20.glUniform1f(radiusShoulderHandle, mRadiusShoulder);

        int radiusChestHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "radius_chest");
        GLES20.glUniform1f(radiusChestHandle, mRadiusChest);

        int radiusFaceHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "radius_face");
        GLES20.glUniform1f(radiusFaceHandle, mRadiusFace);

        int strengthShoulderHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "strength_shoulder");
        GLES20.glUniform1f(strengthShoulderHandle, mStrengthShoulder);

        int strengthChestHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "strength_chest");
        GLES20.glUniform1f(strengthChestHandle, mStrengthChest);

        int strengthFaceHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "strength_face");
        GLES20.glUniform1f(strengthFaceHandle, mStrengthFace);

    }

//    public void setTileAmount(float tileAmount) {
//        this.mTileAmount = tileAmount;
//    }

    public void setHeightStretchX(float x) {
        mHeightStretch_x_strength_input = x;
        this.mHeightStretch_x_strength = this.mHeightStretch_x_strength_input * (1 - this.mHeightStretch_x_point);
    }

    public void setFaceStrength(float x) {
        this.mStrengthFace = x;
    }

//    public void setShoulderStretchDirection(float x, float y) {
//        this.mShoulder_x_destination =  x;
//        this.mShoulder_y_destination =  y;
//    }

    public void setShoulderPointLeft(float x, float y) {
//        this.point_shoulder_left_x =  x;
//        this.point_shoulder_left_y =  y;
    }

    public void setShoulderPointRight(float x, float y) {
//        this.point_shoulder_right_x =  x;
//        this.point_shoulder_right_y =  y;
    }

//    public void setChestPointLeft(float x, float y) {
//        this.mShoulder_right_x =  x;
//        this.mShoulder_right_y =  y;
//    }
//
//    public void setChestPointRight(float x, float y) {
//        this.mShoulder_right_x =  x;
//        this.mShoulder_right_y =  y;
//    }

    public void setFaceRadius(float radius) {
        this.mRadiusFace = radius;
    }

    public void setFacePoint(float x, float y) {
        this.mFace_x =  mSurfaceWidth - x;
        this.mFace_y =  y;

//        this.mChest_left_x = x - 50;
//        this.mChest_left_y = y + 250;
//
//        this.mChest_right_x = x + 50;
//        this.mChest_right_y = y + 150;
    }



    public void setTouchPoint(float rawX, float rawY, int state)
    {
        switch (state) {
            case 1:
                this.mHeightStretch_x_point = (rawY / mSurfaceHeight);
                this.mHeightStretch_x_strength = this.mHeightStretch_x_strength_input * (1 - this.mHeightStretch_x_point);
                break;
            case 2:
//                this.mShoulder_x = mSurfaceWidth - rawX;
//                this.mShoulder_y = rawY;
                break;
            case 3:

                break;
        }
    }
}
