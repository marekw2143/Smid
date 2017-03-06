package com.smid.app.modelManagers;

import android.content.Context;

import com.smid.app.model.movement.MoveData;
import com.smid.app.model.movement.pojo.AccelerationData;

/**
 * Created by marek on 05.05.16.
 */
public class MoveDataModelManager extends ModelManager<MoveData> {
    @Override
    protected String getModelDirectory() {
        return "movementData";
    }

    @Override
    protected String getFilenamePattern() {
        return "moveData";
    }

    public MoveDataModelManager(Context context) {
        super(context);
    }

    @Override
    protected String SerializeModel(MoveData model) {

        int estSize = model.getAccelerationData().size() * 100;
        estSize += 100;//model.getGyroData().size() * 100;

        StringBuilder text = new StringBuilder(estSize);

        text.append("start time: ");
        text.append(model.getStartTime());
        text.append("\neop\n");


        text.append("accelerometer data (time, ax, ay, ax)\n");

        for(int i=0;i<model.getAccelerationData().size(); i++) {
            AccelerationData ad = model.getAccelerationData().get(i);
            text.append(ad.getEventTime() + "," + ad.getAccX() + "," + ad.getAccY() + "," + ad.getAccZ() + "\n");
        }

        text.append("eop\n");

        return text.toString();
    }

    @Override
    protected MoveData DeserializeModel(String representation, String fullFilePath) {
        MoveData md = new MoveData();

        md.setId(getIdFromFullFilePath(fullFilePath));

        return md;
    }
}
