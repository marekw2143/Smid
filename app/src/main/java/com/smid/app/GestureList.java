package com.smid.app;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.smid.app.Adapters.GenericArrayAdapter;
import com.smid.app.Adapters.GestureArrayAdapter;
import com.smid.app.Adapters.IModelSelectionChanged;
import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Gesture;


public class GestureList extends BaseActivity implements IModelSelectionChanged<Gesture> {
    GenericArrayAdapter<Gesture> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.gesture_list);

            getModelAndPresentIt();
        } catch (Exception e) {
            Helper.logException(this, e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        getModelAndPresentIt();
    }

    void getModelAndPresentIt() {
        arrayAdapter = new GestureArrayAdapter(this, android.R.layout.simple_list_item_1, this);

        ListView lv = (ListView) findViewById(R.id.GestureList_MainLayout).findViewById(R.id.GestureList_ContentLayout).findViewById(android.R.id.list);

        lv.setAdapter(arrayAdapter);

        Helper.setTopText(this);
    }

    @Override
    public void SelectionChanged(Gesture model) {
        if(model!= null) {
            Helper.openGestureDetails(this, model, getGestureDetailsTopText(model));
        }
    }

    public void onClick(View v)
    {
        if(v.getId() == R.id.GestureList_NewGesture) {
            Helper.startGestureRegisterDetect(this, getString(R.string.Flow_GestureList_GestureRegister));
        }
    }

    protected String getGestureDetailsTopText(Gesture gesture) {
        return getString(R.string.Flow_GestureList_GestureDetails) + " " + gesture.GetTextRepresentation(this) + " " + getString(R.string.Flow_GestureList_GestureDetails_2);
    }
}
