package com.corner23.android.beautyclocklivewallpaper.widget;

import java.io.File;

import com.corner23.android.beautyclocklivewallpaper.R;
import com.corner23.android.beautyclocklivewallpaper.Settings;
import com.corner23.android.beautyclocklivewallpaper.services.UpdateService;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
	private static final String TAG = "WidgetService";
	
	private static final String DISPLAYTIME_FORMAT = "%02d:%02d";
	
	private Time mTime = new Time();

	private int mScreenHeight = 0;
	private int mScreenWidth = 0;
	
	private String mStorePath = null;
	private SharedPreferences mPrefs;
	private Bitmap mBeautyBitmap = null;
	
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive");
			updateWidget(context);
		}
	};

	private void updateImageOfRemoteWidget(Context context, int id) {
		if (context != null) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			ComponentName remoteWidget = new ComponentName(context, WidgetProvider.class);
			AppWidgetManager awm = AppWidgetManager.getInstance(context);  

			if (remoteViews != null && remoteWidget != null && awm != null) {
				remoteViews.setImageViewResource(R.id.BeautyClockImageView, id);

				mTime.setToNow();
				remoteViews.setTextViewText(R.id.TimeTextView, String.format(DISPLAYTIME_FORMAT, mTime.hour, mTime.minute));
				
				Intent intent = new Intent(this, Settings.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				remoteViews.setOnClickPendingIntent(R.id.BeautyClockImageView, pendingIntent);
				
				awm.updateAppWidget(remoteWidget, remoteViews);
			}
		}
	}
	
	private void updateImageOfRemoteWidget(Context context, Bitmap bitmap) {
		if (context != null) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			ComponentName remoteWidget = new ComponentName(context, WidgetProvider.class);
			AppWidgetManager awm = AppWidgetManager.getInstance(context);  

			if (remoteViews != null && remoteWidget != null && awm != null) {
				if (bitmap == null) {
//					remoteViews.setImageViewResource(R.id.BeautyClockImageView, R.drawable.beautyclock_loading);
				} else {
					remoteViews.setImageViewBitmap(R.id.BeautyClockImageView, bitmap);
				}
				mTime.setToNow();
				remoteViews.setTextViewText(R.id.TimeTextView, String.format(DISPLAYTIME_FORMAT, mTime.hour, mTime.minute));

				Intent intent = new Intent(context, Settings.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
				remoteViews.setOnClickPendingIntent(R.id.BeautyClockImageView, pendingIntent);
				
				awm.updateAppWidget(remoteWidget, remoteViews);
			}
		}
	}

	private Bitmap updateBeautyBitmap() {
		mTime.setToNow();		
		int hour = mTime.hour;
		int minute = mTime.minute;
		
		// check SD card first
		String fname = String.format("%s/%02d%02d.jpg", mStorePath, hour, minute);
		File _f_sdcard = new File(fname);
		if (!_f_sdcard.exists()) {
			fname = String.format("%s/%02d%02d.jpg", getCacheDir().getAbsolutePath(), hour, minute);
		}
		
		Log.d(TAG, fname);
		return BitmapFactory.decodeFile(fname);
	}

	private Bitmap ResizeBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			Log.d(TAG, "null !");
			return null;
		}
		
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width == 0 || height == 0) {
			return null;
		}
		
		if (width < mScreenWidth && height < mScreenHeight) {
			return null;
		}
				
		int maxWidth = (int) (mScreenWidth * 0.9);
		int maxHeight = (int) (mScreenHeight * 0.7);
/*		
		if (height > maxHeight) {
			double ratio = (double) maxHeight / height;
			height = maxHeight;
			width = (int) (width * ratio);
			bScaled = true;
		}
		
		if (width > maxWidth) {
			double ratio = (double) maxWidth / width;
			width = maxWidth;
			height = (int) (height * ratio);
			bScaled = true;
		}
*/
		if (height > width) {
			double ratio = (double) maxHeight / height;
			height = maxHeight;
			width = (int) (width * ratio);
		} else {
			double ratio = (double) maxWidth / width;
			width = maxWidth;
			height = (int) (height * ratio);
		}
		
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}
		
	private void updateWidget(Context context) {
		Log.i(TAG, "updateWidget");
		
		Bitmap bitmap = updateBeautyBitmap();
		if (bitmap == null) {
			updateImageOfRemoteWidget(this, R.drawable.beautyclock_retry);
		} else {
			Log.i(TAG, "bitmap not null");
			Bitmap bitmap_scaled = ResizeBitmap(bitmap);
			if (bitmap_scaled != null) {
				mBeautyBitmap = bitmap_scaled;
			} else {
				mBeautyBitmap = bitmap;
			}
			updateImageOfRemoteWidget(context, mBeautyBitmap);
		}
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");

		// register notification
		IntentFilter filter = new IntentFilter();  
    	filter.addAction(UpdateService.BROADCAST_WALLPAPER_UPDATE);
		registerReceiver(mBroadcastReceiver, filter);
		
		mPrefs = getSharedPreferences(Settings.SHARED_PREFS_NAME, 0);
    	mPrefs.registerOnSharedPreferenceChangeListener(this);
    	onSharedPreferenceChanged(mPrefs, null);		
    	
		startService(new Intent(this, UpdateService.class));
	}
	
	public void onDestroy() {
		Log.i(TAG, "onDestroy");

		unregisterReceiver(mBroadcastReceiver);
		
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		onStartCommand(intent, 0, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		
		mScreenHeight = getResources().getDisplayMetrics().heightPixels;
		mScreenWidth = getResources().getDisplayMetrics().widthPixels;
		
		updateWidget(this);
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if (prefs == null) {
			return;
		}
		
		if (key == null) {
			mStorePath = prefs.getString(Settings.PREF_INTERNAL_PICTURE_PATH, "");
			return;
		}
		
		if (key.equals(Settings.PREF_INTERNAL_PICTURE_PATH)) {
			mStorePath = prefs.getString(Settings.PREF_INTERNAL_PICTURE_PATH, "");
		}
	}
}
