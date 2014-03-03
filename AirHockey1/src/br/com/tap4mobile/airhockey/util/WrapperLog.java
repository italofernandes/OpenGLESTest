package br.com.tap4mobile.airhockey.util;

import android.util.Log;

public class WrapperLog {
	public static final boolean ON = true;

	public static void log(String tag, String msg){
		if (ON) {
			Log.d(tag, msg);
		}
	}
}
