package com.smid.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.modelManagers.ActionModelManager;

import java.util.List;

public class ActionDetails extends BaseActivity {

    private Action action;
    private ActionModelManager actionModelManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionModelManager = new ActionModelManager(this);

        try {

            setContentView(R.layout.action_details);

            Intent i = getIntent();

            Bundle b = i.getExtras();

            if (b != null) {
                Object action_id_obj = b.get(GD_CONSTS.Action_ID);

                if (action_id_obj != null) {
                    Long action_id = (Long) action_id_obj;
                    actionModelManager = new ActionModelManager(this);
                    action = actionModelManager.GetItemById(action_id);
                }
            }


            // Set layout
            prepareView();
        }catch (Exception ex) {
            Helper.logException(this, ex);
        }

    }

    private void prepareView() {
        Helper.setTopText(this);
        getActionName().setText(action.GetTextRepresentation(this));
    }

    protected ViewGroup getActionDetailsView () {
        return (ViewGroup) getmainLayout().findViewById(R.id.ActionDetails_MainGrid);
    }

    protected ViewGroup getmainLayout() {
        return (ViewGroup) findViewById(R.id.ActionDetails_MainLayout);
    }

    protected TextView getActionName (){
        return (TextView) getActionNameLayout().findViewById(R.id.ActionName_Value);
    }

    protected ViewGroup getActionNameLayout () {
        return (ViewGroup) getActionDetailsView().findViewById(R.id.ActionName_Layout);
    }

    public void onClick(View v) {
        try {
            if(v.getId() == R.id.ActionDetails_BtnMainMenu){
                Helper.startMainMenu(this);
            }else if (v.getId() == R.id.ActionDelete) {
                List<Gesture> associatedGestures = actionModelManager.getGesturesAssosiatedWithAction(action);

                if(!associatedGestures.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        doDelete();

                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            } catch (Exception ex) {
                                Helper.logException(ActionDetails.this, ex);
                            }
                        }
                    };

                    builder.setMessage(getStringDeletingAssociatedActions(associatedGestures, action.GetTextRepresentation(ActionDetails.this)))
                            .setPositiveButton(getString(R.string.yes), dialogClickListener)
                            .setNegativeButton(getString(R.string.no), dialogClickListener)
                            .create().show();
                }

                else {
                    startDeletingAction();
                }
            }
        } catch (Exception ex) {
            Helper.logException(this, ex);
        }
    }

    private String getStringDeletingAssociatedActions(List<Gesture> associatedGestures, String actionName) {
        StringBuilder sb = new StringBuilder(getString(R.string.ActionDetails_DeletingAssociatedGestures_1) + " " + actionName + " "
                + getString(R.string.ActionDetails_DeletingAssociatedGestures_2) + " ");
        ;

        for(int i=0; i< associatedGestures.size();i++) {
            sb.append(associatedGestures.get(i).getName());
            if (i < associatedGestures.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(".");

        return sb.toString();

    }

    private String getStringTryingToDeleteActionNamed(String name) {
        return getString(R.string.ActionDetails_TryingToDeleteActionNamed) + name + " . " +
                getString(R.string.ActionDetails_TryingToDeleteActionNamed_2);
    }

    protected void startDeletingAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            doDelete();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                } catch (Exception ex) {
                    Helper.logException(ActionDetails.this, ex);
                }
            }
        };

        builder.setMessage(getStringTryingToDeleteActionNamed(action.GetTextRepresentation(this)))
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .create().show();
    }

    protected void doDelete() {
        actionModelManager.Delete(action);

        Helper.startMainMenu(this);
    }

}
