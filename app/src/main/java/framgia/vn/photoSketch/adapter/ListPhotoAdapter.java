package framgia.vn.photoSketch.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by nghicv on 23/04/2016.
 */
public class ListPhotoAdapter extends RecyclerView.Adapter<ListPhotoAdapter.ViewHolder> {

    public static final String IMAGE_MIME_TYPE = "image/*";
    private List<Photo> mListPhoto;
    private Context mContext;
    public static final int IMAGE_SIZE = 196;

    public ListPhotoAdapter(List<Photo> listPhoto) {
        mListPhoto = listPhoto;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_photo, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = mListPhoto.get(position);
        final Uri uri = Uri.fromFile(new File(photo.getUri()));
        Picasso.with(mContext)
                .load(uri)
                .resize(IMAGE_SIZE, IMAGE_SIZE)
                .centerCrop()
                .into(holder.imageViewPhoto);
        holder.imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewer = new Intent();
                intentViewer.setAction(Intent.ACTION_VIEW);
                intentViewer.setDataAndType(uri, IMAGE_MIME_TYPE);
                mContext.startActivity(intentViewer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListPhoto.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewPhoto = (ImageView) itemView.findViewById(R.id.image_item_photo);
        }
    }
}
