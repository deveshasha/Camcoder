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

public class showPreview extends Activity {
    private static final String TAG = "value";
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_preview);

        /*Intent i = getIntent();
        Bitmap photoReducedSizeBitmp = (Bitmap) i.getParcelableExtra("bmp_Image");
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);*/
        mImageFileLocation = getIntent().getStringExtra("imagePath");

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        showReducedSize();

    }

    public void showReducedSize() {
        int targetImageViewWidth = 420; //mPhotoCapturedImageView.getWidth();
        int targetImageViewHeight = 420; //mPhotoCapturedImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        /*Intent intent = new Intent(this, showPreview.class);
        intent.putExtra("bmp_Image", photoReducedSizeBitmp);
        startActivity(intent);*/
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);

    }

}
