package com.ratechnoworld.megalotto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;



public class ImageUtility {
    private final Context context;

    private ImageUtility(Context context) {
        this.context = context;
    }

    public static ImageUtility using(Context context) {
        return new ImageUtility(context);
    }

    public String toBase64(String path) {
        Bitmap bitmap = toBitmap(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return Base64.encode(bytes);
        }

        return "Error encoding this image.";
    }


    private Bitmap toBitmap(String path) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new File(path)));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
