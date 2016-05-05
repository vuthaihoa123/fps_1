package framgia.vn.photoSketch.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ListPhotoMakeVideoAdapter;
import framgia.vn.photoSketch.bitmaputil.VideoUtil;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.library.DialogUtils;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 02/05/2016.
 */
public class MakeVideoActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerViewListPhoto;
    private RecyclerView mRecyclerViewTool;
    private ImageView mImageViewPhoto;
    private List<Photo> mPhotos = new ArrayList<>();
    private ListPhotoMakeVideoAdapter mAdapter;
    public static final int REQUEST_CODE = 1;
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
        mRecyclerViewTool = (RecyclerView) findViewById(R.id.recycler_tool_edit);
        mImageViewPhoto = (ImageView) findViewById(R.id.image_make_video);
        mAdapter = new ListPhotoMakeVideoAdapter(mPhotos);
        mAdapter.setOnItemSelectListener(new ListPhotoMakeVideoAdapter.OnItemSelectListener() {
            @Override
            public void onSelected(int position) {
                if(mPhotos.size() != 0) {
                    Photo photo = mPhotos.get(position);
                    displayPhoto(photo);
                } else {
                    mImageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
                }
            }
        });
        mRecyclerViewListPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewListPhoto.setAdapter(mAdapter);
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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            List<Photo> photos = (List<Photo>) bundle.getSerializable(ConstActivity.KEY_SELECTED_IMAGE);
            if (photos.size() != 0) {
                mPhotos.addAll(photos);
                mAdapter.notifyDataSetChanged();
                displayPhoto(mPhotos.get(0));
            }
        }
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
}
