package com.beproj.camcoder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class adjust extends AppCompatActivity {

    private static final String TAG = "code value";
    private EditText mCodeText;
    private Context mContext = this;
    long totalSize = 0;
    String image_name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust);

        Intent intent = getIntent();
        String codes = intent.getStringExtra("code");
        image_name = intent.getStringExtra("image_name");
        Log.d(TAG,image_name);
        mCodeText = (EditText) findViewById(R.id.editText);
        mCodeText.setText(codes, TextView.BufferType.EDITABLE);

    }



    public void runCode(View view) {
        String code = mCodeText.getText().toString();
        //Log.d(TAG,code);
        RunCodeServer task = new RunCodeServer();
        task.execute(code);
    }

    private class RunCodeServer extends AsyncTask<String, Integer , String>{

        private final int UPLOADING_CODE  = 0;
        private final int SERVER_PROCESSING  = 1;
        private ProgressDialog dialog;


        @Override
        protected String doInBackground(String... param) {
            String code = param[0];
            Log.d(TAG,code);
            return uploadCode(code);

        }

        public RunCodeServer(){
            dialog = new ProgressDialog(mContext);
        }


        @SuppressWarnings("deprecation")
        private String uploadCode(String code) {
            String responseString;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://192.168.0.102/camcoder/runcode.php");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                publishProgress(UPLOADING_CODE);
                File filename = writeFile(code);
               // File sourceFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camcoder");
                entity.addPart("code", new FileBody(filename));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
/*                    publishProgress(SERVER_PROCESSING);
                    InputStream is = response.getEntity().getContent();
                    String output = getFinalOutput(is);
                    Intent intent = new Intent(adjust.this, adjust.class);
                    intent.putExtra("output", output);
                    startActivity(intent);*/
                }
                else{
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            }catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;
        }
        public File writeFile(String mValue) {

            try {
                File filename = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + image_name);
                FileWriter fw = new FileWriter(filename, true);
                fw.write(mValue + "\n\n");
                fw.close();
                return filename;
            } catch (IOException ioe) {
            }

            return null;
        }

/*        private String getFinalOutput(InputStream is) {
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
        }*/
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if(progress[0] == UPLOADING_CODE){
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
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            showAlert(result);
            //super.onPostExecute(result);
        }

    }
    private void showAlert(String message) {
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
    }
}
