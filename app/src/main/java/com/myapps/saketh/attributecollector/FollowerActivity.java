package com.myapps.saketh.attributecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FollowerActivity extends AppCompatActivity {

    Intent dataCollIntent;
    Intent collIntent;
    Button btnLogin;
    EditText eTxtUser;
    EditText eTxtGrpLeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
        dataCollIntent = new Intent(this, DataCollector.class);
        collIntent = new Intent(this, AttributeCollectionActivity.class);

        eTxtUser = (EditText) findViewById(R.id.eTxtUserName);
        eTxtGrpLeader= (EditText) findViewById(R.id.eTxtLeaderName);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = eTxtUser.getText().toString();
                        userName = userName.toLowerCase().trim();
                        Constants.user = userName;
                        if(eTxtGrpLeader.getText() != null)
                            Constants.grpLeader = eTxtGrpLeader.getText().toString();
                        startService(dataCollIntent);
                        startActivity(collIntent);
                    }
                }
        );
    }
}
