package framgia.vn.photoSketch.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import framgia.vn.photoSketch.R;
import framgia.vn.photoSketch.models.Video;

/**
 * Created by hoavt on 05/05/2016.
 */
public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.ViewHolder> {
    public static final String VIDEO_MIME_TYPE = "video/*";
    public static final int VIDEO_SIZE = 196;
    private List<Video> mListVideo;
    private Context mContext;

    public ListVideoAdapter(List<Video> listVideo) {
        mListVideo = listVideo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_photo, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = mListVideo.get(position);
        final Uri uri = Uri.fromFile(new File(video.getPath()));
//        Picasso.with(mContext)
//                .load(uri)
//                .resize(VIDEO_SIZE, VIDEO_SIZE)
//                .centerCrop()
//                .into(holder.imageViewVideo);
        Bitmap bmpVideo = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        holder.imageViewVideo.setImageBitmap(bmpVideo);
        holder.imageViewVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentViewer = new Intent();
                intentViewer.setAction(Intent.ACTION_VIEW);
                intentViewer.setDataAndType(uri, VIDEO_MIME_TYPE);
                mContext.startActivity(intentViewer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListVideo.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewVideo = (ImageView) itemView.findViewById(R.id.image_item_photo);
        }
    }
}

