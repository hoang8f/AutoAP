package info.hoang8f.autoap.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import info.hoang8f.autoap.R;
import info.hoang8f.autoap.WifiAPUtils;

/**
 * Created by hoang8f on 2/26/14.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static String SEND_INTENT = "intent";
    public static String TURN_ON_AP = "autoap.turnOn";
    public static String TURN_OFF_AP = "autoap.turnOff";
    public static String CHANGE_WIDGET_ON = "autoap.change.on";
    public static String CHANGE_WIDGET_OFF = "autoap.change.off";
    private WifiAPUtils mWifiAPUtils;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mWifiAPUtils = new WifiAPUtils(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.one_click_widget);

        Log.d("APWidget", "Done");
        if (mWifiAPUtils.isWifiApEnable()) {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_enabled);
            remoteViews.setOnClickPendingIntent(R.id.widget_img, clickToTurnOffAP(context));
        } else {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_disabled);
            remoteViews.setOnClickPendingIntent(R.id.widget_img, clickToTurnOnAP(context));
        }
        pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent clickToTurnOffAP(Context context) {
        Intent intent = new Intent();
        intent.setAction(TURN_OFF_AP);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent clickToTurnOnAP(Context context) {
        Intent intent = new Intent();
        intent.setAction(TURN_ON_AP);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
