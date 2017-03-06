package com.smid.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import java.nio.charset.Charset;

/**
 * Created by marek on 08.06.16.
 */
public class GD_CONSTS {
    public static final int MAX_FREE_GESTURES_AMOUNT = 2;



    public static final long MAX_NOISE_SIMPLE = 200;
    public static final long MAX_LENGTH_SIMPLE = Long.MAX_VALUE;

    public static final long MAX_NOISE_SIMPLE_X = 50;
    public static final long MAX_LENGTH_SIMPLE_X = 2000;

    public static final long MAX_NOISE_PEAK = 30;
    public static final long MAX_LENGTH_PEAK = 4000;

    public static final Double THRESHOLD = 0.8; // TODO - zastanows sie czy to aby dobra wartosc

    public static final long ALGORITHM_CHUNK_WINDOW_HALF_SIZE = 50;

    /* minimal duration of "non simple fragments" to be considered valid */
    public static final long MIN_NON_SIMPLE_DURATION = 100;
    /* minimal duration of "simple fragments" to be considered valid */
    public static final long MIN_SIMPLE_DURATION = 100;


    // Storage some value
    public static final String ActionFilenamePrefix = "action";
    public static final String ActionDirectory = "action";

    public static final String SelectedActionID = "selected_action_id";
    public static final int SelectActivityRequestCode = 1;
    public static final String GestureID = "gesture_id";
    public static final String DIRECTORY_EXTERNAL_SERVICES = "ExternalServices";
    public static final String FILENAME_PATTERN_EXTERNAL_SERVICES = "ExternalService_";
    public static final String EXTERNAL_SERVICE_FRIENDLY_NAME = "FriendlyName";
    public static final String EXTERNAL_SERVICE_OPTIONS_FETCHER_INTENT = "OptionsFetcher";
    public static final String EXTERNAL_SERVICE_OPTIONS_EXECUTOR_INTENT = "OptionsExecutor";

    public static final String EXTERNAL_SERVICE_EXTERNAL_OPTION_ID = "ExternalOptionId";
    public static final String EXTERNAL_SERVICE_EXTERNAL_OPTION_FRIENDLY_NAME = "ExternalOptionFriendlyName";
    public static final String EXTERNAL_SERVICE_EXTERNAL_SERVICE_ID = "ExternalServiceId";
    public static final long TEST_ACCELEROMETER_TIME_MS = 10000;
    public static final int MIN_MEASUREMENTS_PER_SECOND = 70;
    public static final int ACC_TEST_NOT_PERFORMED = 0;
    public static final int ACC_TEST_PASSED = 2;
    public static final int ACC_TEST_NOT_PASSED = 1;
    public static final boolean ADD_INFO_HOW_TO_DISABLE_TOP_TEXT = true;
    public static final boolean INFO_YOU_CAN_MAKE_GESTURE = false;
    public static final long GESTURE_DETECTION_MIN_INTERVAL_TICK_SOUND_MS = 1500;
    public static final Double GESTURE_DETECTION_NOMOTION_THRESHOLD_TICK_SOUND = 0.7;
    public static final long GESTURE_DETECTION_NOMOTION_TIME_WINDOW = 31;
    public static final long GESTURE_DETECTION_NOMOTION_MIN_TIME = 100;
    public static final boolean DO_ENCRYPT_FILES = true;
    public static final long OVAL_VISIBILITY_MS = 150;

    public static String Action_ID = "action_id";
    public static String ErrorMessage = "ErrorMessage";
    public static final String WhereUserIsText = "where_is_user";
//    public static final String ActionID = "action_id";


    /** time window both for x and y **/

//    public static final long ALGORITHM_X_TREATED_AS_Z_IN_MS = 2000;

    public static final boolean DEBUG = false;
    /**
     * Value for acceleration in either axis which indicate movement.
     */


    /**
     * Description of no-movement in X axis.
     */
    public static final String HORIZONTAL_SIMPLE = "_";

    /**
     * Description of movement in A direction in X axis.
     */
    public static final String HORIZONTAL_LEFT= "L";

    /**
     * Description of movement in B direction in X axis.
     */
    public static final String HORIZONTAL_RIGHT = "R";

    /**
     * Description of no-movement in Y axis.
     */
    public static final String VERTICAL_SIMPLE = "|";

    /**
     * Description of movement in A direction in Y axis.
     */
    public static final String VERTICAL_UP = "U";

    /**
     * Description of movement in B direction in Y axis.
     */
    public static final String VERTICAL_DOWN= "D";





    /**
     * Time difference of start time of semantic fragment on two axis to treat them as happenning in the almost same time.
     */
    public static final long TIME_CORELATED_MAX_DIFF = 200;

    /**
     * Time window for which average on X axis is calculated before detecting move.
     */
    public static final long TIME_WINDOW_AVERAGING_XS = ALGORITHM_CHUNK_WINDOW_HALF_SIZE;

    /**
     * Time window for which average on Y axis is calculated before detecting move.
     */
    public static final long TIME_WINDOW_AVERAGING_YS = ALGORITHM_CHUNK_WINDOW_HALF_SIZE;
    public static final int INITIAL_ACCELEROMETER_DATA_BUFFER = 20000;


    /**
     *
     *
     * Nomotion detection
     *
     *9
     */

    /**
     * Amount of milliseconds phone must be in nomotion to start fetching data.
     */
    public static final int NOMOTION_TIME = 3000;

    /**
     * time window size for wchich average will be calculated to detect nomotion.
     */
    public static final long NOMOTION_TIME_WINDOW_SIZE = 500;

    /**
     * Maximal value of absolute value of average motion in X axis during MOTION_TIME period to
     * allow describe phone as in nomotion.
     */
    public static final Double NOMOTION_X_THRESHOLD = 0.4;

    /**
     * Maximal value of absolute value of average motion in Y axis during MOTION_TIME period to
     * allow describe phone as in nomotion.
     */
    public static final Double NOMOTION_Y_THRESHOLD = 0.4;

    /**
     * Currently need to be false, it doesn't work.
     */
    public static final boolean PLAY_SOUND_ON_STOP_REGISTERING_GESTURE = false;

    public static final String ENCRYPTION_ALGORITHM_SHORT_NAME = "AES";
    public static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final byte [] ENCRYPTION_KEY = "SecureKeyForSmid".getBytes(Charset.forName("UTF-8"));
    public static final byte [] ENCRYPTION_IV = "SecureKeyForSmid".getBytes(Charset.forName("UTF-8"));
    public static final boolean ENCRYPT_FILES = false;
    public static final String EMPTY_MOVE = GD_CONSTS.HORIZONTAL_SIMPLE + GD_CONSTS.VERTICAL_SIMPLE;

    public static int GestureRegistering_StatusWaiting_Color = Color.RED;
    public static int GestureRegistering_StatusRegistering_Color = Color.GREEN;
    public static int GEstureRegistering_status_AfterRegistering_Color = Color.RED;


    public static int EXTERNAL_SERVICE_RequestCode = 101;
    public static int EXTERNAL_SERVICE_RequestCode_Select_Option = 102;
    public static long GestureDetection_DelayAfterDetected = 1500;
    public static long GestureDetection_DelayAfterGestureStarted = 500;

    public static boolean IsTaskerPluginOnly(Context ctx ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);

        return sharedPref.getBoolean(GestureDetectorSettings.KEY_PREF_WORK_AS_TASKER_PLUGIN, false);
    }
}
