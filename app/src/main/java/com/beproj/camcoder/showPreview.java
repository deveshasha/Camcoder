package com.beproj.camcoder;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class showPreview extends Activity {

    private ImageView mPhotoCapturedImageView;
    private String image_name="";
    private String encoded_string;
    private int code;
    private Bitmap bitmap;
    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);

        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        code = getIntent().getIntExtra("code", -1);
        imageuri = Uri.parse(getIntent().getStringExtra("imagePath"));
        image_name = getfilename(imageuri);
        String path = imageuri.getPath();
        showReducedSize(path);
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
        ServerTask task = new ServerTask();
        task.execute("");
      //  task.execute( Environment.getExternalStorageDirectory().toString() +image_name);
        /*URL url = null;
        String ans="";
        try {
            url = new URL("http://192.168.56.1/camcoder/recognize.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ans = readStream(in);
        }
        catch (MalformedURLException ex){
            Log.e(TAG, "error: " + ex.getMessage(), ex);

        }



        finally {
            urlConnection.disconnect();
        }

        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(ans);*/
    }

    public class ServerTask  extends AsyncTask<String, Integer , Void> {

        URL url = null;
        String ans="";
        public void process() {

            try {
                url = new URL("http://camcoder/recognize.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                ans = readStream(in);
            } catch (MalformedURLException ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);

            } catch (IOException ex) {
                Log.e(TAG, "error: " + ex.getMessage(), ex);

            }

            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            /*TextView t = (TextView) findViewById(R.id.textView);
            t.setText(ans);*/
        }
        @Override
        protected void onPostExecute(Void param) {
            TextView t = (TextView) findViewById(R.id.textView);
            t.setText(ans);
        }



        private String readStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n\r");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();

        }

        @Override
        protected Void doInBackground(String... strings) {
            process();
            return null;
        }
    }


    /*private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            bitmap = BitmapFactory.decodeFile(imageuri.getPath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }*/
    public String getfilename(Uri uri){
        if (uri.getScheme().equals("file")) {
            image_name = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, new String[]{
                        MediaStore.Images.ImageColumns.DISPLAY_NAME
                }, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    image_name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.d(TAG,"name is " + image_name);
                }
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return image_name;
    }



    /*private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, "http://192.168.56.1/camcoder/recognize.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView t = (TextView) findViewById(R.id.textView);
                        t.setText(response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView t = (TextView) findViewById(R.id.textView);
                t.setText("Didnt work");
            }
        }); *//*{
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);

                return map;
            }
        };*//*
        requestQueue.add(request);
    }*/
}
