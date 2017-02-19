package com.beproj.camcoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

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

    private ImageView mPhotoCapturedImageView;
    private String image_name="";
    private String encoded_string;
    private int code;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        code = getIntent().getIntExtra("code", -1);

        if(code == 0){
            String path = getIntent().getStringExtra("imagePath");
            showReducedSize(path);
        }
        else if(code == 1){
            Bitmap bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent()
                            .getByteArrayExtra("byteArray").length);
            mPhotoCapturedImageView.setImageBitmap(bitmap);

        }
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

        Bitmap photoReducedSizeBitmp = BitmapFactory.decodeFile(path, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmp);

    }

    public void uploadFunc(View view) {
        new Encode_image().execute();
    }

    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            bitmap.recycle();
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
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
