package com.androidexperiments.shadercam.example;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.androidexperiments.shadercam.example.gl.ExampleRenderer;
import com.androidexperiments.shadercam.example.gl.SuperAwesomeRenderer;
import com.androidexperiments.shadercam.fragments.CameraFragment;
import com.androidexperiments.shadercam.fragments.GraphicOverlay;
import com.androidexperiments.shadercam.fragments.PermissionsHelper;
import com.androidexperiments.shadercam.gl.CameraRenderer;
import com.androidexperiments.shadercam.utils.ShaderUtils;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Written by Anthony Tripaldi
 *
 * Very basic implemention of shader camera.
 */
public class SimpleShaderActivity extends FragmentActivity implements CameraRenderer.OnRendererReadyListener, PermissionsHelper.PermissionsListener, SeekBar.OnSeekBarChangeListener
        {
    private static final String TAG = SimpleShaderActivity.class.getSimpleName();
    private static final String TAG_CAMERA_FRAGMENT = "tag_camera_frag";

    /**
     * filename for our test video output
     */
    private static final String TEST_VIDEO_FILE_NAME = "test_video.mp4";

    /**
     * We inject our views from our layout xml here using {@link ButterKnife}
     */
    @InjectView(R.id.texture_view) TextureView mTextureView;
//    @InjectView(R.id.btn_detect) Button mRecordBtn;
//    @InjectView(R.id.btn_shot) Button mRecordBtn;


    /**
     * Custom fragment used for encapsulating all the {@link android.hardware.camera2} apis.
     */
    private CameraFragment mCameraFragment;

    /**
     * Our custom renderer for this example, which extends {@link CameraRenderer} and then adds custom
     * shaders, which turns shit green, which is easy.
     */
    private CameraRenderer mRenderer;

    /**
     * boolean for triggering restart of camera after completed rendering
     */
    private boolean mRestartCamera = false;

    private PermissionsHelper mPermissionsHelper;
    private boolean mPermissionsSatisfied = false;

    private FaceDetector previewFaceDetector = null;
    private GraphicOverlay mGraphicOverlay;

    private SeekBar mSeekbar;
    private Button btnControlHeight;
    private FrameLayout cameraContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        cameraContainerLayout = (FrameLayout) findViewById(R.id.glviewFrameLayout);

        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        mSeekbar = (SeekBar) findViewById(R.id.seek_bar);
        btnControlHeight = (Button)findViewById(R.id.btn_control_height);

        btnControlHeight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN){
//                    oldXvalue = me.getX();
//                    oldYvalue = me.getY();
//                    Log.i(myTag, "Action Down " + oldXvalue + "," + oldYvalue);
                }else if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
                    params.setMargins(mTextureView.getWidth() - 100,
                            (int) me.getRawY(), 0, 0);
                    v.setLayoutParams(params);

                    ((SuperAwesomeRenderer) mRenderer).setTouchPoint(me.getRawX(), me.getRawY() + 25);

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    lp.leftMargin = 20;
                    lp.topMargin = (int)me.getRawY() + 25;
                    lp.width = mTextureView.getWidth() - 120;
                    lp.height = 50;
                    mSeekbar.setLayoutParams(lp);
                }
                return true;
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
//        int width = mTextureView.getWidth();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.setMargins(width - 100, height / 2 , 0, 0);
        btnControlHeight.setLayoutParams(params);
//
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        lp.leftMargin = 20;
        lp.topMargin = height / 2 + 25;
        lp.width = width - 120;
        lp.height = 50;
        mSeekbar.setLayoutParams(lp);

//        mSeekbar = new SeekBar(this);
//        mSeekbar.setMax(100);
//      seekBar.setIndeterminate(true);

//        ShapeDrawable thumb = new ShapeDrawable(new OvalShape());
//
//        thumb.setIntrinsicHeight(80);
//        thumb.setIntrinsicWidth(30);
//        seekBar.setThumb(thumb);
//        mSeekbar.setProgress(50);
//        mSeekbar.setVisibility(View.VISIBLE);
//        mSeekbar.setBackgroundColor(Color.BLUE);

