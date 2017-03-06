package com.smid.app.modelManagers;

import android.content.Context;

import com.smid.app.model.businessLogic.ExceptionInfo;

/**
 * Created by marek on 17.06.16.
 */
public class ExceptionInfoModelManager extends ModelManager<ExceptionInfo> {
    public ExceptionInfoModelManager(Context context) {
        super(context);
    }

    @Override
    protected String getModelDirectory() {
        return "ExceptionInfo";
    }

    @Override
    protected String getFilenamePattern() {
        return "sd_";
    }

    @Override
    protected String SerializeModel(ExceptionInfo model) {
        return Long.toString(model.getId()) + "\n" + model.content;
    }

    @Override
    protected ExceptionInfo DeserializeModel(String representation, String fullFilePath) {
        ExceptionInfo ei = new ExceptionInfo();

        ei.setId(Long.parseLong(representation.split("\n")[0]));
        ei.content = representation;

        return ei;
    }
}
