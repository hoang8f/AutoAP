package info.hoang8f.autoap.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import info.hoang8f.autoap.R;
import info.hoang8f.autoap.WifiAPUtils;

/**
 * Created by tranvu on 3/8/14.
 */
public class WidgetBroadcastReceiver extends BroadcastReceiver {
    WifiAPUtils mWifiAPUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        mWifiAPUtils = new WifiAPUtils(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.one_click_widget);
        if (intent.getAction().equals(WidgetProvider.TURN_ON_AP)) {
            if (mWifiAPUtils.setAP(true)) {
                remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_enabled);
                remoteViews.setOnClickPendingIntent(R.id.widget_img, WidgetProvider.clickToTurnOffAP(context));
            }
        } else if (intent.getAction().equals(WidgetProvider.TURN_OFF_AP)) {
            if (mWifiAPUtils.setAP(false)) {
                mWifiAPUtils.enableWifi();
                remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_disabled);
                remoteViews.setOnClickPendingIntent(R.id.widget_img, WidgetProvider.clickToTurnOnAP(context));
            }
        } else if (intent.getAction().equals(WidgetProvider.CHANGE_WIDGET_ON)) {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_enabled);
        } else if (intent.getAction().equals(WidgetProvider.CHANGE_WIDGET_OFF)) {
            remoteViews.setImageViewResource(R.id.widget_img, R.drawable.wifi_disabled);
        }
        WidgetProvider.pushWidgetUpdate(context, remoteViews);
    }
}
