package com.beproj.camcoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class showPreview extends Activity {
    //private static final String TAG = "value";
    private ImageView mPhotoCapturedImageView;
    private String image_name="";
    private Bitmap bitmap;
    private String encoded_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);

        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPhotoCapturedImageView.setImageBitmap(bitmap);
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
