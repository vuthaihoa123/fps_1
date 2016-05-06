package framgia.vn.photoSketch.library;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import framgia.vn.photoSketch.constants.ConstActivity;
import framgia.vn.photoSketch.models.Photo;

/**
 * Created by hoada921 on 2016-04-25.
 */
public class LoadPhoto {
    private static List<Photo> mListPhoto;

    public static List<Photo> loadPhotoAll() {
        mListPhoto = new ArrayList<>();
        // TODO : load all image in device
        return mListPhoto;
    }

    public static List<Photo> loadPhotoPaths(String name) {
        mListPhoto = new ArrayList<>();
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + ConstActivity.ROOT_FOLDER +
                File.separator + name;
        File file = new File(dirPath);
        if (file != null && file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                mListPhoto.add(new Photo(listFile[i].getAbsolutePath()));
            }
        }
        return mListPhoto;
    }

    public static List<Photo> loadPhotos(ContentResolver contentResolver) {
        List<Photo> photos = new ArrayList<>();
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            Photo photo = new Photo();
            photo.setUri(imagecursor.getString(dataColumnIndex));
            photos.add(photo);
        }
        return photos;
    }
}
