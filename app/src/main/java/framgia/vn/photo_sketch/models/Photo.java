package framgia.vn.photo_sketch.models;

import android.graphics.Bitmap;

/**
 * Created by hoada921 on 2016-04-25.
 */
public class Photo {
    private String mUri;
    private Bitmap mBitmap;

    public Photo(String uri) {
        mUri = uri;
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public String getUri() {
        return mUri;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
