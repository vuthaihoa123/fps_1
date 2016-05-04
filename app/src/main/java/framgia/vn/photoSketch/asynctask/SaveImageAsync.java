package framgia.vn.photoSketch.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 20/04/2016.
 */
public class SaveImageAsync extends AsyncTask<Bitmap, Void, Boolean> {
    private Activity mContext;
    private ProgressDialog mDialog;

    public SaveImageAsync(Activity context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage(mContext.getResources().getString(R.string.progress_message_wait_save_image));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Bitmap... params) {
        try {
            BitmapUtil.saveBitmapToSdcard(params[0]);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean value) {
        mDialog.dismiss();
        if (value) Toast.makeText(mContext, R.string.toast_save_image_success, Toast.LENGTH_LONG).show();
        else Toast.makeText(mContext, R.string.toast_save_image_error, Toast.LENGTH_LONG).show();
        super.onPostExecute(value);
    }
}
