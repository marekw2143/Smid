package com.smid.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.smid.app.Adapters.ActionArrayAdapter;
import com.smid.app.Adapters.IModelSelectionChanged;
import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;

public abstract class BaseActionList extends BaseActivity implements IModelSelectionChanged<Action> {
    ActionArrayAdapter arrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.action_list_to_select);

        prepareModelAndPresent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        prepareModelAndPresent();
    }

    void prepareModelAndPresent(){
        ListView listView = (ListView) findViewById(R.id.ActionListView);

        arrayAdapter = new ActionArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, this);

        listView.setAdapter(arrayAdapter);

        Helper.setTopText(this);
    }

    public void onClick(View v){
        if(v.getId() == R.id.BtnAddNewAction) {
            Helper.startSelectActionIntent(this);
        }
    }

    @Override
    public abstract void SelectionChanged(Action model);
}
