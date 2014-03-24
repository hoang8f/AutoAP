package info.hoang8f.autoap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.autoap.widget.WidgetProvider;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch mSwitch;
    private WifiManager mWifiManager;
    private ImageView mTetheringImage;
    private TextView mDescription;
    private EditText ssidEditText;
    private EditText passwordEditText;
    private Spinner spinner;
    private CheckBox checkBox;
    private Button save;
    private WifiAPUtils mWifiAPUtils;
    private String ssid;
    private String securityType;
    private String password;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_main);
        mSwitch = (Switch) findViewById(R.id.ap_button);
        mTetheringImage = (ImageView) findViewById(R.id.tethering_image);
        mDescription = (TextView) findViewById(R.id.description);
        ssidEditText = (EditText) findViewById(R.id.ssid_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        spinner = (Spinner) findViewById(R.id.spinner);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        save = (Button) findViewById(R.id.save_button);

        mWifiAPUtils = new WifiAPUtils(this);

        mSharedPrefs = this.getSharedPreferences(Constants.PREFS_KEY, Context.MODE_PRIVATE);
        ssid = mSharedPrefs.getString(Constants.PREFS_SSID, mWifiAPUtils.ssid);
        securityType = mSharedPrefs.getString(Constants.PREFS_SECURITY, mWifiAPUtils.securityType);
        password = mSharedPrefs.getString(Constants.PREFS_PASSWORD, mWifiAPUtils.password);

        setSwitchImageState();
        mSwitch.setOnCheckedChangeListener(this);
        showSpinner();
        save.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(this);
        mTetheringImage.setOnClickListener(this);

        ssidEditText.setText(ssid);
        passwordEditText.setText(password);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setSwitchImageState();
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

    public void setSwitchImageState() {
        if (mWifiAPUtils.isWifiApEnable()) {
            mSwitch.setChecked(true);
            mTetheringImage.setImageResource(R.drawable.wifi_enabled);
        } else {
            mSwitch.setChecked(false);
            mTetheringImage.setImageResource(R.drawable.wifi_disabled);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ap_button:
                if (isChecked) {
                    //Turn on AP
                    enableAP();
                } else {
                    //Turn off AP
                    disableAP();
                }
                break;
            case R.id.checkBox:
                if (!isChecked) {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
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

    private void showSpinner() {
        List<String> security = new ArrayList<String>();
        security.add(WifiAPUtils.SECURE_OPEN);
        security.add(WifiAPUtils.SECURE_WPA);
        security.add(WifiAPUtils.SECURE_WPA2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, security);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int index = security.indexOf(securityType);
        if (index != -1) spinner.setSelection(index);
        else spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new CustomOnItemSelected());
    }

    public class CustomOnItemSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            LinearLayout pass_layout = (LinearLayout) findViewById(R.id.password_layout);
            LinearLayout checkBox_layout = (LinearLayout) findViewById(R.id.checkBox_layout);
            securityType = parent.getItemAtPosition(position).toString();
            if (WifiAPUtils.SECURE_OPEN.equals(securityType)) {
                pass_layout.setVisibility(View.GONE);
                checkBox_layout.setVisibility(View.GONE);
            } else {
                pass_layout.setVisibility(View.VISIBLE);
                checkBox_layout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                ssid = ssidEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(ssid)) {
                    ssidEditText.setError("Network SSID is empty");
                    return;
                }
                if (TextUtils.isEmpty(password) || password.length() < WifiAPUtils.PASS_MIN_LENGHT) {
                    passwordEditText.setError("You must have 8 characters in password");
                    return;
                }

                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString(Constants.PREFS_SSID, ssid);
                editor.putString(Constants.PREFS_SECURITY, securityType);
                editor.putString(Constants.PREFS_PASSWORD, password);
                editor.commit();

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText()) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                Toast.makeText(MainActivity.this, "Network Info saved", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tethering_image:
                if (mSwitch.isChecked()) {
                    mSwitch.setChecked(false);
                } else {
                    mSwitch.setChecked(true);
                }
                break;
        }
    }
}
