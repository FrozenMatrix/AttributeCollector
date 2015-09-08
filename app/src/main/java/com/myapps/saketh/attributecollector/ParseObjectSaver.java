package com.myapps.saketh.attributecollector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.parse.Parse;
import com.parse.ParseObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class ParseObjectSaver extends Service {
    Vector<ParseObject> parseObjects;
    CSVIterator ite;
    int lineNum = 0;
    int Total = 0;
    public ParseObjectSaver() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        parseObjects = new Vector<>(0);
        Log.d("LOG", "StartCommand");

        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), Constants.ParseApplicationID, Constants.ParseClientKey);

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(Constants.AttributeFilePath));

            ite = (CSVIterator) csvReader.iterator();
            while(ite.hasNext()){
                ++Total;
                if(ite.next()[0].equals("ParseMark"))
                    lineNum = Total;
            }

            try {
                CSVWriter csvWriter = new CSVWriter(new FileWriter(Constants.AttributeFilePath, true), ',');
                csvWriter.writeNext(new String[]{"ParseMark"});
                csvWriter.close();
                Log.d("LOL", "Starting parse");

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                csvReader.close();
                csvReader = new CSVReader(new FileReader(Constants.AttributeFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ite = (CSVIterator) csvReader.iterator();
            Log.d("LOL", "ite test : "+ite.hasNext());

            if(lineNum != 0)
                for(int i=0;i<lineNum && ite.hasNext();i++){
                    ite.next();
                }

            while(ite.hasNext()) {
                String[] temp = ite.next();
                if(temp[0].equals(Constants.Headers[0]))
                    continue;
                if(temp[0].equals("ParseMark"))
                    break;
                ParseObject object = new ParseObject(Constants.user);
                for(int i=0;i<temp.length;i++)
                    object.put(Constants.Headers[i], temp[i]);
                parseObjects.add(object);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Thread parseSaver = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Iterator<ParseObject> it = parseObjects.iterator();
                        while(it.hasNext())
                        {
                            try {
                                Log.d("LOL", "Adding");
                                it.next().saveInBackground().waitForCompletion();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.d("LOL", "Done : "+it.hasNext());
                        }
                    }
                }
        );

        parseSaver.start();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
