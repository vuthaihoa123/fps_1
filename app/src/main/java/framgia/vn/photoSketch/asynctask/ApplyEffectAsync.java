package framgia.vn.photoSketch.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;
import framgia.vn.photoSketch.constants.ConstEffects;
import framgia.vn.photoSketch.models.Effect;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 19/04/2016.
 */
public class ApplyEffectAsync extends AsyncTask<Effect, Void, Bitmap> implements ConstEffects {
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
        mDialog.setMessage(mContext.getResources().getString(R.string.progress_message_wait_apply_effect));
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
                bitmap = BitmapUtil.invert(mBitmap);
                break;
            case FILTER_SKETCH:
                bitmap = BitmapUtil.sketch(mBitmap);
                break;
            case FILTER_VIGNETTE:
                bitmap = BitmapUtil.vignette(mBitmap);
                break;
            case FILTER_SEPIA:
                float valueSepia = (float) value / 100;
                bitmap = BitmapUtil.sepia(mBitmap, valueSepia);
                break;
            case FILTER_GREY_SCALE:
                float valueGreyScale = (float) value / 100;
                bitmap = BitmapUtil.greyScale(mBitmap, valueGreyScale);
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
        else Toast.makeText(mContext, R.string.toast_error_apply_effect, Toast.LENGTH_LONG).show();
        super.onPostExecute(bitmap);
    }
}
