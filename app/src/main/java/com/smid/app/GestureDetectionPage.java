package com.smid.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smid.app.changesDetector.MoveDescription;
import com.smid.app.externalServices.phoneCall.PhoneCallRegistrator;
import com.smid.app.gestureDetection.AccelerometerCapabilitiesDetector;
import com.smid.app.gestureDetection.Counter;
import com.smid.app.gestureDetection.DetectionManager;
import com.smid.app.gestureDetection.IAccelerometerCapabilitiesListener;
import com.smid.app.gestureDetection.IGestureDetectorReceiver;
import com.smid.app.gestureDetection.INomotionManager;
import com.smid.app.gestureDetection.MeasurementStatus;
import com.smid.app.gestureDetection.NomotionDetector;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.modelManagers.ExternalServiceWithSameComponentAlreadyExists;
import com.smid.app.modelManagers.GestureModelManager;
import com.smid.app.modelManagers.GestureNameAlreadyExistsException;
import com.smid.app.modelManagers.NeedsPaymentException;
import com.smid.app.modelManagers.NullGestureNameException;
import com.smid.app.payments.PaymentHelper;
import com.smid.app.service.SimpleGestureReceiver;
import com.smid.app.smid.R;
import com.smid.app.taskerIntegration.TaskerPlugin;
import com.smid.app.taskerIntegration.TaskerQueryReceiver;

import java.util.List;
import java.util.TimerTask;

public class GestureDetectionPage extends BaseActivity implements IGestureDetectorReceiver, INomotionManager, IAccelerometerCapabilitiesListener {
    int mAccelerometerTestPassed = GD_CONSTS.ACC_TEST_NOT_PERFORMED;

    DetectionManager detectionManager;
    private GestureModelManager gestureModelManager;
    Counter counter = new Counter();
    private long lastTime = -1;
    private MoveDescription mMoveDescription;

    long lastNomotionCreationTime = -1;
    String state = "waiting";

    NomotionDetector nomotionDetector;
    private boolean playSound;

    boolean isVisible = false;
    private boolean showImage = false;


    protected Button getMenuButton () {
        return (Button) findViewById(R.id.GestureDetection_MainLayout).findViewById(R.id.toolbar).findViewById(R.id.toolbar_menu_button_container)
                .findViewById(R.id.Menu_GoToMainMenu2);
    }


    protected void makeMenuActive() {
        menuActive = true;
        getMenuButton().setVisibility(View.VISIBLE );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isVisible = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mAccelerometerTestPassed = sharedPref.getInt(GestureDetectorSettings.ACC_TEST_PERFORMED, GD_CONSTS.ACC_TEST_NOT_PERFORMED);

        menuActive = false;

        setContentView(R.layout.activity_gesture_detection_page);

        if(mAccelerometerTestPassed == GD_CONSTS.ACC_TEST_NOT_PERFORMED) {
            try {
                getMenuButton().setVisibility(View.INVISIBLE);

                getInfoTestingPhone().setVisibility(View.VISIBLE);

                AccelerometerCapabilitiesDetector detector = new AccelerometerCapabilitiesDetector(this, this);
                detector.startCollectingData();
            } catch(Exception ex) {
                Helper.logException(this, ex);
            }
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

        playSound = sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_PLAY_SOUND, true);

        showImage = sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_SHOW_IMAGE, true);

