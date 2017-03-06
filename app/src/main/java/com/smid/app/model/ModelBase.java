package com.smid.app.model;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by marek on 02.04.16.
 */
public abstract  class ModelBase implements Comparable<ModelBase> {
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ModelBase() {
        this.id = -1; //this.id = idCounter++;
    }

    @Override
    public int compareTo(ModelBase another) {
        if(another == null) {
            return 1;
        }

        return this.id > another.id ? 1 : (this.id == another.id ? 0 : -1);
    }

    public String GetTextRepresentation(Context context) {
        if(this.getId() < 0) { // TODO: ensure this is called only by MOdelManger!!!! --> better - move to model manager code!!!
            this.setId(-100);// throw new IllegalStateException("This gesture has bad id");
        }

        return "ID:" + Long.toString(this.id);
    }


    /**
     * Creates view which can be used on listview.
     * @param view
     */
    public void FillViewWithData(View view, Context context) {
        TextView tv = (TextView) view.findViewById(android.R.id.text1);

        tv.setText(this.GetTextRepresentation(context));
    }

    public void GenerateEditView(View view) {

    }


    public void FillDataFromVIew(View view) {};


    /**
     * Creates view which represents model details.
     * @return
     */
    public View CreateDetailsView(Context ctx) {
        return null;
    };

    public void GetDataFromViewProperties() {


    }
}
