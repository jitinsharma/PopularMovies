package com.sharma.jitin.popularmovies.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jitin on 04-01-2016.
 */
public class Utility {

    public static Bitmap convertBytesToBitmap(byte[] bytes){
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static byte[] convertBitmapToBytes(Bitmap bitmap){
        byte[] bytes;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        bytes = stream.toByteArray();
        return bytes;
    }

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo != null)
                if(netInfo.isConnected())
                    return true;
        }
        return false;
    }

    public static String convertDate(String value)throws Exception{
        String convertedDate;
        Date date;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        SimpleDateFormat sdfFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

        date = formatter.parse(value);
        convertedDate = sdfFormat.format(date);
        return convertedDate;
    }
}
