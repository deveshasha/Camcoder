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
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class showPreview extends Activity {
    private static final String TAG = "value";
    private ImageView mPhotoCapturedImageView;
    private String mImageFileLocation = "",image_name="";
    private Bitmap b, photoReducedSizeBitmp;
    private int code;
    private String encoded_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_preview);

        /*Intent i = getIntent();
        Bitmap photoReducedSizeBitmp = (Bitmap) i.getParcelableExtra("bmp_Image");
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);*/
        code = getIntent().getIntExtra("code", -1);
        mImageFileLocation = getIntent().getStringExtra("imagePath");
        //mImageGalleryFileLocation = getIntent().getStringExtra("imageGalleryPath");
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);

        // Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("galleryImage");


        if (code == 1) {
            b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
                            .getByteArrayExtra("byteArray").length);

            mPhotoCapturedImageView.setImageBitmap(b);

        } else if (code == 0)
            showReducedSize(mImageFileLocation);
        else
            Toast.makeText(getApplicationContext(), "Cannot load. Error code -1", Toast.LENGTH_SHORT);
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

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        photoReducedSizeBitmp = BitmapFactory.decodeFile(path, bmOptions);
        /*Intent intent = new Intent(this, showPreview.class);
        intent.putExtra("bmp_Image", photoReducedSizeBitmp);
        startActivity(intent);*/
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);

    }


    public void uploadFunc(View view) {
        new Encode_image().execute();
    }

    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (code == 1) {
                    // bitmap = BitmapFactory.decodeFile(file_uri.getPath());
                    b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                  //  b.recycle();

                    byte[] array = stream.toByteArray();
                    encoded_string = Base64.encodeToString(array, 0);
                    return null;
                } else if (code == 0) {
                    photoReducedSizeBitmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                  //  photoReducedSizeBitmp.recycle();

                    byte[] array = stream.toByteArray();
                    encoded_string = Base64.encodeToString(array, 0);
                    return null;

                }
            return null;
        }

            @Override
            protected void onPostExecute(Void aVoid) {
                makeRequest();
            }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.56.1/camcoder/recognize.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);

                return map;
            }
        };
        requestQueue.add(request);
    }
}
