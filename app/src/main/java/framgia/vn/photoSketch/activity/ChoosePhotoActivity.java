package framgia.vn.photoSketch.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.library.UriLibrary;


public class ChoosePhotoActivity extends AppCompatActivity implements ConstActivity, View.OnClickListener {
    private Animation mAnimation;
    private RelativeLayout mRelativeCamera;
    private RelativeLayout mRelativeGallery;
    private RelativeLayout mStepNumber;
    private boolean mCheckStatus = true;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setSupportActionBar(toolbar);
        getControl();
        setEvents();
    }

    @Override
    protected void onStart() {
        overridePendingTransition(0, 0);
        flyIn();
        super.onStart();
    }

    @Override
    protected void onStop() {
        overridePendingTransition(0, 0);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.relative_camera:
                flyOut();
                displayCamera();
                break;
            case R.id.relative_gallery:
                flyOut();
                displayGallery();
                break;
        }
    }

    private void flyIn() {
        mCheckStatus = true;
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.top_holder_in);
        mRelativeCamera.startAnimation(mAnimation);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_holder_in);
        mRelativeGallery.startAnimation(mAnimation);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.step_number_in);
        mStepNumber.startAnimation(mAnimation);
    }

    private void flyOut() {
        if (!mCheckStatus) return;
        mCheckStatus = true;
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.top_holder_out);
        mRelativeCamera.startAnimation(mAnimation);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_holder_out);
        mRelativeGallery.startAnimation(mAnimation);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.step_number_out);
        mStepNumber.startAnimation(mAnimation);
    }

    private void getControl() {
        mRelativeCamera = (RelativeLayout) findViewById(R.id.relative_camera);
        mRelativeGallery = (RelativeLayout) findViewById(R.id.relative_gallery);
        mStepNumber = (RelativeLayout) findViewById(R.id.step_number);
    }

    private void setEvents() {
        mRelativeCamera.setOnClickListener(this);
        mRelativeGallery.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!(resultCode == RESULT_OK)) {
            Toast.makeText(this, R.string.not_found_photo, Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == REQUEST_CAMERA) displayPhotoActivity();
        else if (requestCode == REQUEST_GALLERY) {
            mUri = data.getData();
            displayPhotoActivity();
        }
    }

    private void displayPhotoActivity() {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.setData(mUri);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void displayCamera() {
        mUri = UriLibrary.getOutputMediaFile(getContentResolver());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void displayGallery() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent();
            intent.setType(TYPE_INPUT);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }
}
