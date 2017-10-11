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
public class MeetupActivity extends SimpleShaderActivity implements SeekBar.OnSeekBarChangeListener {
    private SuperAwesomeRenderer mMyRenderer;
    private ExampleRenderer mExampleRenderer;

    private SeekBar mSeekbar;
    private Button mBtnDetector, mBtnEdit;
    private ImageView mShapeHeight, mShapeShoulder, mShapeChest, mShapeFace;
    private LinearLayout mWrapperShapes;
    protected int mState;
    //0 - idle, 1 = height, 2 = face, 3 = shoulder, 4 = chest


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSeekbar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekbar.setOnSeekBarChangeListener(this);
        mSeekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        mSeekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);



        mShapeHeight = (ImageView)findViewById(R.id.view_shape_height);
        mShapeShoulder = (ImageView)findViewById(R.id.view_shape_shoulder);
        mShapeChest = (ImageView)findViewById(R.id.view_shape_chest);
        mShapeFace = (ImageView)findViewById(R.id.view_shape_face);

        mWrapperShapes = (LinearLayout)findViewById(R.id.shape_list);

        mBtnDetector = (Button)findViewById(R.id.btn_detect);
        mBtnEdit = (Button)findViewById(R.id.btn_edit);

        mShapeHeight.setOnClickListener(mGlobalOnClickListener);
        mShapeShoulder.setOnClickListener(mGlobalOnClickListener);
        mShapeChest.setOnClickListener(mGlobalOnClickListener);
        mShapeFace.setOnClickListener(mGlobalOnClickListener);
//        mBtnDetector.setOnClickListener(mGlobalOnClickListener);
        mBtnEdit.setOnClickListener(mGlobalOnClickListener);


        mState = Sharedpreference.getState(getApplicationContext());
    }

    //Global On click listener for all views
    final View.OnClickListener mGlobalOnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.view_shape_height:
                    mState = 1;
                    Toast.makeText(getApplicationContext(), "키 늘리기", Toast.LENGTH_SHORT).show();
                    Sharedpreference.setState(getApplicationContext(), mState);
                    break;
                case R.id.view_shape_face:
                    mState = 2;
                    Toast.makeText(getApplicationContext(), "얼굴 사이즈", Toast.LENGTH_SHORT).show();
                    Sharedpreference.setState(getApplicationContext(), mState);
                    break;
                case R.id.view_shape_shoulder:
                    mState = 2;
                    Sharedpreference.setState(getApplicationContext(), mState);
                    break;
                case R.id.view_shape_chest:
                    mState = 3;
                    Sharedpreference.setState(getApplicationContext(), mState);
                    break;
//                case R.id.btn_detect:
//
//                    break;
                case R.id.btn_edit:
                    toggleShapeList();
                    break;
            }
        }
    };

    private void toggleShapeList() {
        if(mWrapperShapes.getVisibility() == View.VISIBLE) {
            mWrapperShapes.setVisibility(View.GONE);
            mSeekbar.setVisibility(View.GONE);
        } else {
            mWrapperShapes.setVisibility(View.VISIBLE);
            mSeekbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected CameraRenderer getRenderer(SurfaceTexture surface, int width, int height) {
        mMyRenderer = new SuperAwesomeRenderer(this, surface, width, height);
        return mMyRenderer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        mMyRenderer.setTileAmount(map(progress, 0.f, 100.f, 0.1f, 1.9f));

        switch(mState) {
            case 1:
                float strength = 1.0f - (Float.valueOf(progress) / 100.0f);
                float calc = strength + ((1.0f - strength) / 1.5f);
                heightStretchPosition(calc);
                break;
            case 2:
//                float strength = Float.valueOf(progress)
//                shoulderRightStretchPosition(0.0f, Float.valueOf(progress) / 2);

//                float strength_face = 0.5f * (Float.valueOf(progress) / 100.0f);
//                setFaceStrength(strength_face);
                break;
            case 3:

                break;
        }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //dont need
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //dont need
    }

    /**
     * Takes a value, assumes it falls between start1 and stop1, and maps it to a value
     * between start2 and stop2.
     *
     * For example, above, our slide goes 0-100, starting at 50. We map 0 on the slider
     * to .1f and 100 to 1.9f, in order to better suit our shader calculations
     */
    float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }





}