        isVisible = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Helper.Log("debugOnPR", "onResume");

        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Helper.Log("debugOnPR", "onPause");
        isVisible = false;
    }

    protected void doPlaySound() {
        if(!isVisible) {
            return;
        }

        if(playSound) {
            Helper.playSound(this);
        }
    }

    protected void doShowImage() {
        if(showImage) {
            getKolko().setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getKolko().setVisibility(View.INVISIBLE);
                }
            }, GD_CONSTS.OVAL_VISIBILITY_MS);
        }
    }

    @Override
    public void onNomotionStarted(DetectionManager detectionManager) {
        state = "started";

        if(playSound) {
            doPlaySound();
            lastNomotionCreationTime = Helper.getTime();
        }

        if(showImage) {
            doShowImage();
        }

        getTVStatus().setText(getString(R.string.GestureDetection_Status_RecordingMove));
        getTVStatus().setTextColor(GD_CONSTS.GestureRegistering_StatusRegistering_Color);

        initNomotionDetector();
        if(GD_CONSTS.INFO_YOU_CAN_MAKE_GESTURE) {
            new Handler().postDelayed(new TimerTask() {
                @Override
                public void run() {
                    getNowYouCanMakeAGestureTV().setVisibility(View.VISIBLE);
                }
            }, GD_CONSTS.GestureDetection_DelayAfterGestureStarted);
        }

//        getTVDescription().setText(getString(R.string.gestureRegistering_Information));
    }

    @Override
    public void onNomotionFinished(DetectionManager detectionManager) {

    }

    @Override
    public void onGestureDetected(MoveDescription moveDescription, DetectionManager detectionManager) {
        if(!isVisible) {
            return;
        }
        state = "finished";
//        if(!isVisible) {
//            return;
//        }

        if(detectionManager.getTimeCreated() < lastTime) {
            return;
        }
        mMoveDescription = moveDescription;

        getTVStatus().setText(getString(R.string.GestureRegistering_Status_AfterRegistering));
        getTVStatus().setTextColor(GD_CONSTS.GEstureRegistering_status_AfterRegistering_Color);

        final Gesture detectedGesture = gestureModelManager.getValidMoveForDescription(moveDescription);

        if(detectedGesture == null) {
            detectionManager.stop();

            getGestureDetectionComplete().setVisibility(View.VISIBLE);


            boolean blnDetectedGesture = false;
            if(moveDescription!= null) {
                String move = moveDescription.getMove();
                if(move!= null) {
                    if(!move.equals(GD_CONSTS.EMPTY_MOVE)) {
                        blnDetectedGesture = true;
                    }
                }
            }

            if(blnDetectedGesture) {
                getTVDetectedGesture().setVisibility(View.VISIBLE);
                getImageViewLayout().setVisibility(View.VISIBLE);
                printMoveDescription(moveDescription);
                getNoSuchGestureSaved().setVisibility(View.VISIBLE);
                getBtnCreateNewGesture().setVisibility(View.VISIBLE);
            } else {
                getNoGestureWasDetected().setVisibility(View.VISIBLE);
            }


            getBtnReplayDetection().setVisibility(View.VISIBLE);
        } else {
            getTVStatus().setText(getString(R.string.GestureDetection_Status_GestureReceived));
            getTVStatus().setTextColor(GD_CONSTS.GEstureRegistering_status_AfterRegistering_Color);
            getImageViewLayout().setVisibility(View.VISIBLE);

            printMoveDescription(moveDescription);


            if(GD_CONSTS.IsTaskerPluginOnly (this)) {
                Intent i = GestureListToSelect.createRequestIntent();

                TaskerQueryReceiver.setDetectedGestureId(detectedGesture.getId());

                i.putExtra(GestureListToSelect.KEY_VALUE, Long.toString(detectedGesture.getId()));

                TaskerPlugin.Event.addPassThroughMessageID(i);

                sendBroadcast(i);

                return;
            } else {
                new Handler().postDelayed(new TimerTask() {
                    @Override
                    public void run() {
                        SimpleGestureReceiver simpleGestureReceiver = new SimpleGestureReceiver();

                        simpleGestureReceiver.gestureReceived(detectedGesture, GestureDetectionPage.this);
                    }
                }, GD_CONSTS.GestureDetection_DelayAfterDetected);
            }
        }
    }

    private View getNoGestureWasDetected() {
        return getContentLayout().findViewById(R.id.GestureDetection_NoGestureDetected);
    }

    private LinearLayout getImageViewLayout() {
        return (LinearLayout) getContentLayout().findViewById(R.id.GestureDetection_GestureRepresentationLayout);
    }

    protected void initNomotionDetector () {
//        if(!playSound) {
//            return;
//        }

        try {
            Helper.Log("nomotion", "INIT started");

            if (state.equals("started")) {
                Helper.Log("nomotion", "started");
                nomotionDetector = new NomotionDetector(this, this, GD_CONSTS.GESTURE_DETECTION_NOMOTION_MIN_TIME,
                        GD_CONSTS.GESTURE_DETECTION_NOMOTION_TIME_WINDOW,
                        GD_CONSTS.GESTURE_DETECTION_NOMOTION_THRESHOLD_TICK_SOUND);

                nomotionDetector.startCollectingData();
            }
        } catch (Exception e) {
            Helper.logException(this, e);
        }
    }

    @Override
    public synchronized  void nomotionStarted() {
//        if(!playSound){
//            return;
//        }

        Helper.Log("nomotion", "NOMOTION started");

        try {
            nomotionDetector.stopCollectingData();
            nomotionDetector.unregisterListener();
            nomotionDetector.clearNomotionDetectedListener();
        }catch (Exception e) {
            Helper.logException(this, e);
        }

        if(state.equals("started")) {
            if(Helper.getTime() - lastNomotionCreationTime > GD_CONSTS.GESTURE_DETECTION_MIN_INTERVAL_TICK_SOUND_MS) {
                lastNomotionCreationTime = Helper.getTime();

                Helper.Log("nomotion", "playing");
                doPlaySound();
                doShowImage();
            }
        }

        initNomotionDetector();
    }

    protected void setNewDetectionManager() {
        lastTime = counter.getNextCounter();

        detectionManager = new DetectionManager(this, this, counter);


        getNoSuchGestureSaved().setVisibility(View.GONE);
        getTVDetectedGesture().setVisibility(View.GONE);
        getImageViewLayout().setVisibility(View.GONE);
        getBtnCreateNewGesture().setVisibility(View.GONE);
        getBtnReplayDetection().setVisibility(View.GONE);
        getGestureDetectionComplete().setVisibility(View.GONE);
        getNoGestureWasDetected().setVisibility(View.GONE);
        getNowYouCanMakeAGestureTV().setVisibility(View.GONE);

        getTVStatus().setText(getString(R.string.GestureRegistering_WaitintNomove));
        getTVStatus().setTextColor(GD_CONSTS.GestureRegistering_StatusWaiting_Color);

        detectionManager.startWaitingForChanges();
        initNomotionDetector();
    }

    private View getBtnReplayDetection() {
        return getContentLayout().findViewById(R.id.GestureDetection_ReplayDetection);
    }

    protected  ImageView getKolko() {
        return (ImageView) getStatusLayout().findViewById(R.id.GestureDetection_Kolko);
    }
    protected void statusWaiting() {
        getTVStatus().setText(getString(R.string.GestureRegistering_WaitintNomove));
        getTVStatus().setTextColor(GD_CONSTS.GEstureRegistering_status_AfterRegistering_Color);
    }

    @Override
    public void detectoTimeoutReached(DetectionManager detectionManager) {

    }

    public void menuClick(View v) {
        switch(v.getId()) {
            case R.id.Menu_GestureList:
                Helper.goToGestureList(this, getString(R.string.TooltipText_Flow_MainMenu_GestureList));
                break;
            case R.id.Menu_ActionList:
                Intent intent = new Intent(this, ActionListToEdit.class);
                startActivity(intent);
                break;
            case R.id.Menu_NewAction:
                Intent intentNewAction = new Intent(this, SelectActionFromExternalService.class);
                startActivity(intentNewAction);
                break;
            default:
                break;
        }
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btnCreateNewGesture) {
            GestureModelManager gestureModelManager = new GestureModelManager(this);
            Gesture g = gestureModelManager.getGestureForMove(mMoveDescription.getMove());

            if(g!= null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(getString(R.string.GestureRegistering_SuchGestureAlreadyRegisterd)
                        + " "
                        + g.getName()
                        + getString(R.string.GestureRegistering_SuchGestureAlreadyRegisterd_1)
                )
                        .setPositiveButton(getString(R.string.GestureRegistering_OK_GestureWithMoveExists_GotoMainMenu), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper.startMainMenu(GestureDetectionPage.this);

                            }
                        })
                        .setNegativeButton(getString(R.string.GestureREgistering_GestureWithMoveExists_TryRegisterAnotherMove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper.startGestureRegisteringActivity(GestureDetectionPage.this);
                            }
                        })
                        .create().
                        show();

                return;
            }

            List<Gesture> gestureList = gestureModelManager.GetItems();

            if(gestureList.size() >= GD_CONSTS.MAX_FREE_GESTURES_AMOUNT && !PaymentHelper.HasAccess(this)) {
                PaymentHelper.showAlertNeedsBuying(this);
                return;
            }

            selectNewGestureName(null);
        } else if(v.getId() == R.id.GestureDetection_ReplayDetection) {
            setNewDetectionManager();
        }
    }

    protected View getGestureDetectionComplete () {
        return getContentLayout().findViewById(R.id.GestureDetection_GestureDetectionComplete);
    }
    protected Button getBtnCreateNewGesture () {
        return (Button) getContentLayout().findViewById(R.id.btnCreateNewGesture);
    }
    protected TextView getNoSuchGestureSaved() {
        return (TextView) getContentLayout().findViewById(R.id.GestureDetection_SuchGestureIsNotSaved);
    }

    protected TextView getTVDetectedGesture() {
        return (TextView) getContentLayout().findViewById(R.id.GestureDetection_DetectedGesture);
    }

    protected TextView getTVStatus() {
        return (TextView) getStatusLayout().findViewById(R.id.GestureDetection_TV_Status);
    }

    protected TextView getTVDescription() {
        return (TextView) getContentLayout().findViewById(R.id.GestureDetection_TV_Description);
    }

    protected ViewGroup getStatusLayout () {
        return (ViewGroup) getContentLayout().findViewById(R.id.GestureDetection_StatusLayout);
    }

    protected TextView getNowYouCanMakeAGestureTV() {
        return (TextView) getContentLayout().findViewById(R.id.GestureDetection_NowYouCanMakeGesture);
    }
    protected ViewGroup getContentLayout() {
        return getMainLayout();
    }

    protected ViewGroup getMainLayout() {
        return (ViewGroup) findViewById(R.id.GestureDetection_MainLayout);
    }

    protected void printMoveDescription(MoveDescription moveDescription) {
        LinearLayout moveViewLayout = getImageViewLayout();
        moveViewLayout.removeAllViews();

        List<ImageView> ivs = new MotionImagePresenter().transformMoveToImages(moveDescription.getMove(), this);

        for (int i = 0; i < ivs.size(); i++) {
            moveViewLayout.addView(ivs.get(i));
        }
    }

    protected void selectNewGestureName (String descriptionText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater =  getLayoutInflater();

        View view = (View) layoutInflater.inflate(R.layout.popup_new_gesture_name, null);

        final EditText etNewName= (EditText) view.findViewById(R.id.NewGestureNameEditText);
        final TextView tvDescriptionText = (TextView) view.findViewById(R.id.NewGestureNameDescriptionText);

        if(descriptionText == null) {
            tvDescriptionText.setText(getString(R.string.GestureRegistering_NewGestureName));
        } else {
            tvDescriptionText.setText(descriptionText);
        }

        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Gesture g = new Gesture();

                        g.setMove(GestureDetectionPage.this.mMoveDescription.getMove());

                        g.setName(etNewName.getText().toString());

                        GestureModelManager gestureModelManager = new GestureModelManager(GestureDetectionPage.this);

                        try{
                            try {
                                gestureModelManager.Persist(g);
                            } catch (NeedsPaymentException ex) {
                                PaymentHelper.showAlertNeedsBuying(GestureDetectionPage.this);

                                return;
                            }

                            String newGestureCreatedTooltipText = Helper.getNewGestureCreatedTooltipText(GestureDetectionPage.this, g);

                            Helper.openGestureDetails(GestureDetectionPage.this, g, newGestureCreatedTooltipText);
                        } catch (GestureNameAlreadyExistsException ex) {
                            selectNewGestureName(Helper.getStringForAlreadyExistingGestureName(GestureDetectionPage.this, etNewName.getText().toString()));
                        } catch (NullGestureNameException e) {
                            selectNewGestureName(getString(R.string.GestureNameMustNotBeEmpty));
                        } catch (ExternalServiceWithSameComponentAlreadyExists externalServiceWithSameComponentAlreadyExists) {
                            Helper.logException(GestureDetectionPage.this, externalServiceWithSameComponentAlreadyExists);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .create()
                .show();

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

            Helper.showOkMessage(this, getString(R.string.MainMenu_TestPassed), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Helper.startMainMenu(GestureDetectionPage.this);
                }
            });
        }
    }

    private void infromAccelerometerTestOk() {
        makeMenuActive();
        getInfoTestingPhone().setVisibility(View.GONE);

        getMainLayout().findViewById(R.id.GestureDetection_MainContentLayout).setVisibility(View.VISIBLE);

        Intent i = getIntent();
        if(i.getExtras() == null) {
            i.putExtra(GD_CONSTS.WhereUserIsText, getString(R.string.GestureDetection_TooltipText));
        } else {
            Bundle b = i.getExtras();

            if(b.get(GD_CONSTS.WhereUserIsText) == null){
                i.putExtra(GD_CONSTS.WhereUserIsText, getString(R.string.GestureDetection_TooltipText));
            }
        }

//        Helper.setTopText(this);

        gestureModelManager = new GestureModelManager(this);

        setNewDetectionManager();
    }

    protected void informAcceleremotereTestNotPassed() {
        getMenuButton().setVisibility(View.INVISIBLE);

        getInfoTestingPhone().setText(getString(R.string.MainActivity_TxtInfo_PhoneDoesntMeetCriteria));

        Helper.showOkMessage(this, getString(R.string.MainActivity_NOT_SUFFICIENT_ACCELEROMETER), null);
    }

    TextView getInfoTestingPhone (){
        return (TextView) getMainLayout().findViewById(R.id.MainActivity_InfoTesting);
    }
}
