package framgia.vn.photoSketch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ListPhotoMakeVideoAdapter;
import framgia.vn.photoSketch.bitmaputil.VideoUtil;
import framgia.vn.photoSketch.constants.AppConstant;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.library.DialogUtils;
import framgia.vn.photoSketch.library.ItemTouchHelperCallback;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 02/05/2016.
 */
public class MakeVideoActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar mToolbar;
    private RecyclerView mRecyclerViewListPhoto;
    private RelativeLayout mRelativeLayoutTool;
    private ImageView mImageViewScale;
    private ImageView mImageViewTranslate;
    private ImageView mImageViewRotate;
    private ImageView mImageViewPhoto;
    private ImageView mImageEditPhoto;
    private List<Photo> mPhotos = new ArrayList<>();
    private int mCurrentIndex = -1;
    private ListPhotoMakeVideoAdapter mAdapter;
    
    public static final int REQUEST_CODE_SELECT_PHOTO = 1;
    public static final int REQUEST_CODE_EDIT_PHOTO = 2;
    public static final int MAX_SIZE = 100;
    public static final String VIDEO_TYPE = "video/mp4";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_video);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerViewListPhoto = (RecyclerView) findViewById(R.id.recycler_image);
        mRelativeLayoutTool = (RelativeLayout) findViewById(R.id.recycler_tool_edit);
        mImageViewPhoto = (ImageView) findViewById(R.id.image_make_video);
        mImageViewScale = (ImageView) findViewById(R.id.image_scale);
        mImageViewTranslate = (ImageView) findViewById(R.id.image_translate);
        mImageViewRotate = (ImageView) findViewById(R.id.image_rotate);
        mImageEditPhoto = (ImageView) findViewById(R.id.image_edit_photo);
        mImageViewScale.setOnClickListener(this);
        mImageViewTranslate.setOnClickListener(this);
        mImageViewRotate.setOnClickListener(this);
        mImageEditPhoto.setOnClickListener(this);
        mAdapter = new ListPhotoMakeVideoAdapter(mPhotos);
        mAdapter.setOnItemSelectListener(new ListPhotoMakeVideoAdapter.OnItemSelectListener() {
            @Override
            public void onSelected(int position) {
                if(mPhotos.size() != 0) {
                    mCurrentIndex = position;
                    Photo photo = mPhotos.get(position);
                    displayPhoto(photo);
                    setBackgroundToolEffect();
                } else {
                    mImageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
                    mCurrentIndex = -1;
                    showToolLayout(false);
                }
            }
        });
        mRecyclerViewListPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewListPhoto.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerViewListPhoto);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.make_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.apply) {
            if (mPhotos.size() != 0) {
                new ExportVideo().execute();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ) {
            Bundle bundle = data.getExtras();
            switch (requestCode) {
                case REQUEST_CODE_SELECT_PHOTO:
                    List<Photo> photos = (List<Photo>) bundle.getSerializable(ConstActivity.KEY_SELECTED_IMAGE);
                    if (photos.size() != 0) {
                        mPhotos.addAll(photos);
                        mAdapter.notifyDataSetChanged();
                        if (mCurrentIndex == -1)
                            mCurrentIndex = 0;
                        displayPhoto(mPhotos.get(mCurrentIndex));
                        showToolLayout(true);
                        setBackgroundToolEffect();
                    }
                    break;
                case REQUEST_CODE_EDIT_PHOTO:
                    Photo photo = (Photo) bundle.getSerializable(AppConstant.PHOTO);
                    mPhotos.get(mCurrentIndex).setUri(photo.getUri());
                    mAdapter.notifyDataSetChanged();
                    displayPhoto(mPhotos.get(mCurrentIndex));
                    break;
            }

        }
    }

    private void showToolLayout(boolean show) {
        mRelativeLayoutTool.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void displayPhoto(Photo photo) {
        Uri uri = Uri.fromFile(new File(photo.getUri()));
        Picasso.with(MakeVideoActivity.this)
                .load(uri)
                .resize(ConstActivity.IMAGE_MAKE_VIDEO_WIDTH, ConstActivity.IMAGE_MAKE_VIDEO_HEIGHT)
                .centerCrop()
                .into(mImageViewPhoto);
    }

    private void playVideo(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intent.setDataAndType(Uri.parse(path), VIDEO_TYPE);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            DialogUtils.showAlert(MakeVideoActivity.this, R.string.activity_not_found);
        }
    }

    @Override
    public void onClick(View v) {
        if(mCurrentIndex != -1) {
            switch (v.getId()) {
                case R.id.image_scale:
                    mPhotos.get(mCurrentIndex).setEffect(VideoUtil.EFFECT_SCALE);
                    mImageViewScale.setImageResource(R.drawable.background_image_scale_pressed);
                    mImageViewTranslate.setImageResource(R.drawable.background_image_translate);
                    mImageViewRotate.setImageResource(R.drawable.background_image_rotate);
                    break;
                case R.id.image_translate:
                    mPhotos.get(mCurrentIndex).setEffect(VideoUtil.EFFECT_TRANSLATE);
                    mImageViewScale.setImageResource(R.drawable.background_image_scale);
                    mImageViewTranslate.setImageResource(R.drawable.background_image_translate_pressed);
                    mImageViewRotate.setImageResource(R.drawable.background_image_rotate);
                    break;
                case R.id.image_rotate:
                    mPhotos.get(mCurrentIndex).setEffect(VideoUtil.EFFECT_ROTATE);
                    mImageViewScale.setImageResource(R.drawable.background_image_scale);
                    mImageViewTranslate.setImageResource(R.drawable.background_image_translate);
                    mImageViewRotate.setImageResource(R.drawable.background_image_rotate_pressed);
                    break;
                case R.id.image_edit_photo:
                    Intent intent = new Intent(MakeVideoActivity.this, PhotoActivity.class);
                    Photo photo = mPhotos.get(mCurrentIndex);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AppConstant.PHOTO, photo);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_EDIT_PHOTO);
                    break;
            }
        }
    }

    private class ExportVideo extends AsyncTask<Void, Integer, String> {
        private ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MakeVideoActivity.this);
            mProgressDialog.setMessage(getString(R.string.message_exporting));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMax(MAX_SIZE);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            VideoUtil videoUtil = new VideoUtil(mPhotos, new VideoUtil.OnUpdateProgressDialog() {
                @Override
                public void update(int value) {
                    publishProgress(value);
                }
            });
            return videoUtil.makeVideo();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String path) {
            mProgressDialog.dismiss();
            playVideo(path);
        }
    }

    private void setBackgroundToolEffect(){
        Photo photo = mPhotos.get(mCurrentIndex);
        switch (photo.getEffect()) {
            case VideoUtil.EFFECT_SCALE:
                mImageViewScale.setImageResource(R.drawable.background_image_scale_pressed);
                mImageViewTranslate.setImageResource(R.drawable.background_image_translate);
                mImageViewRotate.setImageResource(R.drawable.background_image_rotate);
                break;
            case VideoUtil.EFFECT_TRANSLATE:
                mImageViewScale.setImageResource(R.drawable.background_image_scale);
                mImageViewTranslate.setImageResource(R.drawable.background_image_translate_pressed);
                mImageViewRotate.setImageResource(R.drawable.background_image_rotate);
                break;

            case VideoUtil.EFFECT_ROTATE:
                mImageViewScale.setImageResource(R.drawable.background_image_scale);
                mImageViewTranslate.setImageResource(R.drawable.background_image_translate);
                mImageViewRotate.setImageResource(R.drawable.background_image_rotate_pressed);
                break;
        }
    }
}
