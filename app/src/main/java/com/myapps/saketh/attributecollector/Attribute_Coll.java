package com.myapps.saketh.attributecollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Attribute_Coll extends AppCompatActivity {


    Button btnGrpLdr;
    Button btnFollower;
    Intent grpLdrIntent;
    Intent followerIntent;
    AdminLogin grpLeaderLoginPage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_coll);

        btnGrpLdr = (Button) findViewById(R.id.btnGrpLdr);
        btnFollower = (Button) findViewById(R.id.btnFollower);
        grpLdrIntent = new Intent(this, AdminLogin.class);
        followerIntent = new Intent(this, FollowerActivity.class);

        btnGrpLdr.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(grpLdrIntent);
                    }
                }
        );

        btnFollower = (Button) findViewById(R.id.btnFollower);
        btnFollower.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(followerIntent);
                    }
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


}
