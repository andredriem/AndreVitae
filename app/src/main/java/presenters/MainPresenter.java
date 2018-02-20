package presenters;

import android.content.Intent;
import android.view.View;

import activities.MainActivity;
import activities.cameraActivity;
import android.support.design.widget.Snackbar;


/**
 * Created by andredriemeyer on 20/02/2018.
 */

public class MainPresenter {

    private MainActivity activity;

    public MainPresenter(MainActivity activity){
        this.activity = activity;
    }

    public void launchCameraActivity(){
        Intent i = new Intent(activity.getBaseContext(), cameraActivity.class);
        activity.startActivity(i);
    }

    public void sendEmail(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"aacdriemeyer@inf.ufrgs.br"});
        try {
            activity.startActivity(Intent.createChooser(i, "You\'re hired."));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(view, "No email app installed on this device.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).show();
        }
    }
}
