package com.myapps.saketh.attributecollector;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FollowerActivity extends AppCompatActivity {

    Intent dataCollIntent;
    Intent collIntent;
    Button btnLogin;
    EditText eTxtUser;
    EditText eTxtGrpLeader;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        collIntent = new Intent(this, AttributeCollectionActivity.class);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String toast = msg.getData().getString("Toast");
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        };

        eTxtUser = (EditText) findViewById(R.id.eTxtUserName);
        eTxtGrpLeader= (EditText) findViewById(R.id.eTxtLeaderName);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = eTxtUser.getText().toString();
                        if(!userName.equals("")) {

                            userName = userName.toLowerCase().trim();
                            Constants.user = userName;
                            if (eTxtGrpLeader.getText() != null)
                                Constants.grpLeader = eTxtGrpLeader.getText().toString();
                            startActivity(collIntent);
                        }
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString("Toast", "Type in your Name in the UserName field");
                            Message msg = new Message();
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                    }
                }
        );
    }
}
