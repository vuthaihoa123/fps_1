package framgia.vn.photoSketch.library;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.models.Video;

/**
 * Created by hoavt on 05/05/2016.
 */
public class LoadVideo {
    private static List<Video> mListVideo;

    public static List<Video> loadVideoAll() {
        mListVideo = new ArrayList<>();
        // TODO : load all image in device
        return mListVideo;
    }

    public static List<Video> loadVideoPaths(String name) {
        mListVideo = new ArrayList<>();
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name;
        File file = new File(dirPath);
        if (file != null && file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                mListVideo.add(new Video(listFile[i].getAbsolutePath()));
            }
        }
        return mListVideo;
    }

    public static List<Video> loadVideos(ContentResolver contentResolver) {
        List<Video> photos = new ArrayList<>();
        final String[] columns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID};
        final String orderBy = MediaStore.Video.Media._ID;
        Cursor videocursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int video_column_index = videocursor.getColumnIndex(MediaStore.Video.Media._ID);
        for (int i = 0; i < videocursor.getCount(); i++) {
            videocursor.moveToPosition(i);
            int id = videocursor.getInt(video_column_index);
            int dataColumnIndex = videocursor.getColumnIndex(MediaStore.Video.Media.DATA);
            Video photo = new Video(videocursor.getString(dataColumnIndex));
            photos.add(photo);
        }
        return photos;
    }
}
