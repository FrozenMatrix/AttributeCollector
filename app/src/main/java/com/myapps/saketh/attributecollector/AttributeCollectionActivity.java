package com.myapps.saketh.attributecollector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class AttributeCollectionActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Button btnUpload;
    boolean uploadClicked = false;
    Intent intent;

    RadioGroup rdGrpLayer;

    ListView listViewActivity;

    ArrayList<String> activitieslayer1;
    ArrayList<String> activitieslayer2;

    ArrayAdapter<String> activityL1Adatpter;
    ArrayAdapter<String> activityL2Adatpter;
    TextView txtActivity;

    Intent dataCollIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_collection);
        dataCollIntent = new Intent(this, DataCollector.class);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = this.prefs.edit();

        final String[] activityListL1 = "Walking#Running#Sitting#Standing#Jogging#Stepping".split("#");
        final String[] activityListL2 = "Queuing#Not Queuing".split("#");

        activitieslayer1 = new ArrayList<>();
        activitieslayer2 = new ArrayList<>();

        for(String x:activityListL1)
            activitieslayer1.add(x);
        for(String x:activityListL2)
            activitieslayer2.add(x);

        RadioButton rd = (RadioButton) findViewById(R.id.rdBtnLayer1);
        rd.setSelected(true);

        listViewActivity = (ListView) findViewById(R.id.listActivity);
        activityL1Adatpter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, activitieslayer1);
        activityL2Adatpter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1, activitieslayer2);

        listViewActivity.setAdapter(activityL1Adatpter);
        listViewActivity.setBackgroundColor(Color.GRAY);

        btnUpload = (Button) findViewById(R.id.btnStopUpload);
        txtActivity = (TextView) findViewById(R.id.txtActivity);

        rdGrpLayer = (RadioGroup) findViewById(R.id.rdBtnGrp);
        rdGrpLayer.check(R.id.rdBtnLayer1);
        rdGrpLayer.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton tempRdBtn = (RadioButton) findViewById(checkedId);
                        if (tempRdBtn.equals(findViewById(R.id.rdBtnLayer1)))
                            listViewActivity.setAdapter(activityL1Adatpter);
                        else
                            listViewActivity.setAdapter(activityL2Adatpter);
                    }
                }
        );

        listViewActivity.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String activity = parent.getItemAtPosition(position).toString();
                        txtActivity.setText(activity);
                        Constants.activity = activity;
                        Intent intent = new Intent("Activity");
                        Bundle bundle = new Bundle();
                        bundle.putString("Activity", activity);
                        intent.putExtras(bundle);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }
        );

        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startService(dataCollIntent);
                btnUpload.setText("Stop Collection");
            }
        };

        btnUpload.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(btnUpload.getText().equals("Stop Collection")) {
                            Toast.makeText(getApplicationContext(), "Collection Stopped", Toast.LENGTH_SHORT).show();
                            stopService(dataCollIntent);
                            btnUpload.setText("Start Collection");
                            startActivity(new Intent(getApplicationContext(), ParseUploadActivity.class));
                        }
                        else if(btnUpload.getText().equals("Start Collection")){
                            Timer timer = new Timer();
                            timer.schedule(timerTask, (long) Math.pow(10, 10));
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnUpload.setText("Start Collection");
    }
}
