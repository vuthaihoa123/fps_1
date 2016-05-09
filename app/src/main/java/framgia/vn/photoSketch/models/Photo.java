package framgia.vn.photoSketch.models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by hoada921 on 2016-04-25.
 * Created by nghicv on 23/04/2016.
 */
public class Photo implements Serializable {
    private String mUri;
    private Bitmap mBitmap;
    private boolean mSelected = false;
    private int mEffect = 0;
    public Photo() {

    }

    public void setEffect(int effect) {
        mEffect = effect;
    }

    public int getEffect() {
        return mEffect;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

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
