package framgia.vn.photo_sketch.library;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 13/04/2016.
 */
public class UriLibrary {
    public static Uri getOutputMediaFile(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
