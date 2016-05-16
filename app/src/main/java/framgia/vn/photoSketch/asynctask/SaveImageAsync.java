package framgia.vn.photoSketch.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;
import framgia.vn.photoSketch.constants.AppConstant;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 20/04/2016.
 */
public class SaveImageAsync extends AsyncTask<Bitmap, Void, String> {
    private Activity mContext;
    private ProgressDialog mDialog;
    private Photo mPhoto;

    public SaveImageAsync(Activity context, Photo photo) {
        this.mContext = context;
        mPhoto = photo;
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
    protected String doInBackground(Bitmap... params) {
        try {
            return BitmapUtil.saveBitmapToSdcard(params[0]);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String value) {
        super.onPostExecute(value);
        mDialog.dismiss();
        if (value != null) {
            Toast.makeText(mContext, R.string.toast_save_image_success, Toast.LENGTH_LONG).show();
            if (mPhoto != null) {
                mPhoto.setUri(value);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConstant.PHOTO, mPhoto);
                intent.putExtras(bundle);
                mContext.setResult(Activity.RESULT_OK, intent);
                mContext.finish();
            }
            return;
        }
        Toast.makeText(mContext, R.string.toast_save_image_error, Toast.LENGTH_LONG).show();
    }
}
