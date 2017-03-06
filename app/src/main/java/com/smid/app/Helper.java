package com.smid.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.smid.app.model.businessLogic.ExceptionInfo;
import com.smid.app.modelManagers.ExceptionInfoModelManager;
import com.smid.app.modelManagers.ExternalServiceWithSameComponentAlreadyExists;
import com.smid.app.modelManagers.GestureNameAlreadyExistsException;
import com.smid.app.modelManagers.NeedsPaymentException;
import com.smid.app.modelManagers.NullGestureNameException;
import com.smid.app.payments.PaymentHelper;
import com.smid.app.smid.R;
import com.smid.app.helper.BackgroundSound;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.model.businessLogic.TypeOfAction;

import java.io.Closeable;
import java.util.Calendar;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by marek on 08.06.16.
 */
public class Helper {
    public static final int PICK_CONTACT = 1234;

    private static final String ActionType_PHONE = "PHONE";
    private static final String ActionType_SMS = "SMS";
    private static final String ActionType_CALL_APPLICATION = "CALL_APPLICATION";
    private static final String ActionType_External = "EXTERNAL";

    public static Intent GetContactFetcherIntent() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    public static String getActionType(TypeOfAction actionType) {
        if (actionType == TypeOfAction.Phone) {
            return ActionType_PHONE;
        } else if (actionType == TypeOfAction.SMS) {
            return ActionType_SMS;
        } else if (actionType == TypeOfAction.CallApplication) {
            return ActionType_CALL_APPLICATION;
        } else if(actionType == TypeOfAction.External) {
            return ActionType_External;
        }

        return null;
    }

    public static TypeOfAction getActionType(String value) {
        if (value.equals(ActionType_PHONE)) {
            return TypeOfAction.Phone;
        } else if (value.equals(ActionType_SMS)) {
            return TypeOfAction.SMS;
        } else if (value.equals(ActionType_CALL_APPLICATION)) {
            return TypeOfAction.CallApplication;
        }

        return null;
    }

    /**
     * Returns current time in millis.
     */
    public static long getTime() {
        Calendar rightNow = Calendar.getInstance();

        return rightNow.getTimeInMillis();
    }

    public static void logException(Context ctx, Exception ex) {
        try {
            String content = "Stack trace: " + ex.getStackTrace().toString() + "\nMessage + " + ex.getMessage() + "\n" + "Ex.toString: " + ex.toString();

            logException(ctx, content);
        }catch (Exception exc){

        }
    }

    public static void logException(Context ctx, String where, Exception ex) {
//
//        String content = "Where: " + where + "\nMessage + " + ex.getMessage() + "\nEx.toString: " + ex.toString();
//
//        content += "Stack trace detailed: ";
//
//        for (int i = 0; i < ex.getStackTrace().length; i++) {
//            content += ex.getStackTrace()[i].toString() + "\n";
//        }
//
//        logException(ctx, content);
    }

    public static void setTopText(Activity activity) {
//
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
//        Boolean showHelpText = sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_SHOW_HELP_AT_TOP, true);
//
//        if(!showHelpText) {
//            myToolbar.setVisibility(View.GONE);
//            return;
//        }
//
//        Intent intent = activity.getIntent();
//
//        Bundle b = intent.getExtras();
//
//        if (b != null) {
//            Object gesture_id_obj = b.get(GD_CONSTS.WhereUserIsText);
//
//            if (gesture_id_obj != null) {
//                String text = (String) gesture_id_obj;
//
//                if(GD_CONSTS.ADD_INFO_HOW_TO_DISABLE_TOP_TEXT) {
//                    String toAdd = " " + activity.getString(R.string.TopTextInfoHowToDisable);
//                    if(!text.endsWith(".")) {
//                        toAdd = "." + toAdd;
//                    }
//
//                    text += toAdd;
//                }
//
//                myToolbar.setText(text);
//            }
//        }

//        if (myToolbar.getText() == null || myToolbar.getText().equals("")) {
//            myToolbar.setText("Demo tekst");
//        }
    }

