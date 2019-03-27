package com.securespaces.android.vlanconfig;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.EditText;

import com.securespaces.android.vlanconfig.R;

import java.util.List;

public class ConfigPrefFrag extends PreferenceFragment {
    private static final String TAG = "ConfigPrefFrag";
    private UpdatingEditTextPreference mLinkPref, mIFName, mVlanId;
    private PreferenceScreen mPrefCat;
    private PrimeEngine mEngine = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_prefs);
        mPrefCat = getPreferenceScreen();
        mLinkPref = (UpdatingEditTextPreference) findPreference(MainActivity.LINK_ADDRESS_KEY);
        mIFName = (UpdatingEditTextPreference) findPreference(MainActivity.IFNAME_KEY);
        mVlanId = (UpdatingEditTextPreference) findPreference(MainActivity.VLAN_ID_KEY);
    }

    public void setPrimeEngine(Context context) {
        mEngine = new PrimeEngine(context);
    }

    public void setPrefsEnabled(boolean enabled) {
        mPrefCat.setEnabled(enabled);
    }

    public void clearPref(){
        mVlanId.setText("");
        mIFName.setText("");
        mLinkPref.setText("");
    }

    public Bundle createConfigBundle() {

        Bundle bundle = new Bundle();
        if (isNonNullAndNonEmpty(mIFName.getText())) {
            bundle.putString(MainActivity.IFNAME_KEY, mIFName.getText());
        }

        if (isNonNullAndNonEmpty(mVlanId.getText())) {
            bundle.putString(MainActivity.VLAN_ID_KEY, mVlanId.getText());
        }

        if (isNonNullAndNonEmpty(mLinkPref.getText())) {
            bundle.putString(MainActivity.LINK_ADDRESS_KEY, mLinkPref.getText());
        }

        return bundle;
    }

    private boolean isNonNullAndNonEmpty(String s) {
        return (s != null) && (!s.isEmpty());
    }
}
