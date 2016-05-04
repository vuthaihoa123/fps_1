package framgia.vn.photoSketch.asynctask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;

public class DisplayBitmapAsync extends AsyncTask<String, Void, Bitmap> {
    private DisplayMetrics mMetrics;
    private Activity mContext;
    private ImageView mImageView;

    //    BitmapLoader bitmapLoader;
    public DisplayBitmapAsync(Activity context) {
        this.mContext = context;
        this.mMetrics = context.getResources().getDisplayMetrics();
        mImageView = (ImageView) context.findViewById(R.id.source_image);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapUtil.resize(params[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null)
            mImageView.setImageBitmap(bitmap);
        super.onPostExecute(bitmap);
    }
}
