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
    private float mHeightStretch_x_strength_input = 0.1f;
    private float mHeightStretch_x_strength = 0.05f;
    private float mHeightStretch_x_point = 0.5f;

    private int mSurfaceWidth, mSurfaceHeight;
    private float mFace_x = -300.0f, mFace_y = -300.0f;
    private float mRadiusFace = 300.0f;
    private float mStrengthFace = 1.1f;


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

        int heightStretchStrengthHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "stretch_x_strenth");
        GLES20.glUniform1f(heightStretchStrengthHandle, mHeightStretch_x_strength);

        int heightStretchPointHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "stretch_x_point");
        GLES20.glUniform1f(heightStretchPointHandle, mHeightStretch_x_point);

        int facePointHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "point_face");
        GLES20.glUniform2f(facePointHandle, mFace_y, mFace_x);

        int radiusFaceHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "radius_face");
        GLES20.glUniform1f(radiusFaceHandle, mRadiusFace);

        int strengthFaceHandle = GLES20.glGetUniformLocation(mCameraShaderProgram, "strength_face");
        GLES20.glUniform1f(strengthFaceHandle, mStrengthFace);
    }

    public void setHeightStretchX(float x) {
        mHeightStretch_x_strength_input = x;
        this.mHeightStretch_x_strength = this.mHeightStretch_x_strength_input * (1 - this.mHeightStretch_x_point);
    }

    public void setFaceStrength(float x) {
        this.mStrengthFace = x;
    }

    public void setFaceRadius(float radius) {
        this.mRadiusFace = radius;
    }

    public void setFacePoint(float x, float y) {
        this.mFace_x =  mSurfaceWidth - x;
        this.mFace_y =  y;
    }

    public void setTouchPoint(float rawX, float rawY)
    {
        this.mHeightStretch_x_point = (rawY / mSurfaceHeight);
        this.mHeightStretch_x_strength = this.mHeightStretch_x_strength_input * (1 - this.mHeightStretch_x_point);
    }
}
