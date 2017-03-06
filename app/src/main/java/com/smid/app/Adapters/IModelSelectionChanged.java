package com.smid.app.Adapters;

import com.smid.app.model.ModelBase;

/**
 * Created by marek on 09.06.16.
 */
public interface IModelSelectionChanged<T extends ModelBase> {
    void SelectionChanged(T model);

}
