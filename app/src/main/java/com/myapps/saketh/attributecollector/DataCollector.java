package com.myapps.saketh.attributecollector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataCollector extends Service {
    SensorManager senseManager;
    Sensor acc;
    SensorEventListener accListener;
    Sensor gyro;
    SensorEventListener gyroListener;
    Sensor grav;
    SensorEventListener gravListener;
    Sensor mag;
    SensorEventListener magSenseListener;
    double[] senseVals;
    int state = 0, count =0;

    CSVWriter csvWriter;
    File attributeFile;
    SharedPreferences prefs;

    String activity;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Serviced", Toast.LENGTH_SHORT).show();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String serviceID = this.toString();
        Log.d("ServAce", serviceID);

        senseManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        acc = senseManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = senseManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        grav = senseManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mag = senseManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(mag == null)
            Log.d("MAG", "NULL");
        senseVals = new double[13];
        String filePath = Environment.DIRECTORY_DOCUMENTS+"\\attribute.csv";
        attributeFile = new File("sdcard/attribute.csv");
        Constants.AttributeFilePath = attributeFile.getPath();
        activity = "Unknown";

        try {
            csvWriter = new CSVWriter(new FileWriter(attributeFile, true), ',');
            csvWriter.writeNext(Constants.Headers);
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        accListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(state == 0){
                    senseVals[0] = System.currentTimeMillis();
                    senseVals[1] = event.values[0];
                    senseVals[2] = event.values[1];
                    senseVals[3] = event.values[2];
                    state = 1;
                    Log.d("ServAce", "Still running");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        gyroListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(state == 1){
                    senseVals[4] = event.values[0];
                    senseVals[5] = event.values[1];
                    senseVals[6] = event.values[2];
                    state = 2;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };


        gravListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(state == 2){
                    senseVals[7] = event.values[0];
                    senseVals[8] = event.values[1];
                    senseVals[9] = event.values[2];
                    state = 3;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        magSenseListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(state == 3){
                    senseVals[10] = event.values[0];
                    senseVals[11] = event.values[1];
                    senseVals[12] = event.values[2];
                    state = 4;
                }
                activity = Constants.activity;
                writeToFile();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        senseManager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_NORMAL);
        senseManager.registerListener(gyroListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        senseManager.registerListener(gravListener, grav, SensorManager.SENSOR_DELAY_NORMAL);
        senseManager.registerListener(magSenseListener, mag, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    public void writeToFile(){
        if(++count > 100) {
            Log.d("ServAce", "Counted");
            this.stopSelf();
        }
        String[] nextLine = new String[14];
        nextLine[13] = activity;
        if(state == 4){
            state = 0;
            for(int i=0;i<10;++i){
                nextLine[i] = String.valueOf(senseVals[i]);
            }
            try {

                csvWriter = new CSVWriter(new FileWriter(attributeFile, true), ',');
                csvWriter.writeNext(nextLine);
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        senseManager.unregisterListener(accListener);
        senseManager.unregisterListener(gyroListener);
        senseManager.unregisterListener(gravListener);
        senseManager.unregisterListener(magSenseListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
