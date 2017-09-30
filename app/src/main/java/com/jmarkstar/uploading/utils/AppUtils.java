package com.jmarkstar.uploading.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

/**
 * Created by jmarkstar on 30/09/2017.
 */

public class AppUtils {

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String thePath = "no-path-found";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                thePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return  thePath;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