    public static Intent setTopTextToBeDisplayed(Intent intent, String text) {
        intent.putExtra(GD_CONSTS.WhereUserIsText, text);

        return intent;
    }

    public static void closeQuietly(Context context, Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            Helper.logException(context, ex);
        }
    }

    public static void openGestureDetails(Context context, Gesture gesture) {
        openGestureDetails(context, gesture, null);
    }

    public static void openGestureDetails(Context context, Gesture gesture, String helperText) {
        Intent intent = new Intent(context, GestureDetails.class);

        intent.putExtra(GD_CONSTS.GestureID, gesture.getId());

        if(helperText != null) {
            intent.putExtra(GD_CONSTS.WhereUserIsText, helperText);
        }

        context.startActivity(intent);
    }

    public static String getStringForAlreadyExistingGestureName(Context context, String gestureName) {
        StringBuilder sb =new StringBuilder();

        sb.append(context.getString(R.string.already_existing_gesture_part_1));
        sb.append(" ");
        sb.append(gestureName);
        sb.append(" ");
        sb.append(context.getString(R.string.already_existing_gesture_part_2));

        return sb.toString();
    }

    public static String getNewGestureCreatedTooltipText(Context context, Gesture g) {
        StringBuilder sb = new StringBuilder();

        sb.append(context.getString(R.string.FlowGestureRegistering_NewGesture__GestureDetails_part_1));

        sb.append(" ");
        sb.append(g.getName());
        sb.append(" ");

        sb.append(context.getString(R.string.FlowGestureRegistering_NewGesture__GestureDetails_part_2));

        return sb.toString();
    }

    public static void goToGestureList (Context context, String text) {
        Intent intent = new Intent(context, GestureList.class);

        setTopTextToBeDisplayed(intent, text);

        try{
            context.startActivity(intent);
        } catch (Exception e) {
            Helper.logException(context, e);
        }

    }

    public static void safelyCloseCursor(Cursor cursorPhone) {
        try{
            if(cursorPhone != null) {
                if(!cursorPhone.isClosed())
                {
                    cursorPhone.close();
                }
            }
        } catch(Exception ex) {
        }
    }

    public static void Log(String topis, String text ){
        if(GD_CONSTS.DEBUG) {
            Log.d(topis, text);
        }
    }

    public static void logException(Context ctx, String content) {
        try {
            ExceptionInfoModelManager exceptionInfoModelManager = new ExceptionInfoModelManager(ctx);

            ExceptionInfo ei1 = new ExceptionInfo();
            ei1.content = Calendar.getInstance().getTime().toLocaleString() + " " + content;

            try {
                exceptionInfoModelManager.Persist(ei1);
            } catch (GestureNameAlreadyExistsException e) {

            } catch (NullGestureNameException e) {

            } catch (ExternalServiceWithSameComponentAlreadyExists externalServiceWithSameComponentAlreadyExists) {

            } catch (NeedsPaymentException e) {
                PaymentHelper.showAlertNeedsBuying(ctx);
            }
        } catch (Exception ex) {

        }
    }

    public static void RunGDService (Context ctx) {
        return;
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
//
//        Boolean runAsForeground = false;//sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_RUN_FOREGROUND, false);
//
//
//        Intent intent = new Intent(ctx, GDService.class);
//        ctx.startService(intent);

    }

    public static void preventServiceFromProcessingActions (Context ctx) {
        setALLOW_REGISTERING_GESTURE_OPTION(ctx, false);
    }

    public static void allowServiceProcessingActions (Context ctx) {
        setALLOW_REGISTERING_GESTURE_OPTION(ctx, true);
    }

    public static void restartService(final Context ctx, final Intent i) {
//        Intent serviceIntent = new Intent(ctx, GDService.class);
//
//        ctx.bindService(serviceIntent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                Helper.Log("gestureDetected", "service connected, stopping and starting service.");
//
//                GDService.LocalBinder binder = (GDService.LocalBinder) service;
//
//                GDService instance = binder.getServiceInstance();
//
//                instance.stopService(i);
//
//                Helper.RunGDService(ctx);
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//            }
//        }, ctx.BIND_AUTO_CREATE);
    }

    protected static void setALLOW_REGISTERING_GESTURE_OPTION (Context ctx, final Boolean value ){
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
//
//        SharedPreferences.Editor editor = sharedPref.edit();
//
//        editor.putBoolean(GestureDetectorSettings.KEY_PREF_ALLOW_REGISTERING_GESTURE, value);
//        editor.commit();
//
//        Intent serviceIntent = new Intent(ctx, GDService.class);
//
//        ctx.bindService(serviceIntent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                Helper.Log("gestureDetected", "service connected set allow registering options");
//
//                GDService.LocalBinder binder = (GDService.LocalBinder) service;
//
//                GDService instance = binder.getServiceInstance();
//
//                if(value) {
//                    instance.restart();
//                } else {
//                    instance.stopAllExistingMoves();
//                }
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//            }
//        }, ctx.BIND_AUTO_CREATE);

    }

    public static void showErrorMessage(Context ctx, String text) {
        Intent i = new Intent(ctx, ActionPopup.class);

        i.putExtra(GD_CONSTS.ErrorMessage, text);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);

        ctx.startActivity(i);
    }

    public static void startGestureRegisteringActivity(Context ctx) {
        Intent intent = new Intent(ctx, GestureDetectionPage.class);

        setTopTextToBeDisplayed(intent, ctx.getString(R.string.GestureRegistering_Description));

        ctx.startActivity(intent);
    }

    public static void startMainMenu(Context ctx) {
        Intent intent = new Intent(ctx, GestureDetectionPage.class);

        ctx.startActivity(intent);
    }

    public static void showOkMessage(Context ctx, String text, DialogInterface.OnClickListener listener) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

            builder
                    .setPositiveButton(ctx.getString(R.string.ok), listener)
                    .setMessage(text)
                    .create()
                    .show();

        } catch (Exception e) {
            Helper.logException(ctx, e);
        }

    }

    public static int getNotificationSmallIcon() {
        return R.drawable.arrowlu;
    }

    public static void startSelectActionIntent(Context ctx) {
        Intent i = new Intent(ctx, SelectActionFromExternalService.class);

        Helper.setTopTextToBeDisplayed(i, ctx.getString(R.string.OPENING_SELECT_ACTION_TYPE));

        ctx.startActivity(i);
    }

    public static void startActionListToSelect(Context ctx) {
        Intent i = new Intent(ctx, ActionListToSelect.class);

        setTopTextToBeDisplayed(i, ctx.getString(R.string.ActionListToSelect_TooltipText));

        ctx.startActivity(i);
    }

    public static void startActionListToEdit(Context ctx) {
        Intent i = new Intent(ctx, ActionListToEdit.class);

        setTopTextToBeDisplayed(i, ctx.getString(R.string.ActionListToEdit_TooltipText));

        ctx.startActivity(i);
    }

    public static void startGestureRegisterDetect(Context ctx) {
        Helper.startGestureRegisterDetect(ctx,  ctx.getString(R.string.GestureDetection_TooltipText));
    }

    public static void startGestureRegisterDetect(Context ctx, String text) {
        Intent i = new Intent(ctx, GestureDetectionPage.class);

        setTopTextToBeDisplayed(i,text);

        ctx.startActivity(i);
    }

    public static void startSettings(Context  ctx) {
        Intent intent = new Intent(ctx, GestureDetectorSettings.class);
        ctx.startActivity(intent);
    }

    public static void playSoundSameThread(Context ctx) {
        MediaPlayer mp = MediaPlayer.create(ctx, R.raw.e);
        mp.start();
    }

    public static void playSound(Context ctx) {
        new BackgroundSound(ctx).execute();
    }

}
