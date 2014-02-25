package info.hoang8f.autoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private Switch mSwitch;
    private WifiManager mWifiManager;
    private ImageView mTetheringImage;
    private TextView mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        mSwitch = (Switch) findViewById(R.id.ap_button);
        mTetheringImage = (ImageView) findViewById(R.id.tethering_image);
        mDescription = (TextView) findViewById(R.id.description);

        mSwitch.setOnCheckedChangeListener(this);
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //TODO Add more settings
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //Turn on AP
            enableAP();
        } else {
            //Turn off AP
            disableAP();
        }
    }

    private boolean enableAP() {
        if (setAP(true)) {
            mTetheringImage.setImageResource(R.drawable.wifi_enabled);
            mDescription.setText(R.string.tethering_off);
            return true;
        }
        showMessage(R.string.failed_on);
        return false;
    }

    private boolean disableAP() {
        if (setAP(false)) {
            mTetheringImage.setImageResource(R.drawable.wifi_disabled);
            mDescription.setText(R.string.tethering_on);
            enableWifi();
            return true;
        }
        showMessage(R.string.failed_off);
        return false;
    }

    private boolean setAP(boolean shouldOpen) {
        WifiConfiguration wifi_configuration = new WifiConfiguration();
        wifi_configuration.SSID ="AutoAP Access Point";
        wifi_configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        mWifiManager.setWifiEnabled(false);
        try {
            //USE REFLECTION TO GET METHOD "SetWifiAPEnabled"
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(mWifiManager, wifi_configuration, shouldOpen);
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void enableWifi() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    private void showMessage(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message);
        builder.setMessage(message);
        builder.create().show();
    }
}
