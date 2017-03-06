package com.smid.app;

import android.content.Intent;

import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;

//import com.example.marek.testapp1.model.ActionAssociation;

public class ActionListToEdit extends BaseActionList {
    @Override
    public void SelectionChanged(Action model) {
        if(model!= null) {
            Intent intent = new Intent(this, ActionDetails.class);

            intent.putExtra(GD_CONSTS.Action_ID, model.getId());

            Helper.setTopTextToBeDisplayed(intent, getString(R.string.FlowActionList_ActionDetails) + " " + model.getName() + " ." +
            getString(R.string.FlowActionList_ActionDetails_2));

            startActivity(intent);
        }
    }
}
