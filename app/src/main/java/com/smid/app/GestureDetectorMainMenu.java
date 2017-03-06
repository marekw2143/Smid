package com.smid.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smid.app.smid.R;
import com.smid.app.externalServices.phoneCall.PhoneCallRegistrator;
import com.smid.app.gestureDetection.AccelerometerCapabilitiesDetector;
import com.smid.app.gestureDetection.IAccelerometerCapabilitiesListener;
import com.smid.app.gestureDetection.MeasurementStatus;

public class GestureDetectorMainMenu extends BaseActivity implements IAccelerometerCapabilitiesListener {
    int mAccelerometerTestPassed = GD_CONSTS.ACC_TEST_NOT_PERFORMED;
    TextView getInfoTestingPhone (){
        return (TextView) getMainLayout().findViewById(R.id.MainActivity_InfoTesting);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mAccelerometerTestPassed = sharedPref.getInt(GestureDetectorSettings.ACC_TEST_PERFORMED, GD_CONSTS.ACC_TEST_NOT_PERFORMED);

        if(mAccelerometerTestPassed == GD_CONSTS.ACC_TEST_NOT_PERFORMED) {
            getInfoTestingPhone().setVisibility(View.VISIBLE);

            AccelerometerCapabilitiesDetector detector = new AccelerometerCapabilitiesDetector(this, this);
            detector.startCollectingData();
        } else if (mAccelerometerTestPassed == GD_CONSTS.ACC_TEST_NOT_PASSED) {
            informAcceleremotereTestNotPassed();
        } else  if (mAccelerometerTestPassed == GD_CONSTS.ACC_TEST_PASSED) {
            infromAccelerometerTestOk();
        }

        if(mAccelerometerTestPassed == GD_CONSTS.ACC_TEST_PASSED) {
            Boolean defaultServicesLoaded = sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_DEFAULT_SERVICES_LOADED, false);

            if(!defaultServicesLoaded) {
                try {
                    PhoneCallRegistrator.register(this);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(GestureDetectorSettings.KEY_PREF_DEFAULT_SERVICES_LOADED, true);
                    editor.commit();
                } catch(Exception ex) {
                    Helper.logException(this, ex);
                }
            }
        }
    }


    protected void informAcceleremotereTestNotPassed() {
        getInfoTestingPhone().setText(getString(R.string.MainActivity_TxtInfo_PhoneDoesntMeetCriteria));

        Helper.showOkMessage(this, getString(R.string.MainActivity_NOT_SUFFICIENT_ACCELEROMETER), null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Helper.setTopText(this);
    }

    public void menuClick(View v) {
        switch(v.getId()) {
            case R.id.Menu_GestureList:
                Helper.goToGestureList(this, getString(R.string.TooltipText_Flow_MainMenu_GestureList));
                break;
            case R.id.Menu_ActionList:
                Helper.startActionListToEdit(this);
                break;
            case R.id.Menu_NewAction:
                Helper.startSelectActionIntent(this);
                break;
            case R.id.Menu_RegisterGesture:
                Helper.startGestureRegisterDetect(this);
                break;
            case R.id.Menu_Settings:
                Helper.startSettings(this);
                break;
            case R.id.Menu_About:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                break;
//            case R.id.Menu_Payment:
//                Intent paymentIntent = new Intent(this, PaymentActivityDebug.class);
//                startActivity(paymentIntent);
//                break;
//            case R.id.PaymentTest:
//                Intent i = new Intent(this, PaymentActivity.class);
//                startActivity(i);
//                break;
//            case R.id.PaymentTestDebug:
//                Intent ai = new Intent(this, PaymentActivityDebug.class);
//                startActivity(ai);
//                break;
            default:
                break;
        }
    }
    @Override
    public void capabilitiesMeasured(MeasurementStatus status) {
        if(status == MeasurementStatus.NOT_SUFFICIENT) {
            informAcceleremotereTestNotPassed();
        } else if(status == MeasurementStatus.SUFFICIENT){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(GestureDetectorSettings.ACC_TEST_PERFORMED, GD_CONSTS.ACC_TEST_PASSED);
            editor.commit();

            Helper.showOkMessage(this, getString(R.string.MainMenu_TestPassed), null);

            infromAccelerometerTestOk();
        }
    }

    private void infromAccelerometerTestOk() {
        getInfoTestingPhone().setVisibility(View.GONE);

        Intent i = getIntent();
        String str = getString(R.string.MainMenu_Info) + " " + getString(R.string.Menu_RegisterGesture) + " " + getString(R.string.MainMenu_Info_2);
        Helper.setTopTextToBeDisplayed(i, str);
        Helper.setTopText(this);

        getNormalLayout().setVisibility(View.VISIBLE);
    }

    private ViewGroup getNormalLayout() {
        return (ViewGroup) getMainLayout().findViewById(R.id.MainActivity_ContentLayout);
    }

    private ViewGroup getMainLayout() {
        return (ViewGroup) findViewById(R.id.MainActivity_MainLayout);
    }

}