//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1000, 50);
//        mSeekbar.setLayoutParams(lp);
//        btnShot.setLayoutParams(lp);

        mSeekbar.setOnSeekBarChangeListener(this);
        mSeekbar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));
        mSeekbar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


//        LinearLayout ll = new LinearLayout(this);
//        ll.setOrientation(LinearLayout.VERTICAL);
//
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        layoutParams.setMargins(30, 20, 30, 0);
//
//        SeekBar okButton=new SeekBar(this);
//        okButton.setText("some text");
//        ll.addView(mSeekbar, layoutParams);

        previewFaceDetector = new FaceDetector.Builder(getApplicationContext())
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//                .setMode(FaceDetector.FAST_MODE)
//                .setProminentFaceOnly(true)
//                .setTrackingEnabled(true)
                .build();
        if(previewFaceDetector.isOperational()) {
            previewFaceDetector.setProcessor(new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory()).build());
        } else {
            Toast.makeText(getApplicationContext(), "FACE DETECTION NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        }
        setupCameraFragment();
//        setupInteraction();

        //setup permissions for M or start normally
        if(PermissionsHelper.isMorHigher())
            setupPermissions();

    }

    private void shotAction() {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        switch( audioManager.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Vibrator v = (Vibrator)getApplication().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
                break;
        }

        final LinearLayout takePictureOverlay = new LinearLayout(getApplicationContext());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        takePictureOverlay.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        takePictureOverlay.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.momento_white_transparent));

        cameraContainerLayout.addView(takePictureOverlay);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) takePictureOverlay.getParent()).removeView(takePictureOverlay);
            }
        }, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float strength = 1.0f - (Float.valueOf(progress) / 100.0f);
        float calc = strength + ((1.0f - strength) / 1.5f);
        heightStretchPosition(calc);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //dont need
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //dont need
    }

    private void setupPermissions() {
        mPermissionsHelper = PermissionsHelper.attach(this);
        mPermissionsHelper.setRequestedPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        );
    }

    /**
     * create the camera fragment responsible for handling camera state and add it to our activity
     */
    private void setupCameraFragment()
    {
        if(mCameraFragment != null && mCameraFragment.isAdded())
            return;

        mCameraFragment = CameraFragment.getInstance();
        mCameraFragment.setCameraToUse(CameraFragment.CAMERA_PRIMARY); //pick which camera u want to use, we default to forward
        mCameraFragment.setTextureView(mTextureView);
        mCameraFragment.setDetector(previewFaceDetector);
        mCameraFragment.setFaceOverlayView(mGraphicOverlay);


        //add fragment to our setup and let it work its magic
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(mCameraFragment, TAG_CAMERA_FRAGMENT);
        transaction.commit();
    }

    /**
     * add a listener for touch on our surface view that will pass raw values to our renderer for
     * use in our shader to control color channels.
     */
    private void setupInteraction() {
        mTextureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mRenderer instanceof SuperAwesomeRenderer) {
//                    float ratio = event.getRawY() /

//                    float x = 0.9f;
//                    mRenderer.resetTextureStretch(x);
//                    float x_render = (1.0f - x);
//                    ((SuperAwesomeRenderer) mRenderer).setStretchX(x_render);

                    ((SuperAwesomeRenderer) mRenderer).setTouchPoint(event.getRawX(), event.getRawY());

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    lp.leftMargin = 20;
                    lp.topMargin = (int)event.getRawY();
                    lp.width = 1000;
                    lp.height = 30;

                    mSeekbar.setLayoutParams(lp);

                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    lp2.leftMargin = 1000;
                    lp2.topMargin = (int)event.getRawY();
                    lp2.width = 200;
                    lp2.height = 200;

//                    btnShot.setLayoutParams(lp2);
//                    MeetupActivity.

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Things are good to go and we can continue on as normal. If this is called after a user
     * sees a dialog, then onResume will be called next, allowing the app to continue as normal.
     */
    @Override
    public void onPermissionsSatisfied() {
        Log.d(TAG, "onPermissionsSatisfied()");
        mPermissionsSatisfied = true;
    }

    /**
     * User did not grant the permissions needed for out app, so we show a quick toast and kill the
     * activity before it can continue onward.
     * @param failedPermissions string array of which permissions were denied
     */
    @Override
    public void onPermissionsFailed(String[] failedPermissions) {
        Log.e(TAG, "onPermissionsFailed()" + Arrays.toString(failedPermissions));
        mPermissionsSatisfied = false;
        Toast.makeText(this, "shadercam needs all permissions to function, please try again.", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume()");

        super.onResume();
        ShaderUtils.goFullscreen(this.getWindow());

        /**
         * if we're on M and not satisfied, check for permissions needed
         * {@link PermissionsHelper#checkPermissions()} will also instantly return true if we've
         * checked prior and we have all the correct permissions, allowing us to continue, but if its
         * false, we want to {@code return} here so that the popup will trigger without {@link #setReady(SurfaceTexture, int, int)}
         * being called prematurely
         */
        //
        if(PermissionsHelper.isMorHigher() && !mPermissionsSatisfied) {
            if(!mPermissionsHelper.checkPermissions())
                return;
            else
                mPermissionsSatisfied = true; //extra helper as callback sometimes isnt quick enough for future results
        }

        if(!mTextureView.isAvailable())
            mTextureView.setSurfaceTextureListener(mTextureListener); //set listener to handle when its ready
        else
            setReady(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
    }

    @Override
    protected void onPause() {
        super.onPause();

        shutdownCamera(false);
        mTextureView.setSurfaceTextureListener(null);
    }

    /**
     * {@link ButterKnife} uses annotations to make setting {@link android.view.View.OnClickListener}'s
     * easier than ever with the {@link OnClick} annotation.
     */
//    @OnClick(R.id.btn_record)
//    public void onClickRecord()
//    {
//        if(mRenderer.isRecording())
//            stopRecording();
//        else
//            startRecording();
//    }

    @OnClick(R.id.btn_shot)
    public void onClickRecord()
    {

        shotAction();
        takeShot();
//        if(mRenderer.isRecording())
//            stopRecording();
//        else
//            startRecording();
    }

    /**
     * called whenever surface texture becomes initially available or whenever a camera restarts after
     * completed recording or resuming from onpause
     * @param surface {@link SurfaceTexture} that we'll be drawing into
     * @param width width of the surface texture
     * @param height height of the surface texture
     */
    protected void setReady(SurfaceTexture surface, int width, int height) {
        mRenderer = getRenderer(surface, width, height);
        mRenderer.setCameraFragment(mCameraFragment);
        mRenderer.setOnRendererReadyListener(this);
        mRenderer.start();


        //initial config if needed
        mCameraFragment.configureTransform(width, height);
    }

    /**
     * Override this method for easy usage of stock example setup, allowing for easy
     * recording with any shader.
     */
    protected CameraRenderer getRenderer(SurfaceTexture surface, int width, int height) {
        return new ExampleRenderer(this, surface, width, height);
    }

//    private void takeShot() {
////        mRenderer.takeShot();
//    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "모멘토");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Momento", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public String saveBitmap(Bitmap bmp) {
//        String path = getPath(getApplicationContext());
//        long currentTime = System.currentTimeMillis();
//        String filename = path + "/" + currentTime + ".jpg";
        return saveBitmap(bmp, getOutputMediaFile().toString());
    }

    public static String saveBitmap(Bitmap bmp, String filename) {

        Log.i("Log", "saving Bitmap : " + filename);

        try {
            FileOutputStream fileout = new FileOutputStream(filename);
            BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileout);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bufferOutStream);
            bufferOutStream.flush();
            bufferOutStream.close();
        } catch (IOException e) {
            Log.e("Log", "Err when saving bitmap...");
            e.printStackTrace();
            return null;
        }

        Log.i("Log", "Bitmap " + filename + " saved!");
        return filename;
    }

    protected void heightStretchPosition(float x_val) {
        float x = x_val;
        mRenderer.resetTextureStretch(x);
        float x_render = (1.0f - x);
        ((SuperAwesomeRenderer) mRenderer).setHeightStretchX(x_render);
    }

    protected void setFaceStrength(float x_strength) {
        ((SuperAwesomeRenderer) mRenderer).setFaceStrength(x_strength);
    }



    protected void shoulderRightStretchPosition(float x_val, float y_val) {
//        ((SuperAwesomeRenderer) mRenderer).setShoulderStretchDirection(x_val, y_val);
    }


    private void takeShot()
    {

        mRenderer.takeShot(new CameraRenderer.TakePictureCallback() {
            @Override
            public void takePictureOK(Bitmap bmp) {
                if (bmp != null) {
                    String s = saveBitmap(bmp);
                    bmp.recycle();
//                    showText("Take Shot success!");
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                } else {

                }
//                    showText("Take Shot failed!");
            }



//            @Override
//            public void getJoints(List<JointModel> joints) {
//                ((SuperAwesomeRenderer) mRenderer).setShoulderPointLeft(joints.get(8).getPointX(), joints.get(8).getPointY());
//                ((SuperAwesomeRenderer) mRenderer).setShoulderPointRight(joints.get(9).getPointX(), joints.get(9).getPointY());
//            }
        });
    }

    private void stopRecording()
    {
        mRenderer.stopRecording();
//        mRecordBtn.setText("Record");

        //restart so surface is recreated
        shutdownCamera(true);

        Toast.makeText(this, "File recording complete: " + getVideoFile().getAbsolutePath(), Toast.LENGTH_LONG).show();
    }

    private File getVideoFile()
    {
        return new File(Environment.getExternalStorageDirectory(), TEST_VIDEO_FILE_NAME);
    }

    /**
     * kills the camera in camera fragment and shutsdown render thread
     * @param restart whether or not to restart the camera after shutdown is complete
     */
    private void shutdownCamera(boolean restart)
    {
        //make sure we're here in a working state with proper permissions when we kill the camera
        if(PermissionsHelper.isMorHigher() && !mPermissionsSatisfied) return;

        //check to make sure we've even created the cam and renderer yet
        if(mCameraFragment == null || mRenderer == null) return;

        mCameraFragment.closeCamera();

        mRestartCamera = restart;
        mRenderer.getRenderHandler().sendShutdown();
        mRenderer = null;
    }

    /**
     * Interface overrides from our {@link com.androidexperiments.shadercam.gl.CameraRenderer.OnRendererReadyListener}
     * interface. Since these are being called from inside the CameraRenderer thread, we need to make sure
     * that we call our methods from the {@link #runOnUiThread(Runnable)} method, so that we don't
     * throw any exceptions about touching the UI from non-UI threads.
     *
     * Another way to handle this would be to create a Handler/Message system similar to how our
     * {@link com.androidexperiments.shadercam.gl.CameraRenderer.RenderHandler} works.
     */
    @Override
    public void onRendererReady() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraFragment.setProcessingThread();
                mCameraFragment.setPreviewTexture(mRenderer.getPreviewTexture());
                mCameraFragment.openCamera();
                mCameraFragment.setOverlay();
            }
        });
    }

    @Override
    public void onRendererFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRestartCamera) {
                    setReady(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
                    mRestartCamera = false;
                }
            }
        });
    }


    /**
     * {@link android.view.TextureView.SurfaceTextureListener} responsible for setting up the rest of the
     * rendering and recording elements once our TextureView is good to go.
     */
    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.SurfaceTextureListener()
    {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, final int width, final int height) {
            //convenience method since we're calling it from two places
            setReady(surface, width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            mCameraFragment.configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) { }
    };


    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            ((SuperAwesomeRenderer) mRenderer).setFacePoint((face.getPosition().x + face.getWidth() / 2 ) * 4, (face.getPosition().y + face.getHeight() / 2) * 4);
            ((SuperAwesomeRenderer) mRenderer).setFaceRadius(face.getHeight() * 3);
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            ((SuperAwesomeRenderer) mRenderer).setFacePoint((face.getPosition().x + face.getWidth() / 2 ) * 4, (face.getPosition().y + face.getHeight() / 2) * 4);
            ((SuperAwesomeRenderer) mRenderer).setFaceRadius(face.getHeight() * 3);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }


}
