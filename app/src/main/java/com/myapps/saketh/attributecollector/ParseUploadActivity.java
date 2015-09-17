package com.myapps.saketh.attributecollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ParseUploadActivity extends AppCompatActivity {

    Handler parseUploadHandler;
    Handler toastHandler;
    Button btnUpload;
    ProgressBar uploadProgress;
    TextView txtUploadPer;


    boolean uploadClicked = false;

    BroadcastReceiver progressBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_upload);

        btnUpload = (Button) findViewById(R.id.btnUpload);
        uploadProgress = (ProgressBar) findViewById(R.id.uploadProgressbar);
        uploadProgress.setProgress(0);

        txtUploadPer = (TextView) findViewById(R.id.txtUploadPer);
        parseUploadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int progress = msg.getData().getInt("Progress");
                uploadProgress.setProgress(progress);
                txtUploadPer.setText(progress+"%");
                Log.d("LOL", "Progress : " + progress );
                uploadProgress.setMax(100);
                if(progress == 100){
                    btnUpload.setText("Try Upload Again");
                    Toast.makeText(getApplicationContext(), "Upload Done!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        progressBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                Message msg = new Message();
                msg.setData(intent.getExtras());
                parseUploadHandler.sendMessage(msg);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(progressBroadcastReceiver, new IntentFilter("Progress-Intent"));

        toastHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String toast = msg.getData().getString("Toast");
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        };

        btnUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isNetworkAvailable()) {
                            btnUpload.setText("Uploading");
                            if (!uploadClicked) {
                                Intent intentParse = new Intent(getApplicationContext(), ParseObjectSaver.class);
                                startService(intentParse);
                                uploadClicked = true;
                            }
                        }
                        else {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("Toast", "Check Network Connection");
                            msg.setData(bundle);
                            toastHandler.sendMessage(msg);
                        }
                    }
                }
        );
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
