package com.securespaces.android.vlanconfig;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.securespaces.android.ssm.UserUtils;
import com.securespaces.android.vlanconfig.R;
import com.securespaces.android.ssm.SpacesManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String FRAGMENT_TAG = "frag_tag";

    private SpacesManager mSpacesManager;
    private ConfigPrefFrag mConfigFragment;
    private SharedPreferences mPrefs;
    private TextView mTextView;
    private static boolean vlanRemoved = false;
    private PrimeEngine mEngine = null;
    private Context mContext = null;

    public static final String IFNAME_KEY = "iFName";
    public static final String VLAN_ID_KEY = "vlanId";
    public static final String LINK_ADDRESS_KEY = "linkAddress";
    public static final int TYPE_VLAN_ADD = 1;
    public static final int TYPE_VLAN_REMOVE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(R.string.app_name_full);

        mContext = this;
        mSpacesManager = new SpacesManager(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEngine = new PrimeEngine(this);
        if (savedInstanceState == null) {
            mConfigFragment = new ConfigPrefFrag();
            getFragmentManager().beginTransaction().add(R.id.contentPanel, mConfigFragment, FRAGMENT_TAG).commit();
        } else {
            mConfigFragment = (ConfigPrefFrag) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
        mConfigFragment.setPrimeEngine(this);
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVlan();
            }
        });

        Button removeButton = findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeVlan();
            }
        });

        Button listButton = findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listInterface();
            }
        });

        mTextView = findViewById(R.id.currentVlanConfigTextView);
    }

    @Override
    public void onResume() {
        super.onResume();

        mConfigFragment.setPrefsEnabled(true);
        if (vlanRemoved) {
            mConfigFragment.clearPref();
        }
        displayCurrentConfig();
    }

    public void displayCurrentConfig() {

        mSpacesManager.listInterface(UserUtils.myUserId());
        List<String> lists = mEngine.listVlans(UserUtils.myUserId());
        String s = "\nCURRENT VLAN CONFIGURATION:\n\n";
        if (lists != null) {

            for (String vlan: lists) {
                s += vlan;
            }
        } else {
            s += "VLAN: \n";
            s += "IP Address: ";
        }
        mTextView.setText(s);
    }

    private String keyValueString(Bundle b, String k) {
        return k + " = " + b.getString(k);
    }


    private void addVlan() {
        Bundle bundle = new Bundle();
        bundle = mConfigFragment.createConfigBundle();

        String ifName = bundle.getString(MainActivity.IFNAME_KEY, null);
        String vlanIdStr = bundle.getString(MainActivity.VLAN_ID_KEY, null);
        String ipAddr = bundle.getString(MainActivity.LINK_ADDRESS_KEY, null);

        if (!validate(ifName, vlanIdStr)) {
            return;
        }
        int vlanId = Integer.parseInt(vlanIdStr);
        if (mSpacesManager.addVlan(ifName, vlanId, ipAddr)) {
            vlanRemoved = false;
            handleSuccess(TYPE_VLAN_ADD, ifName+"."+vlanIdStr);
        } else {
            handleError("Unable to add vlan: "+ifName+"."+vlanIdStr);
        }
    }

    private void removeVlan() {
        Bundle bundle = new Bundle();
        bundle = mConfigFragment.createConfigBundle();

        String ifName = bundle.getString(MainActivity.IFNAME_KEY, null);
        String vlanIdStr = bundle.getString(MainActivity.VLAN_ID_KEY, null);

        if (!validate(ifName, vlanIdStr)) {
            return;
        }
        String vlanName = ifName+"."+vlanIdStr;
        if (mSpacesManager.removeVlan(vlanName)) {
            vlanRemoved = true;
            handleSuccess(TYPE_VLAN_REMOVE, vlanName);
        } else {
            handleError("Unable to remove vlan: "+ifName+"."+vlanIdStr);
        }
    }

    private void listInterface() {

        Intent intent = new Intent(this, ListObjectActivity.class);
        intent.putExtra(PrimeEngine.EXTRA_USERID,UserUtils.myUserId());
        mContext.startActivityAsUser(intent, UserUtils.myUserHandle());
    }

    private boolean validate(String ifName, String vlanIdStr) {
        if (vlanIdStr == null || ifName == null){
            handleError("Invalid VLAN info");
            return false;
        }
        int vlanId = Integer.parseInt(vlanIdStr);
        if (vlanId <= 0) {
            handleError("Invalid VLAN ID: "+vlanId);
            return false;
        }
        return true;
    }

    private void handleSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.success_title)
                .setMessage(R.string.success_message)
                .show();
    }

    private void handleSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.success_title)
                .setMessage(message)
                .show();
    }

    private void handleSuccess(int type, String vlanName) {

        String message = "Invalid operation";

        switch (type) {
            case TYPE_VLAN_ADD:
                message = "VLAN: "+vlanName+" added";
                break;
            case TYPE_VLAN_REMOVE:
                message = "VLAN: "+vlanName+" removed";
                break;
            default:
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reset();
            }
        });
        builder.setTitle(R.string.success_title)
                .setMessage(message)
                .show();
    }

    private void handleError(Exception ex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_title)
                .setMessage(ex.getLocalizedMessage())
                .show();
    }

    private void handleError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error_title)
                .setMessage(message)
                .show();
    }

    private void reset() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
