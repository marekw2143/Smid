package com.smid.app.modelManagers;

import android.content.Context;
import android.net.Uri;

import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;
import com.smid.app.model.businessLogic.Action;
import com.smid.app.model.businessLogic.Gesture;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by marek on 08.06.16.
 */
public class ActionModelManager extends ModelManager<Action> {
    final GestureModelManager gestureModelManager;
    public ActionModelManager(Context context) {
        super(context);

        gestureModelManager = new GestureModelManager(context);
    }

    @Override
    protected String getModelDirectory() {
        return GD_CONSTS.ActionDirectory;
    }

    @Override
    protected String getFilenamePattern() {
        return GD_CONSTS.ActionFilenamePrefix;
    }

    @Override
    /**
     * Id
     * ActionType
     * contactUri
     */
    protected String SerializeModel(Action model) {
        StringBuilder sb = new StringBuilder(100);

        sb.append(model.getId() + "\n");
        sb.append(Helper.getActionType(model.getActionType()) + "\n");

        if(model.getContactUri() == null) {
            sb.append("\n");
        } else {
            sb.append(model.getContactUri().toString() + "\n");
        }

        sb.append(Long.toString(model.getExternalServiceId()) + "\n");
        sb.append(model.getExternalServiceObjectId() + "\n");
        sb.append(model.getName() + "\n");

        return sb.toString();
    }

    @Override
    protected Action DeserializeModel(String representation, String fullFilePath) {
        String[] parts = representation.split("\n");

        Action model = new Action();

        model.setId(Long.parseLong(parts[0]));
        model.setActionType(Helper.getActionType(parts[1]));

        if(parts[2].equals("")) {
            model.setContactUri(null);
        } else {
            model.setContactUri(Uri.parse(parts[2]));
        }

        model.setExternalServiceId(Long.parseLong(parts[3]));
        model.setExternalServiceObjectId(parts[4]);
        model.setName(parts[5]);

        return model;
    }

    @Override
    public void Delete(Action model) {
        List<Gesture> associatedGestures = getGesturesAssosiatedWithAction(model);

        for(int i=0;i<associatedGestures.size(); i++) {
            gestureModelManager.Delete(associatedGestures.get(i));
        }

        super.Delete(model);
    }

    public List<Gesture> getGesturesAssosiatedWithAction(Action action) {
        List<Gesture> gestures = gestureModelManager.GetItems();

        List<Gesture> ret = new LinkedList<>();

        for(int i=0;i<gestures.size();i++) {
            Gesture g = gestures.get(i);

            if(g.getActionID() == action.getId()) {
                ret.add(g);
            }
        }

        return ret;
    }

}