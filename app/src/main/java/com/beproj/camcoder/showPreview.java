package com.beproj.camcoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class showPreview extends Activity {

    private ImageView mPhotoCapturedImageView;
    private String image_name="",path;
    private int code;
    private Bitmap bitmap;
    Uri imageuri;
    long totalSize = 0;
    private ProgressBar progressBar;
    //private TextView txtPercentage;

    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_preview);
       // process_button = (Button) findViewById(R.id.process);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //txtPercentage = (TextView) findViewById(R.id.textView);
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        code = getIntent().getIntExtra("code", -1);
        imageuri = Uri.parse(getIntent().getStringExtra("imagePath"));
        image_name = getfilename(imageuri);
        path = imageuri.getPath();
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

        Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(path, bmOptions);
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmap);

    }

    public void uploadFunc(View view) {
        new UploadFileToServer().execute();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        private final int UPLOADING_PHOTO  = 0;
        private final int SERVER_PROCESSING  = 1;
        private ProgressDialog dialog;

        public UploadFileToServer(){
            dialog = new ProgressDialog(mContext);
        }


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
/*            progressBar.setVisibility(View.VISIBLE);
            if(progress[0] == UPLOADING_PHOTO) {
                // updating progress bar value
                progressBar.setProgress(progress[0]);
                // updating percentage value
                txtPercentage.setText(new StringBuilder().append(String.valueOf(progress[0])).append("%").toString());
            }else if (progress[0] == SERVER_PROCESSING){

            }*/
            if(progress[0] == UPLOADING_PHOTO){
                dialog.setMessage("Uploading");
                dialog.show();
            }
            else if (progress[0] == SERVER_PROCESSING){
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog.setMessage("Processing");
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            /* performProcessing(); */
            uploadFile();
            return null;
        }

        @SuppressWarnings("deprecation")
        private void uploadFile() {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.43.151:8000/upload/");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                publishProgress(UPLOADING_PHOTO);
                File sourceFile = new File(path);
                entity.addPart("image", new FileBody(sourceFile));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);

                //HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    publishProgress(SERVER_PROCESSING);
                    InputStream is = response.getEntity().getContent();
                    String code = getCodeFromImage(is);
                    Intent intent = new Intent(showPreview.this, adjust.class);
                    intent.putExtra("code", code);
                    intent.putExtra("image_name",image_name);
                    startActivity(intent);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getCodeFromImage(InputStream is) {
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
        protected void onPostExecute(String result) {
           // Log.e(TAG, "Response from server: " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // showing the server response in an alert dialog
            //showAlert(result);
            //process_button.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
        }
    }

/*    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Successfully Uploaded")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
                    //Log.d(TAG,"name is " + image_name);
                }
            } finally {

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return image_name;
    }
}
