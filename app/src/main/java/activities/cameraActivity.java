package activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.andredriemeyer.andrevitae.R;

import presenters.CameraPresenter;

public class cameraActivity extends AppCompatActivity {

    private CameraPresenter cameraPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraPresenter = new CameraPresenter(this);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }


    @Override
    protected void onResume(){
        super.onResume();
        cameraPresenter.restartCamera();
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraPresenter.releaseCamera();
    }

    public void photoButtonOnClick(View view){
        cameraPresenter.takePhoto();
    }


    public void galleryPreviewViewOnClick(View view) {
        cameraPresenter.openGallery();
    }
}
