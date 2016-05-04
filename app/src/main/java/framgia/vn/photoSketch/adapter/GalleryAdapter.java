package framgia.vn.photoSketch.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 02/05/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Photo> mPhotos;
    private Context mContext;

    public GalleryAdapter(List<Photo> listPhoto) {
        mPhotos = listPhoto;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null)
            mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final Photo photo = mPhotos.get(position);
        Uri uri = Uri.fromFile(new File(photo.getUri()));
        Picasso.with(mContext)
                .load(uri)
                .resize(ConstActivity.PICASSO_IMAGE_RESIZE_WIDTH, ConstActivity.PICASSO_IMAGE_RESIZE_HEIGHT)
                .centerCrop()
                .into(viewHolder.imageViewPhoto);
        viewHolder.checkboxPhoto.setChecked(photo.isSelected());
        viewHolder.imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkboxPhoto.setChecked(!photo.isSelected());
                photo.setSelected(!photo.isSelected());
            }
        });
        viewHolder.checkboxPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkboxPhoto.setChecked(!photo.isSelected());
                photo.setSelected(!photo.isSelected());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatCheckBox checkboxPhoto;
        ImageView imageViewPhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            checkboxPhoto = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox_item_gallery);
            imageViewPhoto = (ImageView) itemView.findViewById(R.id.image_gallery);
        }
    }
}
