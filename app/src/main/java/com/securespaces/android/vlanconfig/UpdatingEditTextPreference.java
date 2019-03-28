package com.securespaces.android.vlanconfig;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class UpdatingEditTextPreference extends EditTextPreference {

    public UpdatingEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public UpdatingEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UpdatingEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpdatingEditTextPreference(Context context) {
        super(context);
    }

    public View onCreateView(ViewGroup parent) {
        setSummary(getText());
        return super.onCreateView(parent);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            setSummary(getText());
        }
    }
}
