package com.facebook.ShipBob.activities;

import java.util.List;

import com.facebook.ShipBob.R;
import com.shipbob.helpers.Log;
import com.shipbob.vendors.bitmaphandling.CameraUtil;
import com.shipbob.vendors.bitmaphandling.PhotoHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {
    private static final String LOG_TAG = CameraActivity.class.getName();
    private static final String LOG_LINE = "---------------------------------";

    private Camera _camera;
    private boolean _previewIsRunning = false;

    private SurfaceView _svCameraView;
    private SurfaceHolder _surfaceHolder;
    private Button _btnCapture;
    private boolean _chkAutofocus = false;

    long id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getIntent().getLongExtra("recordId", -1);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        _svCameraView = (SurfaceView) findViewById(R.id.svCameraView);

        _surfaceHolder = _svCameraView.getHolder();
        _surfaceHolder.addCallback(this);

/*        _chkAutofocus = (CheckBox)findViewById(R.id.chkAutofocus);
*/
        _btnCapture = (Button) findViewById(R.id.btnCapture);
        _btnCapture.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_camera != null) {
                    //Decide whether or not to use autofocus
                    if (_chkAutofocus) {
                        Log.d(LOG_LINE + "Preparing to take the picture using autofocus...");


                        _camera.autoFocus(new AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Log.d(LOG_LINE + "_camera.autoFocus.onAutoFocus(...) entered.");

                                takePicture();

                                Log.d(LOG_LINE + "_camera.autoFocus.onAutoFocus(...) finished.");
                            }
                        });
                    } else {
                        Log.d(LOG_LINE + "Preparing to take the picture without autofocus...");

                        takePicture();
                    }
                }
            }
        });
    }

    private void takePicture() {
        Log.d(LOG_LINE + "takePicture() entered.");

        _camera.takePicture(_shutterCallback, null, new PhotoHandler(getApplicationContext(), id));
        _previewIsRunning = false;


        Log.d(LOG_LINE + "takePicture() finished.");
    }

    private ShutterCallback _shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
            Log.d(LOG_LINE + "_shutterCallback.onShutter() called.");
        }
    };

    private PictureCallback _jpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(LOG_LINE + "_jpegCallback.onPictureTaken() called.");

            _camera.setDisplayOrientation(90);
            _camera.startPreview();
            _previewIsRunning = true;
        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (_previewIsRunning) {
            Log.d(LOG_LINE + "About to stop preview...");

            _camera.stopPreview();

            Log.d(LOG_LINE + "Stopped preview.");
        }

        try {
            Log.d(LOG_LINE + "About to set up camera parameters...");

            Camera.Parameters parameters = _camera.getParameters();

            //Get the optimal preview size so we don't get an exception when setting the parameters
            List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            Size optimalPreviewSize = CameraUtil.getOptimalPreviewSize(supportedPreviewSizes, width, height);

            parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
            parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);

            _camera.setDisplayOrientation(90);

            _camera.setParameters(parameters);

            _camera.setPreviewDisplay(holder);

            Log.d(LOG_LINE + "Finished setting up camera parameters.");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(ex.toString());
        }

        Log.d(LOG_LINE + "About to start preview...");

        _camera.startPreview();
        _previewIsRunning = true;

        Log.d(LOG_LINE + "Started preview.");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _camera = Camera.open();

        _camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_LINE + "Tearing down camera because surface was destroyed...");

        _camera.stopPreview();
        _previewIsRunning = false;
        _camera.release();

        Log.d(LOG_LINE + "Finished tearing down camera because surface was destroyed.");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_LINE + "About to set request orientation to SCREEN_ORIENTATION_PORTRAIT...");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.d(LOG_LINE + "Successfully set request orientation to SCREEN_ORIENTATION_PORTRAIT.");
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            // no animation of transition
            overridePendingTransition(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
