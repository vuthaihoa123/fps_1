package framgia.vn.photoSketch.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by hoavt on 27/04/2016.
 */

/**
 * Reference: https://github.com/jdamcd/android-crop
 * build.gradle: compile 'com.soundcloud.android:android-crop:1.0.1@aar'
 */

public class CropLibrary {
    public static final String NAME_OF_IMAGE = "Title";
    private int mQualityImage = 100;

    /**
     * Call when beginning for crop image
     *
     * @param activity
     * @param source   uri of image that you want to crop
     * @return void
     */
    public void beginCrop(Activity activity, Uri source) {
//        File cachedFile = new File(activity.getCacheDir(), sCachedFileName);
//        Uri destination = Uri.fromFile(cachedFile);
        CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(activity);
//        Crop.of(source, destination)    // Create a crop Intent builder with source and destination image Uris
//                .asSquare()             // Crop area with fixed 1:1 aspect ratio
//                .start(activity);       // Send the crop Intent from an Activity
    }

    public Bitmap uriToBmp(Activity activity, Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
    }

    public Uri bmpToUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, mQualityImage, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, NAME_OF_IMAGE, null);
        return Uri.parse(path);
    }

    /**
     * Call at onActivityForResult in MainActivity when "requestCode == CropLibrary.CROP_REQUEST"
     *
     * @param activity
     * @param cropView    : image view that you need to crop
     * @param resultCode: result code returned at onActivityForResult in the activity
     * @param data:       intent returned at onActivityForResult in the activity
     * @return void
     */
    public void handleCrop(Activity activity, ImageView cropView, int resultCode, Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == Activity.RESULT_OK) {
            Uri resultUri = result.getUri();
            cropView.setImageURI(resultUri);
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Exception error = result.getError();
            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
