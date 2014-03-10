package edu.congyuandong.checkei.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @Author: congyuandong
 * @time 2013年10月7日 01:23:45
 * @Message: 设置信息
 **/
public class SystemSettings {

	private static SharedPreferences getSharePreferences(Context ctx) {
		return PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	private static SharedPreferences.Editor getSharePreferencesEditor(
			Context ctx) {
		return getSharePreferences(ctx).edit();
	}

	public static String getSettingMessage(Context ctx, String key,
			String defaultVaule) {
		SharedPreferences settings = getSharePreferences(ctx);
		return settings.getString(key, defaultVaule);
	}

	public static void setSettingMessage(Context ctx, String key, String value) {
		SharedPreferences.Editor editor = getSharePreferencesEditor(ctx);
		editor.putString(key, value);
		editor.commit();
	}

	private static boolean getSettingMessage(Context ctx, String key,
			boolean defaultVaule) {
		SharedPreferences settings = getSharePreferences(ctx);
		return settings.getBoolean(key, defaultVaule);
	}

	private static void setSettingMessage(Context ctx, String key, boolean value) {
		SharedPreferences.Editor editor = getSharePreferencesEditor(ctx);
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static Integer getSettingMessage(Context ctx, String key,
			int defaultVaule) {
		SharedPreferences settings = getSharePreferences(ctx);
		return settings.getInt(key, defaultVaule);
	}

	public static void setSettingMessage(Context ctx, String key, int value) {
		SharedPreferences.Editor editor = getSharePreferencesEditor(ctx);
		editor.putInt(key, value);
		editor.commit();
	}

	private static Long getSettingMessage(Context ctx, String key,
			long defaultVaule) {
		SharedPreferences settings = getSharePreferences(ctx);
		return settings.getLong(key, defaultVaule);
	}

	private static void setSettingMessage(Context ctx, String key, long value) {
		SharedPreferences.Editor editor = getSharePreferencesEditor(ctx);
		editor.putLong(key, value);
		editor.commit();
	}

}
