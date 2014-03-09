package edu.congyuandong.checkei.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 获取手机的各种信息
 * 
 * @author congyuandong
 * @date 20130507 21:27:37
 */

public class GetStatus {
	/**
	 * @return 手机型号
	 */
	public String getModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * @return 系统版本
	 */
	public String getRelease() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * @return 分辨率
	 */
	public String getMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		return String.valueOf(dm.widthPixels) + "*"
				+ String.valueOf(dm.heightPixels);
	}

	/**
	 * @return IMEI
	 */
	public String getIMEI(Context context) {
		TelephonyManager tel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tel.getDeviceId();
	}
	
	/**
	 * @return 系统时间
	 */
	public String getTime(){
		Date date=new Date();  
	    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    String time=formatter.format(date);
	    return time;
	}
		
	/**
	 * 获取手机服务商信息
	 */
	public String getProvidersName(Context context) {
		String ProvidersName = "N/A";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String IMSI = telephonyManager.getSubscriberId();
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			System.out.println(IMSI);
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				ProvidersName = "中国移动";
			} else if (IMSI.startsWith("46001")) {
				ProvidersName = "中国联通";
			} else if (IMSI.startsWith("46003")) {
				ProvidersName = "中国电信";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ProvidersName;
	}

	/**
	 * 
	 * @param context
	 * @return 联网类型
	 */
	public String getNetWork(Context context) {
		ConnectivityManager connectionManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			String net = networkInfo.getTypeName();
			if (net.equals("WIFI"))
				return "WIFI";
			if (net.equals("mobile"))
				return "3G";
			return "UNKOWN";
		} else {
			return "无网络";
		}
		// return networkInfo.getTypeName();
	}

	/**
	 * 获取手机应用信息
	 */
	// public AppInfo getAppInfo(Context context){
	// AppInfo appInfo = new AppInfo();
	//
	// List<PackageInfo> packages =
	// context.getPackageManager().getInstalledPackages(0);
	// //PackageInfo packageInfo = packages.get(0);
	//
	// ActivityManager mgr = (ActivityManager)
	// context.getSystemService(Context.ACTIVITY_SERVICE);
	// RunningTaskInfo info = mgr.getRunningTasks(1).get(0);
	//
	// for(int i=0;i<packages.size();i++) {
	// PackageInfo packageInfo = packages.get(i);
	// if(packageInfo.packageName.equals(info.topActivity.getPackageName())){
	// appInfo.appName =
	// packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
	// appInfo.packageName = packageInfo.packageName;
	// appInfo.versionName = packageInfo.versionName;
	// appInfo.versionCode = packageInfo.versionCode;
	// return appInfo;
	// }
	// }
	// //Toast.makeText(this, info.topActivity.getPackageName(),
	// Toast.LENGTH_LONG).show();
	// return appInfo;
	// }

}
