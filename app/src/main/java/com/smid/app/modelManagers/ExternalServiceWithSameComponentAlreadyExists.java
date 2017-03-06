package com.smid.app.modelManagers;

/**
 * Created by marek on 29.06.16.
 */
public class ExternalServiceWithSameComponentAlreadyExists extends Exception {
    private String componentName;

    public ExternalServiceWithSameComponentAlreadyExists(String componentName) {

        this.componentName = componentName;
    }

    public String getComponentName() {
        return componentName;
    }
}
