package com.glela.micro_mall.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.glela.micro_mall.GlelaWebUtil;

public class GlelaUtils {

    public static void toast(CharSequence cs) {
        if (GlelaWebUtil.mGlelaApp != null) {
            Toast.makeText(GlelaWebUtil.mGlelaApp, cs, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取媒体的uri
     */
    public static Uri getMediaContentUri(Context context, String absolutePath) {
        Uri newUri;
//      先查找是否有这个uri
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{absolutePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            newUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absolutePath);
            newUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        if (cursor != null)
            cursor.close();
        return newUri;
    }
}
