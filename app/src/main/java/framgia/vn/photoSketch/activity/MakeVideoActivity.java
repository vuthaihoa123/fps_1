package framgia.vn.photoSketch.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.adapter.ListPhotoMakeVideoAdapter;
import framgia.vn.photoSketch.constants.ConstActivity;
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
            // TODO
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            List<Photo> photos = (List<Photo>) bundle.getSerializable(ConstActivity.KEY_SELECTED_IMAGE);
            mPhotos.addAll(photos);
            mAdapter.notifyDataSetChanged();
            displayPhoto(mPhotos.get(0));
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
}
