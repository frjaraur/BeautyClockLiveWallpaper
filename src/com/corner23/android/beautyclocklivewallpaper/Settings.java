package com.corner23.android.beautyclocklivewallpaper;

import java.io.File;

import com.corner23.android.beautyclocklivewallpaper.services.DeadWallpaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity 
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String SHARED_PREFS_NAME = "bclw_settings";
	
    public static final String PREF_FETCH_WHEN_SCREEN_OFF = "fetch_screen_off";
    public static final String PREF_RING_HOURLY = "ring_hourly";
    public static final String PREF_SAVE_COPY = "save_copy";
    public static final String PREF_FIT_SCREEN = "fit_screen";
    public static final String PREF_NO_SCROLL = "no_scroll";
    public static final String PREF_FETCH_LARGER_PICTURE = "fetch_larger_picture";
    public static final String PREF_PICTURE_SOURCE = "picture_source";
    public static final String PREF_ENABLE_DEADWALLPAPER = "bcdw_enable";
    public static final String PREF_PICTURE_PER_FETCH = "picture_per_fetch";
    public static final String PREF_INTERNAL_PICTURE_PATH = "picture_path";
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName(SHARED_PREFS_NAME);
		File secretParadise = new File(Environment.getExternalStorageDirectory() + "/BeautyClock/givemepower");
		if (secretParadise.exists()) {
			addPreferencesFromResource(R.xml.preferences_secret);
		} else {
	        addPreferencesFromResource(R.xml.preferences);
		}
/*		
        Preference enable_dead_lw = this.findPreference(PREF_ENABLE_DEADWALLPAPER);
        if (enable_dead_lw != null) {
        	if (Double.parseDouble(android.os.Build.VERSION.RELEASE) >= 2.1) {
        		enable_dead_lw.setEnabled(false);
    }
}
*/        
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	if (key == null) {
    		return;
    	}
    	
    	if (key.equals(PREF_ENABLE_DEADWALLPAPER)) {
    		boolean enable = prefs.getBoolean(PREF_ENABLE_DEADWALLPAPER, false);
    		Intent intent = new Intent(this, DeadWallpaper.class);
    		if (enable) {
        		startService(intent);
    		} else {
    			stopService(intent);
    		}
    	}
    }
}

