package framgia.vn.photoSketch.models;

import android.net.Uri;

/**
 * Created by hoavt on 05/05/2016.
 */
public class Video {
    private String mPath = null;
    private Uri mUri = null;

    public Video(String path) {
        mPath = path;
    }

    public Video(Uri uri) {
        mUri = uri;
    }

    public String getPath() {
        return mPath;
    }

    public Uri getUri() {
        return mUri;
    }
}
