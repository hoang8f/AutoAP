package info.hoang8f.autoap;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tranvu on 3/12/14.
 */
public class WifiAPUtils {

    public static String SECURE_OPEN = "Open";
    public static String SECURE_WPA = "WPA";
    public static String SECURE_WPA2 = "WPA2";
    public static int PASS_MIN_LENGHT = 8;
    public String ssid = "Free WiFi Hotspot";
    public String securityType = SECURE_OPEN;
    public String password = "12345678";

    WifiManager mWifiManager;
    Context context;
    SharedPreferences mSharedPrefs;


    public WifiAPUtils(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mSharedPrefs = context.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE);
    }

    public boolean setAP(boolean shouldOpen) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = mSharedPrefs.getString(Constants.PREFS_SSID, ssid);
        securityType = mSharedPrefs.getString(Constants.PREFS_SECURITY, securityType);
        password = mSharedPrefs.getString(Constants.PREFS_PASSWORD, password);
        if (securityType.equals(SECURE_OPEN)) {
            configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        } else {
            if (securityType.equals((SECURE_WPA))) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            } else if (securityType.equals(SECURE_WPA2)) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            }
            configuration.preSharedKey = password;
        }
        mWifiManager.setWifiEnabled(false);
        try {
            //USE REFLECTION TO GET METHOD "SetWifiAPEnabled"
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(mWifiManager, configuration, shouldOpen);
            return true;
        } catch (NoSuchMethodException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return false;
        }
    }

    public void enableWifi() {
        mWifiManager.setWifiEnabled(true);
    }

    public boolean isWifiApEnable() {
        boolean isWifiApEnable = false;
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for (Method method : mMethods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    isWifiApEnable = (Boolean) method.invoke(mWifiManager);
                } catch (IllegalArgumentException e) {
                    Crashlytics.logException(e);
                } catch (IllegalAccessException e) {
                    Crashlytics.logException(e);
                } catch (InvocationTargetException e) {
                    Crashlytics.logException(e);
                }
                break;
            }
        }
        return isWifiApEnable;
    }
}
