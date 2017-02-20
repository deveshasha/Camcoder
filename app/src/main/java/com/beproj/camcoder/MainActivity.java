package com.beproj.camcoder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Uri photoFileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.beproj.camcoder.R.layout.activity_main);
    }

    public void takePhotoIntent(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        photoFileUri = Uri.fromFile(photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);

        startActivityForResult(callCameraApplicationIntent, REQUEST_CAMERA);
    }

    File createImageFile() throws IOException {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camcoder");
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camcoder", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        return image;
    }

    public void galleryIntent(View view)    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Intent open_displayPage = new Intent(this,showPreview.class);
            open_displayPage.putExtra("imagePath",photoFileUri.toString());
            startActivity(open_displayPage);
        }
        else if(requestCode == SELECT_FILE && resultCode == RESULT_OK){

            File imagefile = null;
            InputStream inputStream;
            OutputStream outputStream;
            try {
                 imagefile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                outputStream = new FileOutputStream(imagefile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead = inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,bytesRead);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            photoFileUri = Uri.fromFile(imagefile);
            Intent open_displayPage = new Intent(this,showPreview.class);
            open_displayPage.putExtra("imagePath", photoFileUri.toString());
            startActivity(open_displayPage);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
