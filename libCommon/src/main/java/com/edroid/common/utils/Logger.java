package com.edroid.common.utils;

import java.io.File;

import android.os.Environment;
import android.util.Log;

/**
 * 
 * 日志打印模块
 * 
 * @author Yichou 2013-12-12
 * 
 * <p>2014-10-10 本地调试标志，默认是否开启依据本地调试
 */
public final class Logger {
	/**
	 * 本机是否开启调试
	 */
	public static final boolean LOCAL_DEBUG_ON = new File(Environment.getExternalStorageDirectory(), "debug").exists();
	
	private boolean debug;
	private String tag;
	private String tmpTag;
	

	public void setTagPerfix(String tagPerfix) {
		if(tagPerfix == null)
			tag = tmpTag;
		else {
			tag = tagPerfix + tag;
		}
	}
	
	private Logger(boolean debug, String tag) {
		setDebug(debug);
		setTag(tag);
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
		this.tmpTag = tag;
	}
	
	public String getTag() {
		return tmpTag;
	}
	
	public static Logger create(boolean debug, String tag) {
		return new Logger(debug, tag);
	}

	public static Logger create(String tag) {
		return new Logger(LOCAL_DEBUG_ON, tag);
	}

	public static Logger create() {
		return new Logger(LOCAL_DEBUG_ON, null);
	}
	
	/**
	 * 注意：Log 不允许 msg = null ，否则报空指针异常 2013-7-3 yichou
	 * 
	 * @param tag
	 * @param msg
	 */
	public void i(String msg) {
		i(tag, msg);
	}

	public void d(String msg) {
		d(tag, msg);
	}

	public void e(String msg) {
		e(tag, msg);
	}

	public void v(String msg) {
		v(tag, msg);
	}

	public void w(String msg) {
		w(tag, msg);
	}
	
	public void i(String tag, String msg) {
		if (debug) {
			Log.i(tag, msg != null ? msg : "");
		}
	}

	public  void d(String tag, String msg) {
		if (debug) {
			Log.d(tag, msg != null ? msg : "");
		}
	}

	public void e(String tag, String msg) {
		if (debug) {
			Log.e(tag, msg != null ? msg : "");
		}
	}

	public void v(String tag, String msg) {
		if (debug) {
			Log.v(tag, msg != null ? msg : "");
		}
	}

	public void w(String tag, String msg) {
		if (debug) {
			Log.w(tag, msg != null ? msg : "");
		}
	}
}
