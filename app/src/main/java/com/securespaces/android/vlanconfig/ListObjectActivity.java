package com.securespaces.android.vlanconfig;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.securespaces.android.ssm.UserUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ListObjectActivity extends Activity {

    private TextView mView = null;
    private PrimeEngine mEngine = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_object);
        mEngine = new PrimeEngine(this);
        mView = (TextView) findViewById(R.id.listInterface);
        listInterface();
    }

    private void listInterface() {
        String iFList = mEngine.listInterface(UserUtils.myUserId());
        mView.setTextSize(15);
        mView.setText("\nThe network interface: "+"\n\n"+iFList);
    }
}
