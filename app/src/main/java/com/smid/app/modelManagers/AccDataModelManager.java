package com.smid.app.modelManagers;

import android.content.Context;
import android.util.Pair;

import com.smid.app.model.movement.AccData;

import java.util.List;

/**
 * Created by marek on 22.04.16.
 */
public class AccDataModelManager extends ModelManager<AccData> {

    @Override
    protected String getModelDirectory() {
        return  "aMoveData";
    }

    @Override
    protected String getFilenamePattern() {
        return "accData";
    }

    public AccDataModelManager(Context context) {
        super(context);
    }

    @Override
    protected String SerializeModel(AccData model) {
        StringBuilder sb = new StringBuilder(model.getX().size() * 1000);

        sb.append("time,");
        for (Pair<Long, Float> t : model.getX())
        {
            sb.append(t.first - model.getStartTime());
            sb.append(",");
        }

        sb.append("\nx,");
        writeToSb(sb, model.getX());
        sb.append("\ny,");
        writeToSb(sb, model.getY());
        sb.append("\nz,");
        writeToSb(sb, model.getZ());

        return sb.toString();
    }

    protected void writeToSb(StringBuilder sb, List<Pair<Long, Float>>lst){
        for(int i=0;i<lst.size(); i++) {
            sb.append(lst.get(i).second);

            if(i<lst.size() - 1) {
                sb.append(",");
            }
        }
    }

    @Override
    protected AccData DeserializeModel(String representation, String fullFilePath) {
        AccData data = new AccData(fullFilePath, null, null, null, 0);

        data.setId(getIdFromFullFilePath(fullFilePath));

        return data;
    }
}
