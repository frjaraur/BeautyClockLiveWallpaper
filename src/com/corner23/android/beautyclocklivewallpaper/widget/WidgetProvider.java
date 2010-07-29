package com.corner23.android.beautyclocklivewallpaper.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetProvider extends AppWidgetProvider {
	private static final String TAG = "WidgetProvider";
	
	@Override
	public void onEnabled(Context context) {
		Log.i(TAG, "onEnabled");
		context.startService(new Intent(context, WidgetService.class));
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Log.i(TAG, "onDisabled");
		context.stopService(new Intent(context, WidgetService.class));
		super.onDisabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(TAG, "onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.i(TAG, "onDeleted" );

		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		
		final String action = intent.getAction(); 
	    if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) { 
	        final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID); 
	        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) { 
	            this.onDeleted(context, new int[] { appWidgetId }); 
	        } 
	    } else { 
	        super.onReceive(context, intent); 
	    } 
	}
}
