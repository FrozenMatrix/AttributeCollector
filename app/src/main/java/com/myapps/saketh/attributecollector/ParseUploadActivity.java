package com.myapps.saketh.attributecollector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import com.parse.Parse;
import com.parse.ParseObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Vector;

public class ParseUploadActivity extends AppCompatActivity {
    Vector<ParseObject> parseObjects;
    CSVIterator ite;
    Handler objectStoreHandler;
    Handler parseUploadHandler;
    Button btnUpload;
    ProgressBar uploadProgress;

    int Total = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_upload);

        btnUpload = (Button) findViewById(R.id.btnUpload);
        uploadProgress = (ProgressBar) findViewById(R.id.uploadProgressed);
        uploadProgress.setProgress(0);

        parseObjects = new Vector<>(1);
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), Constants.ParseApplicationID, Constants.ParseClientKey);

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(Constants.AttributeFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        parseUploadHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1 == 1){
                    uploadProgress.setProgress(msg.getData().getInt("Progress"));
                    uploadProgress.setMax(100);
                }
            }
        };

        final Thread parseSaver = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Iterator<ParseObject> it = parseObjects.iterator();
                        while(it.hasNext())
                        {
                            try {
                                it.next().saveInBackground().waitForCompletion();
                                ++count;
                                Message msg = new Message();
                                msg.arg1 = 1;
                                Bundle bundle = new Bundle();
                                bundle.putInt("Progress", (count/Total)*100);
                                msg.setData(bundle);
                                parseUploadHandler.sendMessage(msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d("LOL", "Done : " + it.hasNext());
                        }
                    }
                }
        );

        ite = (CSVIterator) csvReader.iterator();

        while(ite.hasNext()){
            ++Total;
            ite.next();
        }

        ite = (CSVIterator) csvReader.iterator();

        objectStoreHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                    if(msg.arg1 == 0)
                        parseSaver.start();
            }
        };

        final Thread objectStore = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while(ite.hasNext()) {
                            String[] temp = ite.next();
                            if(temp[0].equals(Constants.Headers[0]))
                                continue;
                            ParseObject object = new ParseObject(Constants.user);
                            for(int i=0;i<temp.length;i++)
                                object.put(Constants.Headers[i], temp[i]);
                            parseObjects.add(object);
                        }
                        Message msg = new Message();
                        msg.arg1 = 0;
                        objectStoreHandler.sendMessage(msg);
                    }
                }
        );

        btnUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objectStore.start();
                    }
                }
        );
    }
}
