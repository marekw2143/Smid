package com.smid.app.Adapters;

import android.content.Context;

import com.smid.app.model.businessLogic.Action;
import com.smid.app.modelManagers.ActionModelManager;
import com.smid.app.modelManagers.ModelManager;

/**
 * Created by marek on 08.06.16.
 */
public class ActionArrayAdapter extends GenericArrayAdapter<Action> {
    public ActionArrayAdapter(Context context, int resource, IModelSelectionChanged<Action> modelSelectionChanged) {
        super(context, resource, modelSelectionChanged);
    }

    @Override
    protected ModelManager<Action> getModelManager(Context context) {
        return new ActionModelManager(context);
    }
}
