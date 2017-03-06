//package com.gdapplication.app.gesturedetector;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.gdapplication.app.gesturedetector.model.businessLogic.Gesture;
//import com.gdapplication.app.gesturedetector.modelManagers.GestureNameAlreadyExistsException;
//import com.gdapplication.app.gesturedetector.modelManagers.NullGestureNameException;
//
//public class ExternalActionSelected extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_external_action_selected);
//
//        Intent i = getIntent();
//
//        Bundle b = i.getExtras();
//
//
//
//        if(gesture == null) { // only for testing!
//            gesture = new Gesture();
//
//            try {
//                gestureModelManager.Persist(gesture);
//            } catch (GestureNameAlreadyExistsException e) {
//                Helper.logException(this, e);
//            } catch (NullGestureNameException e) {
//                Helper.logException(this, e);
//            }
//        }
//    }
//}
