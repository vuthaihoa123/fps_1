package framgia.vn.photoSketch.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.bitmaputil.BitmapUtil;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.library.DialogUtils;
import framgia.vn.photoSketch.library.LoadPhoto;
import framgia.vn.photoSketch.models.Photo;

public class CombinePhotoActivity extends AppCompatActivity implements ConstActivity {
    private RecyclerView mRecyclerViewImage, mRecyclerViewImageCombine, mRecyclerViewImageCombine1;
    private List<Photo> mListPhoto, mListPhotoChoose;
    private boolean[] mThumbnailsSelection;
    private AdapterImage mAdapterImage;
    private AdapterImageCombine mAdapterImageCombine;
    private AdapterImageCombine1 mAdapterImageCombine1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_photo);
        setControl();
        setData();
    }

    private void setControl() {
        mRecyclerViewImage = (RecyclerView) findViewById(R.id.recyclerView_image);
        mRecyclerViewImage.setLayoutManager(new GridLayoutManager(this, NUMBER_IMAGE_VERTICAL_IMAGE_VIEW));
        /* RecyclerView Combine */
        mRecyclerViewImageCombine = (RecyclerView) findViewById(R.id.recyclerView_image_combine);
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(NUMBER_IMAGE_VERTICAL_IMAGE_COMBINE, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        // Attach layout manager to the RecyclerView
        mRecyclerViewImageCombine.setLayoutManager(gridLayoutManager);
        /* RecyclerView 1 Combine */
        mRecyclerViewImageCombine1 = (RecyclerView) findViewById(R.id.recyclerView_image_combine_1);
        StaggeredGridLayoutManager gridLayoutManager1 =
                new StaggeredGridLayoutManager(NUMBER_IMAGE_VERTICAL_IMAGE_COMBINE, StaggeredGridLayoutManager.HORIZONTAL);
        gridLayoutManager1.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerViewImageCombine1.setLayoutManager(gridLayoutManager1);
    }

    private void setData() {
        mListPhoto = LoadPhoto.loadPhotoPaths(BitmapUtil.FOLDER_NAME);
        mListPhotoChoose = new ArrayList<Photo>();
        mAdapterImageCombine = new AdapterImageCombine(mListPhotoChoose);
        mRecyclerViewImageCombine.setAdapter(mAdapterImageCombine);
        mAdapterImageCombine1 = new AdapterImageCombine1(mListPhotoChoose);
        mRecyclerViewImageCombine1.setAdapter(mAdapterImageCombine1);
        mAdapterImage = new AdapterImage(mListPhoto);
        mRecyclerViewImage.setAdapter(mAdapterImage);
    }

    private void createBitmapForLayout() throws IOException {
        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRecyclerViewImageCombine.setDrawingCacheEnabled(true);
                mRecyclerViewImageCombine.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                mRecyclerViewImageCombine.layout(0, 0, mRecyclerViewImageCombine.getMeasuredWidth(),
                        mRecyclerViewImageCombine.getMeasuredHeight());
                mRecyclerViewImageCombine.buildDrawingCache(true);
                Bitmap b = Bitmap.createBitmap(mRecyclerViewImageCombine.getDrawingCache());
                mRecyclerViewImageCombine.setDrawingCacheEnabled(false);
                try {
                    BitmapUtil.saveBitmapToSdcard(b);
                    Toast.makeText(CombinePhotoActivity.this, R.string.toast_save_image_combine_success, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
        DialogUtils.showDialog(this, getResources().getString(R.string.dialog_title_save_image_combine),
                getResources().getString(R.string.dialog_message_save_image_combine),
                getResources().getString(R.string.dialog_message_yes),
                getResources().getString(R.string.dialog_message_no), positive, negative);

    }

    public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> implements ConstActivity {
        private List<Photo> mListPhotoAdapter;
        private Context mContext;
        private int mCountChecked = 0;

        public AdapterImage(List<Photo> list) {
            mListPhotoAdapter = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_view_image, null);
            mThumbnailsSelection = new boolean[getItemCount()];
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Photo photo = mListPhotoAdapter.get(position);
            Uri uri = Uri.fromFile(new File(photo.getUri()));
            Picasso.with(mContext).load(uri)
                    .resize(PICASSO_IMAGE_RESIZE_WIDTH, PICASSO_IMAGE_RESIZE_HEIGHT)
                    .centerCrop()
                    .into(holder.imageViewImage);
        }

        @Override
        public int getItemCount() {
            return mListPhotoAdapter.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView imageViewImage;
            private CheckBox checkBoxImage;

            public ViewHolder(View itemView) {
                super(itemView);
                imageViewImage = (ImageView) itemView.findViewById(R.id.imageView_item_view_image);
                checkBoxImage = (CheckBox) itemView.findViewById(R.id.checkBox_item_view_image);
                imageViewImage.setOnClickListener(this);
                checkBoxImage.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                switch (v.getId()) {
                    case R.id.imageView_item_view_image:
                        setCheckBox(position);
                        break;
                    case R.id.checkBox_item_view_image:
                        setCheckBox(position);
                        break;
                }
            }

            private void setCheckBox(int position) {
                if (!mThumbnailsSelection[position]) {
                    if (mCountChecked < NUMBER_IMAGE_IN_IMAGE_COMBINE) {
                        checkBoxImage.setChecked(!mThumbnailsSelection[position]);
                        mThumbnailsSelection[position] = !mThumbnailsSelection[position];
                        mListPhotoChoose.add(mListPhotoAdapter.get(position));
                        mAdapterImageCombine.notifyDataSetChanged();
                        mAdapterImageCombine1.notifyDataSetChanged();
                        mCountChecked++;
                    } else {
                        Toast.makeText(mContext, R.string.toast_select_6_image, Toast.LENGTH_SHORT).show();
                        checkBoxImage.setChecked(false);
                    }
                } else {
                    for (Photo photo : mListPhotoChoose) {
                        if (mListPhotoAdapter.get(position).getUri().equals(photo.getUri())) {
                            mListPhotoChoose.remove(photo);
                            mAdapterImageCombine.notifyDataSetChanged();
                            mAdapterImageCombine1.notifyDataSetChanged();
                            break;
                        }
                    }
                    checkBoxImage.setChecked(!mThumbnailsSelection[position]);
                    mThumbnailsSelection[position] = !mThumbnailsSelection[position];
                    mCountChecked--;
                }
            }
        }
    }

    public class AdapterImageCombine extends RecyclerView.Adapter<AdapterImageCombine.ViewHolder> implements ConstActivity {
        private List<Photo> mListPhotoAdapter;
        private Context mContext;

        public AdapterImageCombine(List<Photo> list) {
            mListPhotoAdapter = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_view_image_combine, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Photo photo = mListPhotoAdapter.get(position);
            Uri uri = Uri.fromFile(new File(photo.getUri()));
            Picasso.with(mContext).load(uri)
                    .resize(PICASSO_IMAGE_COMBINE_RESIZE_WIDTH, PICASSO_IMAGE_COMBINE_RESIZE_HEIGHT)
                    .centerCrop()
                    .into(holder.imageViewImage);
        }

        @Override
        public int getItemCount() {
            return mListPhotoAdapter.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView imageViewImage;

            public ViewHolder(View itemView) {
                super(itemView);
                imageViewImage = (ImageView) itemView.findViewById(R.id.imageView_item_view_image_combine);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                try {
                    createBitmapForLayout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class AdapterImageCombine1 extends RecyclerView.Adapter<AdapterImageCombine1.ViewHolder> implements ConstActivity {
        private List<Photo> mListPhotoAdapter;
        private Context mContext;

        public AdapterImageCombine1(List<Photo> list) {
            mListPhotoAdapter = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_view_image_combine_1, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Photo photo = mListPhotoAdapter.get(position);
            Uri uri = Uri.fromFile(new File(photo.getUri()));
            Picasso.with(mContext).load(uri)
                    .resize(PICASSO_IMAGE_COMBINE_RESIZE_WIDTH, PICASSO_IMAGE_COMBINE_RESIZE_HEIGHT)
                    .centerCrop()
                    .into(holder.imageViewImage1);

        }

        @Override
        public int getItemCount() {
            return mListPhotoAdapter.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView imageViewImage1;

            public ViewHolder(View itemView) {
                super(itemView);
                imageViewImage1 = (ImageView) itemView.findViewById(R.id.imageView_item_view_image_combine_1);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                try {
                    createBitmapForLayout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
