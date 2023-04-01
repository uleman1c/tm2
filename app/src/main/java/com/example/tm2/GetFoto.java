package com.example.tm2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class GetFoto {

    public Uri uri;
    public Intent intent;
    public File file;

    public static File createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    UUID.randomUUID().toString(),  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Uri uriFromFile(Context context, File file){

        return FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".fileprovider", file);

    }

    public static byte[] getBytes(FileInputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



    public static Bitmap bitmapFromFile(File file){


        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        byte[] inputData = null;

        try {
            inputData = getBytes(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;

    }

    public GetFoto(Context context) {

        if (Build.VERSION.SDK_INT >= 24) {

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (true || intent.resolveActivity(context.getPackageManager()) != null) {

                file = createImageFile(context);

                if (file != null) {
                    uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".fileprovider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);

                } else {
                    intent = null;
                }
            } else {
                    intent = null;
            }

        } else {

            file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");

            uri = Uri.fromFile(file);

            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            if (intent.resolveActivity(context.getPackageManager()) == null) {

                intent = null;

            }

        }


    }
}
