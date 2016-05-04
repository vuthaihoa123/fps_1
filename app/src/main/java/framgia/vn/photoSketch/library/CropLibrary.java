package framgia.vn.photoSketch.library;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by hoavt on 27/04/2016.
 */

/**
 * Reference: https://github.com/jdamcd/android-crop
 * build.gradle: compile 'com.soundcloud.android:android-crop:1.0.1@aar'
 */

public class CropLibrary {
    private static final String sCachedFileName = "cropped";

    /**
     * Call when beginning for crop image
     * @param activity
     * @param source uri of image that you want to crop
     *
     * @return void
     * */
    public void beginCrop(Activity activity, Uri source) {
        File cachedFile = new File(activity.getCacheDir(), sCachedFileName);
        Uri destination = Uri.fromFile(cachedFile);
        Crop.of(source, destination)    // Create a crop Intent builder with source and destination image Uris
                .asSquare()             // Crop area with fixed 1:1 aspect ratio
                .start(activity);       // Send the crop Intent from an Activity
    }

    /**
     * Call at onActivityForResult in MainActivity when "requestCode == CropLibrary.CROP_REQUEST"
     * @param activity
     * @param cropView : image view that you need to crop
     * @param resultCode: result code returned at onActivityForResult in the activity
     * @param result: intent returned at onActivityForResult in the activity
     *
     * @return void
     * */
    private void handleCrop(Activity activity, ImageView cropView, int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            cropView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
