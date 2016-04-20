package framgia.vn.photo_sketch.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import framgia.vn.photo_sketch.R;
import framgia.vn.photo_sketch.bitmaputil.BitmapUtil;
import framgia.vn.photo_sketch.constants.ConstEffects;
import framgia.vn.photo_sketch.constants.ConstNotification;
import framgia.vn.photo_sketch.models.Effect;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 19/04/2016.
 */
public class ApplyEffectAsync extends AsyncTask<Effect, Void, Bitmap> implements ConstEffects, ConstNotification {
    private Activity mContext;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private ProgressDialog mDialog;

    public ApplyEffectAsync(Activity context, Bitmap bitmap) {
        this.mContext = context;
        this.mBitmap = bitmap;
        mImageView = (ImageView) mContext.findViewById(R.id.source_image);
    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(WAIT_APPLY_EFFECT);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Effect... params) {
        Bitmap bitmap = null;
        int value = params[0].getValue();
        switch (params[0].getName()) {
            case FILTER_HUE:
                bitmap = BitmapUtil.hue(mBitmap, value);
                break;
            case FILTER_BRIGHT:
                bitmap = BitmapUtil.brightness(mBitmap, value);
                break;
            case FILTER_CONTRAST:
                bitmap = BitmapUtil.contrast(mBitmap, value);
                break;
            case FILTER_HIGHLIGHT:
                bitmap = BitmapUtil.highlight(mBitmap, value);
                break;
            case FILTER_INVERT:
                bitmap = BitmapUtil.invert(mBitmap, value);
                break;
            case FILTER_SKETCH:
                bitmap = BitmapUtil.sketch(mBitmap);
                break;
            case FILTER_VIGNETTE:
                bitmap = BitmapUtil.vignette(mBitmap);
                break;
            case FILTER_SEPIA:
                bitmap = BitmapUtil.sepia(mBitmap);
                break;
            case FILTER_GREY_SCALE:
                bitmap = BitmapUtil.greyScale(mBitmap);
                break;
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mDialog.dismiss();
        if (bitmap != null) mImageView.setImageBitmap(bitmap);
        else Toast.makeText(mContext, ERROR_APPLY_EFFECT, Toast.LENGTH_LONG).show();
        super.onPostExecute(bitmap);
    }
}
