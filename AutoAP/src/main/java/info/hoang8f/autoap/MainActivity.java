package info.hoang8f.autoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import info.hoang8f.autoap.widget.WidgetProvider;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private Switch mSwitch;
    private WifiManager mWifiManager;
    private ImageView mTetheringImage;
    private TextView mDescription;
    private WifiAPUtils mWifiAPUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitch = (Switch) findViewById(R.id.ap_button);
        mTetheringImage = (ImageView) findViewById(R.id.tethering_image);
        mDescription = (TextView) findViewById(R.id.description);

        mWifiAPUtils = new WifiAPUtils(this);
        setmSwitch();
        mSwitch.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setmSwitch();
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

    public void setmSwitch() {
        mSwitch.setChecked(mWifiAPUtils.isWifiApEnable());
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

    public boolean enableAP() {
        if (mWifiAPUtils.setAP(true)) {
            mTetheringImage.setImageResource(R.drawable.wifi_enabled);
            mDescription.setText(R.string.tethering_off);
            Intent intent = new Intent();
            intent.setAction(WidgetProvider.CHANGE_WIDGET_ON);
            this.sendBroadcast(intent);
            return true;
        }
        showMessage(R.string.failed_on);
        return false;
    }

    public boolean disableAP() {
        if (mWifiAPUtils.setAP(false)) {
            mTetheringImage.setImageResource(R.drawable.wifi_disabled);
            mDescription.setText(R.string.tethering_on);
            mWifiAPUtils.enableWifi();
            Intent intent = new Intent();
            intent.setAction(WidgetProvider.CHANGE_WIDGET_OFF);
            this.sendBroadcast(intent);
            return true;
        }
        showMessage(R.string.failed_off);
        return false;
    }


    private void showMessage(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message);
        builder.setMessage(message);
        builder.create().show();
    }
}
