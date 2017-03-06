package com.smid.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.smid.app.Adapters.ExternalServiceArrayAdapter;
import com.smid.app.Adapters.IModelSelectionChanged;
import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;
import com.smid.app.model.businessLogic.ExternalService;
import com.smid.app.modelManagers.ActionModelManager;
import com.smid.app.modelManagers.ExternalServiceWithSameComponentAlreadyExists;
import com.smid.app.modelManagers.GestureNameAlreadyExistsException;
import com.smid.app.modelManagers.NullGestureNameException;

public class SelectActionFromExternalService extends BaseActivity implements IModelSelectionChanged<ExternalService> {

    ExternalService mExternalService = null;
    private ExternalServiceArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action_from_external_service);

        updateView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateView();
    }

    private void updateView() {
        arrayAdapter = new ExternalServiceArrayAdapter(this, android.R.layout.simple_list_item_1, this);

        ListView lv = (ListView) findViewById(R.id.SelectAction_MainLayout).findViewById(R.id.SelectAction_ContentLayout).findViewById(android.R.id.list);

        lv.setAdapter(arrayAdapter);

        Helper.setTopText(this);
    }


    @Override
    public void SelectionChanged(ExternalService model) {
        Intent i = new Intent(model.getOptionsFetcherIntent());

        mExternalService = model;

        startActivityForResult(i, GD_CONSTS.EXTERNAL_SERVICE_RequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GD_CONSTS.EXTERNAL_SERVICE_RequestCode ) {
            if(data == null) {
                Helper.showOkMessage(this, noActionDataFromExsternalService(), null);
            } else {
                Bundle b = data.getExtras();
                String optionId = null;
                String friendlyName = null;

                if (b != null) {
                    Object objExternalOptionId = b.get(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_ID);

                    if (objExternalOptionId != null) {
                        optionId = (String) objExternalOptionId;
                    }

                    Object objExternalOptionFriendlyName = b.get(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_ID);

                    if (objExternalOptionFriendlyName != null) {
                        friendlyName = (String) objExternalOptionFriendlyName;
                    }

                    Object objExternalFriendlyName = b.get(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_FRIENDLY_NAME);

                    if (objExternalFriendlyName != null) {
                        friendlyName = (String) objExternalFriendlyName;
                    }

                    Action action = new Action();

                    action.setExternalServiceId(mExternalService.getId());
                    action.setExternalServiceObjectId(optionId);
                    action.setName(friendlyName);

                    ActionModelManager actionModelManager = new ActionModelManager(this);
                    try {
                        actionModelManager.Persist(action);
                    } catch (GestureNameAlreadyExistsException e) {
                        Helper.logException(this, e);
                    } catch (NullGestureNameException e) {
                        Helper.logException(this, e);
                    } catch (ExternalServiceWithSameComponentAlreadyExists externalServiceWithSameComponentAlreadyExists) {
                        Helper.logException(this, externalServiceWithSameComponentAlreadyExists);
                    } catch (Exception ex) {
                        Helper.logException(this, ex);

                        Helper.showOkMessage(this, getString(R.string.SelectActionExternalSoruce_ErrorSavingAction), null);
                    }

                    Intent resultIntent = new Intent();

                    resultIntent.putExtra(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_ID, optionId);
                    resultIntent.putExtra(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_SERVICE_ID, mExternalService.getId());

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        }
    }

    protected String noActionDataFromExsternalService() {
        return getString(R.string.SelectActionExternalSource_DidntProvideValue)
                + " "
                + mExternalService.getFriendlyName()
                + " "
                + getString(R.string.SelectActionExternalSource_DidntProvideValue_2);
    }

}
