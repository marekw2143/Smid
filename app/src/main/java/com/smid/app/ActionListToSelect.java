package com.smid.app;

import android.content.Intent;

import com.smid.app.model.businessLogic.Action;
public class ActionListToSelect extends BaseActionList{
    @Override
    public void SelectionChanged(Action model) {
        Intent resultIntent = new Intent();

        resultIntent.putExtra(GD_CONSTS.SelectedActionID, model.getId());

        setResult(RESULT_OK, resultIntent);

        finish();
    }
}

