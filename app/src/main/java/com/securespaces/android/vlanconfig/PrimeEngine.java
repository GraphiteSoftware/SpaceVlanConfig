package com.securespaces.android.vlanconfig;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.securespaces.android.ssm.SpacesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;



public class PrimeEngine {

    private Context mContext = null;
    private SpacesManager mSpacesManager = null;
    private Handler mHandler = null;
    private final static String TAG = "PrimeEngine";
    private final static String INET_ADDRESS_TOKEN = "inet addr";
    public static final String EXTRA_USERID = "EXTRA_USERID";

    public PrimeEngine(Context ctx){
        mContext = ctx;
        mHandler = new Handler(Looper.getMainLooper());
        mSpacesManager = new SpacesManager(mContext);
    }

    public String listInterface(int userId) {
        String data = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(
                new FileReader("/data/media/"+userId+"/Android/data/ss_if_list.txt"));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            data = buffer.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    public List<String> listVlans(int userId) {
        ArrayList<String> data = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(
                new FileReader("/data/media/"+userId+"/Android/data/ss_if_list.txt"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if ((line.contains("eth") || line.contains("wlan"))) {
                    String lineTmp[] = line.split(" ");
                    if (lineTmp[0] != null && lineTmp[0].length() > 2 && lineTmp[0].contains(".")) {
                        Log.d(TAG, "The exact line: "+lineTmp[0]);
                        StringBuilder buffer = new StringBuilder();
                        if (data == null) data = new ArrayList <String>();
                        buffer.append("VLAN: "+lineTmp[0]);
                        String nextLine = bufferedReader.readLine();
                        Log.d(TAG, "The next line: "+nextLine);
                        if (nextLine != null && nextLine.contains(INET_ADDRESS_TOKEN)) {
                            String ipAddr = nextLine.substring(nextLine.indexOf(":")+1);
                            buffer.append(", IP address: "+ipAddr);
                            Log.d(TAG, "Found line inet addr: "+ipAddr);
                        }
                        buffer.append("\n");
                        data.add(buffer.toString());
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
