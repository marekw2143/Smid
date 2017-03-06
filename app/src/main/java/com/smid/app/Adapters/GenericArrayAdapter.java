package com.smid.app.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.smid.app.smid.R;
import com.smid.app.modelManagers.ModelManager;
import com.smid.app.model.ModelBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**\
 * Allows selecting/deselecting items
 * Created by marek on 08.04.16.
 */
public abstract class GenericArrayAdapter<T extends ModelBase> extends ArrayAdapter<T> {
    private final IModelSelectionChanged<T> modelSelectionChanged;

    private Set<T> selectedModels = new TreeSet<T>();

    private Map<T, View> modelViewMap = new HashMap<>();

    private ModelManager<T> manager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void markModelSelected(T model) {
        selectedModels.add(model);

        if(modelViewMap.containsKey(model)){
            View v = modelViewMap.get(model);
            v.setBackgroundColor(0x19d9b7FF);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void markModelUnselected(T model) {
        selectedModels.remove(model);

//        if(modelViewMap.containsKey(model)){
//            View v = modelViewMap.get(model);
//            v.setBackgroundResource(R.drawable.list_elem_unselected);
//        }
    }

    public void setModelManager(ModelManager<T> mgr) {
        this.manager = mgr;

        this.addAll(manager.GetItems());
    }

    public boolean isModelSelected(T model) {
        return selectedModels.contains(model);
    }

    public GenericArrayAdapter(Context context, int resource, IModelSelectionChanged<T> modelSelectionChanged) {
        super(context, resource);
        this.manager = getModelManager(context);
        this.modelSelectionChanged = modelSelectionChanged;


        this.addAll(manager.GetItems());

        //TODO: factory....
        //TODO: generalnie podczas edycji, formatki beda musialy na onZnowuWidoczne pobierac dane modelu....
    }

    protected abstract ModelManager<T> getModelManager(Context context);

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T model = this.getItem(position);

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(android.R.layout.simple_list_item_1, null);
        }

        v.setBackgroundResource(R.drawable.list_elem_unselected);
        v.setOnClickListener(new ModelClickListener(model));

        modelViewMap.put(model, v);

        model.FillViewWithData(v, getContext());

        return v;
    }

    void unselectAllModelsExceptOne(T model){
        Set<T> tmp = new TreeSet<T>(selectedModels);

        for (T t :tmp) {
            if(t.getId()!= model.getId()) {
               markModelUnselected(t);
            }
        }
    }
    class ModelClickListener implements  View.OnClickListener {
        private T model;

        public ModelClickListener(T model) {
            this.model = model;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {
            unselectAllModelsExceptOne(model);

            if(isModelSelected(model)) {
                markModelUnselected(this.model);

                if(modelSelectionChanged!= null) { // IMPORTANT: modelSelectionChanged should be triggered here, not in unselect/select model, as this is executed once per click while select/unselect may be executed several times!
                    modelSelectionChanged.SelectionChanged(model);
                }
            } else {
                markModelSelected(model);

                if(modelSelectionChanged!= null){
                    modelSelectionChanged.SelectionChanged(model);
                }
            }
        }
    }
}

