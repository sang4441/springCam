package com.androidexperiments.shadercam.example;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.androidexperiments.shadercam.example.gl.ExampleRenderer;
import com.androidexperiments.shadercam.example.gl.SuperAwesomeRenderer;
import com.androidexperiments.shadercam.gl.CameraRenderer;


/**
 * For our NYC Android Developers Meetup, we've created a super simple
 * implementation of ShaderCam, with sliders
 */
public class FaceHeightActivity extends SimpleShaderActivity {
    private SuperAwesomeRenderer mMyRenderer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected CameraRenderer getRenderer(SurfaceTexture surface, int width, int height) {
        mMyRenderer = new SuperAwesomeRenderer(this, surface, width, height);
        return mMyRenderer;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //dont need
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //dont need
    }
}
