package com.beproj.camcoder;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String mImageFileLocation = "";
    private File image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.beproj.camcoder.R.layout.activity_main);
    }

    public void takePhoto(View view) {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCameraApplicationIntent, REQUEST_CAMERA);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Intent open_displayPage=new Intent(this,showPreview.class);
            open_displayPage.putExtra("code",0);
            open_displayPage.putExtra("imagePath", mImageFileLocation);
            startActivity(open_displayPage);
        }
        else if(requestCode == SELECT_FILE && resultCode == RESULT_OK){
            onSelectFromGalleryResult(data);
        }
    }

    File createImageFile() throws IOException {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camcoder");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Camcoder", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp + "_";

        //File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //image = File.createTempFile(imageFileName,".jpg", mediaStorageDir);

        image = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        mImageFileLocation = image.getAbsolutePath();

        return image;

    }

    public void galleryIntent(View view)    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        /*String path = data.getData().getPath();
        Intent open_displayPage = new Intent(this,showPreview.class);
        open_displayPage.putExtra("code",1);
        open_displayPage.putExtra("imageGalleryPath", path);
        startActivity(open_displayPage);*/

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent open_displayPage = new Intent(this,showPreview.class);
        open_displayPage.putExtra("code",1);

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);
        open_displayPage.putExtra("byteArray", bs.toByteArray());
        startActivity(open_displayPage);


        open_displayPage.putExtra("galleryImage", bm);
        startActivity(open_displayPage);

        //ivImage.setImageBitmap(bm);
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
