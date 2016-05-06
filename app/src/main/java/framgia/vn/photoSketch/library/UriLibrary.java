package framgia.vn.photoSketch.library;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

import framgia.vn.photoSketch.constants.ConstUri;

/**
 * Created by FRAMGIA\nguyen.huy.quyet on 13/04/2016.
 */
public class UriLibrary implements ConstUri {
    public static Uri getOutputMediaFile(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String UriToUrl(Context context, Uri imageUri) {
        // LocalStorageProvider
        if (isLocalStorageDocument(imageUri)) {
            // The path is the id
            return DocumentsContract.getDocumentId(imageUri);
        }
        // ExternalStorageProvider
        else if (isExternalStorageDocument(imageUri)) {
            String[] type = typeUri(imageUri);
            if (PRIMARY.equalsIgnoreCase(type[0])) {
                return Environment.getExternalStorageDirectory() + File.separator + type[1];
            }
        }
        // DownloadsProvider
        else if (isDownloadsDocument(imageUri)) {
            final String id = DocumentsContract.getDocumentId(imageUri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse(URI_DOWNLOAD), Long.valueOf(id));
            return getDataColumn(context, contentUri, null, null);
        }
        //  MediaProvider
        else if (isMediaDocument(imageUri)) {
            String[] type = typeUri(imageUri);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            final String selection = SELECTION;
            final String[] selectionArgs = new String[]{type[1]};
            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        // MediaStore (and general)
        else if (CONTENT.equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        return null;
    }

    public static boolean isLocalStorageDocument(Uri uri) {
        return LOCAL_STORAGE_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return EXTERNAL_STORAGE_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return DOWNLOAD_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return MEDIA_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return GOOGLE_PHOTO_URI.equals(uri.getAuthority());
    }

    private static String[] typeUri(Uri uri) {
        String docId = DocumentsContract.getDocumentId(uri);
        return docId.split(":");
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = COLUMN;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
