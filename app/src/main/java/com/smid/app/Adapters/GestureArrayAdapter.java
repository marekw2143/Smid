package com.smid.app.Adapters;

import android.content.Context;

import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.modelManagers.GestureModelManager;
import com.smid.app.modelManagers.ModelManager;

/**
 * Created by marek on 08.06.16.
 */
public class GestureArrayAdapter extends GenericArrayAdapter<Gesture> {
    public GestureArrayAdapter(Context context, int resource, IModelSelectionChanged<Gesture> modelSelectionChanged) {
        super(context, resource, modelSelectionChanged);
    }

    @Override
    protected ModelManager<Gesture> getModelManager(Context context) {
        return (ModelManager<Gesture>) new GestureModelManager(context);
    }
}
