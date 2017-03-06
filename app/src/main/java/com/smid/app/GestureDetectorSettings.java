package com.smid.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;

import com.smid.app.smid.R;

public class GestureDetectorSettings extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String KEY_PREF_SHOW_HELP_AT_TOP = "show_help_at_top";
    public static final String KEY_PREF_START_STICKY = "pref_start_sticky";
    public static final String KEY_PREF_USE_WAKE_LOCK = "pref_use_wake_lock";
    public static final String KEY_PREF_RUN_FOREGROUND = "pref_run_foreground";
    public static final String KEY_PREF_ALLOW_REGISTERING_GESTURE = "PREF_ALLOW_REGISTERING_GESTURE";
    public static final String KEY_PREF_SHOW_IMAGE = "pref_gesture_detection_show_image";
    public static final String ACC_TEST_PERFORMED = "perf_acc_test_performed";
    public static final String KEY_PREF_DEFAULT_SERVICES_LOADED = "pref_default_services_loaded";
    public static final String KEY_PREF_PLAY_SOUND = "pref_gesture_detection_play_sound";
    public static final String KEY_PREF_WORK_AS_TASKER_PLUGIN = "pref_work_as_tasker_plugin";


    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals(KEY_PREF_START_STICKY)) {
                Helper.restartService(GestureDetectorSettings.this, new Intent(GestureDetectorSettings.this, GestureDetectorSettings.class));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefsFragment pp = new PrefsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, pp).commit();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(listener);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        listener.onSharedPreferenceChanged(sharedPreferences, key);
    }


    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);


        }
    }
}
