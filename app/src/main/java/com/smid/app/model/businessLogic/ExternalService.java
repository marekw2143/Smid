package com.smid.app.model.businessLogic;

import android.content.Context;

import com.smid.app.model.ModelBase;

/**
 * Created by marek on 29.06.16.
 */
public class ExternalService extends ModelBase {
    private String friendlyName;

    /**
     * ten intent zwraca id oraz friendly name akcji.
     */
    private String optionsFetcherIntent;

    /**
     * Ten intent przyjmuje
     */
    private String optionExecutorIntent;

    public String getOptionsFetcherIntent() {
        return optionsFetcherIntent;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getOptionExecutorIntent() {
        return optionExecutorIntent;
    }

    public void setOptionsFetcherIntent(String optionsFetcherIntent) {
        this.optionsFetcherIntent = optionsFetcherIntent;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void setOptionExecutorIntent(String optionExecutorIntent) {
        this.optionExecutorIntent = optionExecutorIntent;
    }

    @Override
    public String GetTextRepresentation(Context context) {
        return this.getFriendlyName();
    }
}
