package info.hoang8f.autoap.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import info.hoang8f.autoap.R;

/**
 * Created by hoang8f on 2/26/14.
 */

public class WidgetProvider extends AppWidgetProvider {
    static String SEND_INTENT="intent";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.one_click_widget);

        Log.d("APWidget","Done");
        if (isWifiApEnable(context)) {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_enabled);
            remoteViews.setOnClickPendingIntent(R.id.widget_img, clickToTurnOffAP(context));
        } else {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_disabled);
            remoteViews.setOnClickPendingIntent(R.id.widget_img, clickToTurnOnAP(context));
        }
//       remoteViews.setOnClickPendingIntent(R.id.widget_img, clickToTurnOnAP(context));
        pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent clickToTurnOffAP(Context context) {
        Intent intent = new Intent();
        intent.setAction("autoap.turnOff");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent clickToTurnOnAP(Context context) {
        Intent intent = new Intent();
        intent.setAction("autoap.turnOn");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    public boolean isWifiApEnable(Context context) {
        boolean isWifiApEnable = false;
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Method[] mMethods = manager.getClass().getDeclaredMethods();
        for (Method method : mMethods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    isWifiApEnable = (Boolean) method.invoke(manager);
                } catch (IllegalArgumentException e) {

                } catch (IllegalAccessException e) {

                } catch (InvocationTargetException e) {

                }
                break;
            }
        }
        return isWifiApEnable;
    }
}
