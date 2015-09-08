package com.myapps.saketh.attributecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdminLogin extends AppCompatActivity {

    EditText txtUsername;
    Button btnLogin;
    String[] usernames;
    boolean state;

    Intent mainActIntent;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        usernames = "saketh#prerna#Aaron".split("#");

        state = false;

        mainActIntent = new Intent(this, AttributeCollectionActivity.class);
        serviceIntent = new Intent(this, DataCollector.class);

        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = txtUsername.getText().toString();
                        userName = userName.toLowerCase().trim();
                        for(String x: usernames){
                            if(x.equals(userName)){
                                state = true;
                                Constants.user = userName;
                                startService(serviceIntent);
                                startActivity(mainActIntent);
                            }
                        }
                        if(!state){
                            Toast.makeText(getApplicationContext(), "Wrong username!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


    }
}
