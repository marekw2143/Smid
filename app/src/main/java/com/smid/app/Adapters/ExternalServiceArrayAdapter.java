package com.smid.app.Adapters;

import android.content.Context;

import com.smid.app.model.businessLogic.ExternalService;
import com.smid.app.modelManagers.ExternalServiceModelManager;
import com.smid.app.modelManagers.ModelManager;

/**
 * Created by marek on 29.06.16.
 */
public class ExternalServiceArrayAdapter extends GenericArrayAdapter<ExternalService> {
    public ExternalServiceArrayAdapter(Context context, int resource, IModelSelectionChanged<ExternalService> modelSelectionChanged) {
        super(context, resource, modelSelectionChanged);
    }

    @Override
    protected ModelManager<ExternalService> getModelManager(Context context) {
        return new ExternalServiceModelManager(context);
    }
}
