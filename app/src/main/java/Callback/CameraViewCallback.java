package Callback;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by andredriemeyer on 12/02/2018.
 */

public class CameraViewCallback implements SurfaceHolder.Callback {
    private SurfaceHolder cameraHolder;
    private Camera camera;

    public CameraViewCallback(Context context, Camera camera, SurfaceView sView) {
        this.camera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        cameraHolder = sView.getHolder();
        cameraHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        cameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void updateCamera(Camera camera){
        this.camera = camera;

        cameraHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        if(camera == null){
            return;
        }

        try {
            camera.setPreviewDisplay(cameraHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}