package presenters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.example.andredriemeyer.andrevitae.R;
import activities.cameraActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

import Callback.CameraViewCallback;

import static android.content.ContentValues.TAG;

/**
 * Created by andredriemeyer on 06/02/2018.
 */

public class CameraPresenter{

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private cameraActivity activity;
    private Camera camera;

    private SurfaceView cameraView;
    private SurfaceHolder cameraHolder;
    private Camera.PictureCallback pictureCallback;
    private CameraViewCallback callback;

    private ImageView galleryPreviewView;


    public CameraPresenter(cameraActivity activity) throws MissingResourceException{

        this.activity = activity;
        this.camera = null;
        cameraView = activity.findViewById(R.id.cameraView);
        galleryPreviewView = activity.findViewById(R.id.galleryPreviewView);

        if(!checkPermissions()){
            activity.finish();
            return;
        }

        if (!checkCameraHardware(activity.getBaseContext())) {
            throw new MissingResourceException("There is no camera Hardware", getClass().getName(), "");
        }

        startCamera();

        updateGalleryPreview();
    }

    private void updateGalleryPreview() {
        final Cursor cursor = getLastGalleryPicturesByDescendingOrder();
        if(cursor.moveToFirst()){
            String imageLocaton = cursor.getString(1);

            File imageFile = new File(imageLocaton);
            if(imageFile.exists()){
                galleryPreviewView.setImageBitmap(BitmapFactory.decodeFile(imageLocaton));
            }


        }

        cursor.close();
    }

    private boolean checkPermissions(){

        List<String> permissionsList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.CAMERA);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        if(!permissionsList.isEmpty()){
            ActivityCompat.requestPermissions(activity,permissionsList.toArray(new String[permissionsList.size()]),1);
            return false;
        }

        return true;
    }

    private void startCamera(){
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        pictureCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                System.out.println(pictureFile);

                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions: ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    ImageView updateblaview = activity.findViewById(R.id.galleryPreviewView);
                    updateblaview.setImageBitmap(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()));
                    galleryAddPic(pictureFile.toString());

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
                catch (Exception e){
                    Log.d(TAG, "Unexpected Error accessing file: " + e.getMessage());
                }

                camera.startPreview();
            }
        };

        callback = new CameraViewCallback(activity.getBaseContext(), camera, cameraView);

        cameraHolder = cameraView.getHolder();
        cameraHolder.addCallback(callback);
    }

    public void restartCamera(){
        if(camera == null) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            callback.updateCamera(camera);
        }
    }


    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
        if(camera == null){
            System.out.println("here");
        }
    }

    public void takePhoto(){
        camera.takePicture(null,null, pictureCallback);
    }

    public void changePreviewPhoto(){

    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private Cursor getLastGalleryPicturesByDescendingOrder(){
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        return galleryPreviewView.getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
    }

    public void openGallery() {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        activity.startActivity(i);
    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println(path);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

}
