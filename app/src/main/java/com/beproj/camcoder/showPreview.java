package com.beproj.camcoder;

/*import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class showPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);
    }
}*/

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;

public class showPreview extends Activity {
    private static final String TAG = "value";
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation ="",mImageGalleryFileLocation="";
    private Bitmap b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_preview);

        /*Intent i = getIntent();
        Bitmap photoReducedSizeBitmp = (Bitmap) i.getParcelableExtra("bmp_Image");
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);*/
        int code = getIntent().getIntExtra("code",-1);
        mImageFileLocation = getIntent().getStringExtra("imagePath");
        //mImageGalleryFileLocation = getIntent().getStringExtra("imageGalleryPath");
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);

       // Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("galleryImage");


        if(code == 1) {
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
                            .getByteArrayExtra("byteArray").length);

            mPhotoCapturedImageView.setImageBitmap(b);
        }
        else if(code == 0)
            showReducedSize(mImageFileLocation);
        else
            Toast.makeText(getApplicationContext(),"Cannot load. Error code -1",Toast.LENGTH_SHORT);
        //showReducedSize(String mImageGalleryFileLocation );

    }

    public void showReducedSize(String path) {
        int targetImageViewWidth = 420; //mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = 420; //mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(path, bmOptions);
        /*Intent intent = new Intent(this, showPreview.class);
        intent.putExtra("bmp_Image", photoReducedSizeBitmp);
        startActivity(intent);*/
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);

    }

}
