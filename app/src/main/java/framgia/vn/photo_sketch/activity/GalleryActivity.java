package framgia.vn.photo_sketch.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photo_sketch.R;
import framgia.vn.photo_sketch.adapter.GalleryAdapter;
import framgia.vn.photo_sketch.constants.ConstActivity;
import framgia.vn.photo_sketch.library.LoadPhoto;
import framgia.vn.photo_sketch.models.Photo;

/**
 * Created by nghicv on 02/05/2016.
 */
public class GalleryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerViewSelectPhoto;
    private Toolbar mToolbar;
    private List<Photo> mPhotos = new ArrayList<>();
    public static final int ITEM_SPACE = 10;
    public static final int NUM_COLUMN = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initView();
        mPhotos = LoadPhoto.loadPhotos(getContentResolver());
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mRecyclerViewSelectPhoto = (RecyclerView) findViewById(R.id.recycler_gallery_image);
        mRecyclerViewSelectPhoto.setLayoutManager(new GridLayoutManager(GalleryActivity.this, NUM_COLUMN));
        mRecyclerViewSelectPhoto.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = ITEM_SPACE;
                outRect.right = ITEM_SPACE;
                outRect.bottom = ITEM_SPACE;
                if (parent.getChildLayoutPosition(view) == 0 ||parent.getChildLayoutPosition(view) == 1 || parent.getChildLayoutPosition(view) == 2) {
                    outRect.top = ITEM_SPACE;
                } else {
                    outRect.top = 0;
                }
                if(parent.getChildLayoutPosition(view) % NUM_COLUMN == 1) {
                    outRect.left = 0;
                    outRect.right = 0;
                }
            }
        });
        GalleryAdapter galleryAdapter = new GalleryAdapter(mPhotos);
        mRecyclerViewSelectPhoto.setAdapter(galleryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.apply) {
            List<Photo> selectedPhotos = new ArrayList<>();
            for(Photo photo : mPhotos) {
                if (photo.isSelected())
                    selectedPhotos.add(photo);
            }
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstActivity.KEY_SELECTED_IMAGE, (Serializable) selectedPhotos);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
