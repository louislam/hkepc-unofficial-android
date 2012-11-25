package net.louislam.hkepc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Settings {

	public static String get(Context c, String key) {
		  SharedPreferences appSharedPrefs = PreferenceManager
		  .getDefaultSharedPreferences(c);
		  return appSharedPrefs.getString(key, "");
	}

	public static void set(Context c, String key, String value) {
		SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
		Editor prefsEditor = appSharedPrefs.edit();
		prefsEditor.putString(key, value);
		prefsEditor.commit();
	}
	
}
