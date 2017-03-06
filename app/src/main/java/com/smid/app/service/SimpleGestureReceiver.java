package com.smid.app.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.smid.app.GD_CONSTS;
import com.smid.app.GestureListToSelect;
import com.smid.app.Helper;
import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.Action;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.modelManagers.ActionModelManager;
import com.smid.app.taskerIntegration.TaskerPlugin;

/**
 * Created by marek on 27.06.16.
 */
public class SimpleGestureReceiver implements ISimpleGestureDetectorReceiver {

    @Override
    public void gestureReceived(Gesture gesture, final Context ctx) {
        {
            try {

                if(GD_CONSTS.IsTaskerPluginOnly(ctx)) {
                    Intent i = GestureListToSelect.createRequestIntent();

                    i.putExtra(GestureListToSelect.KEY_VALUE, Long.toString(gesture.getId()));

                    TaskerPlugin.Event.addPassThroughMessageID(i);

                    ctx. sendBroadcast(i);

                    return;
                } else {
                    ActionModelManager actionModelManager = new ActionModelManager(ctx);
                    Action action = actionModelManager.GetItemById(gesture.getActionID());
                    if (action == null) {
                        Helper.showOkMessage(ctx, ctx.getString(R.string.Action_NoActionAssociatedWithGesture), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper.startGestureRegisterDetect(ctx);
                            }
                        });
                    }
                    action.Execute(ctx);
                }
            } catch (Exception ex) {
                Helper.logException(ctx, ex);
            }
        }
    }
}
