package com.smid.app.model.businessLogic;

import android.content.Context;
import android.view.View;

import com.smid.app.model.ModelBase;


/**
 * Created by marek on 02.04.16.
 */
public class Gesture extends ModelBase {

    private long ActionID = -1;

    static int newGestureContainer = -1;

    private String move;

    public Gesture() {
        super();
        this.id = newGestureContainer--;

        setName("Jestem Gesture " + this.getId());
    }

    public Gesture(String definition) {
        this();
        try {
            String[] paths = definition.split(";");

            int idPart = Integer.parseInt(paths[0].split(":")[1]);

            String name = paths[1].split(":")[1];

            this.setId(idPart);
            this.setName(name);
        } catch (Exception ex) {
            this.setName("Inproper Gesture");
        }
    }

    private String name;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    /**
     * id,name
     * TO impolement: id,name,gesture_data
     *
     * @return
     */
    @Override
    public String GetTextRepresentation(Context context) {
        return getName();
    }


    public void EditViewGenerate(Context ctx, View view) {

    }

    @Override
    public void FillDataFromVIew(View view) {
        // Name
//        EditText etGestureName = (EditText) view.findViewById(R.id.GestureNameValue);
//        setName(etGestureName.getText().toString());

//        // Associated action.
//        TextView txActionAssociation = (TextView) view.findViewById(R.id.GestureActionAssociationValue);
//        ActionAssociation aa = GetActionAssociation(ctx);

    }

    @Override
    public View CreateDetailsView(Context ctx) {
        return null;
    }

    public long getActionID() {
        return ActionID;
    }

    public void setActionID(long actionID) {
        ActionID = actionID;
    }

    public String getMove() {
        if(move == null || "".equals(move)) {
            move = "tmpMOve";
        }

        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
}