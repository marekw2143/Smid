package com.smid.app.modelManagers;

import android.content.Context;

import com.smid.app.GD_CONSTS;
import com.smid.app.changesDetector.MoveDescription;
import com.smid.app.model.businessLogic.Gesture;
import com.smid.app.payments.PaymentHelper;

import java.util.List;

/**
 * Created by marek on 18.04.16.
 */
public class GestureModelManager extends ModelManager<Gesture> {
    static final Object _cacheSync = new Object();

    static List<Gesture> _cache;

    @Override
    protected String getModelDirectory() {
        return "Gestures";
    }

    @Override
    protected String getFilenamePattern() {
        return "gest";
    }

    public GestureModelManager(Context context) {
        super(context);
        clearCache();
    }

    protected String SerializeModel(Gesture gesture) {
        StringBuilder sb = new StringBuilder(100);

        sb.append(gesture.getId());
        sb.append("\n");
        sb.append(gesture.getName());
        sb.append("\n");
        sb.append(gesture.getActionID());
        sb.append("\n");
        sb.append(gesture.getMove());
        sb.append("\n");

        return sb.toString();
    }

    @Override
    protected Gesture DeserializeModel(String representation, String fullFilePath) {
        Gesture gesture = new Gesture();
        String[] parts = representation.split("\n");

        long idPart = Long.parseLong(parts[0]);
        String name = parts[1];
        long actionId = Long.parseLong(parts[2]);

        gesture.setId(idPart);
        gesture.setName(name);
        gesture.setActionID(actionId);
        gesture.setMove(parts[3]);

        return gesture;
    }

    @Override
    public void Delete(Gesture model) {
        clearCache();

        super.Delete(model);
    }

    @Override
    public void Persist(Gesture model) throws GestureNameAlreadyExistsException, NullGestureNameException, ExternalServiceWithSameComponentAlreadyExists, NeedsPaymentException {
        clearCache();
        List<Gesture> gestures = this.GetItems();

        if(model.getId() < 0 && !PaymentHelper.HasAccess(context)) { // only when adding new one
            if(gestures.size() > GD_CONSTS.MAX_FREE_GESTURES_AMOUNT - 1) {
                PaymentHelper.showAlertNeedsBuying(context);

                return;
            }
        }


        if(model.getName().equals("")){
            throw new NullGestureNameException();
        }

        for(int i=0; i< gestures.size(); i++) {
            Gesture another = gestures.get(i);

            if(another.getId() != model.getId()) {
                String anotherName = another.getName();
                String currentName = model.getName();
                if(anotherName.equals(currentName)) {
                    throw new GestureNameAlreadyExistsException();
                }
            }
        }

        super.Persist(model);
    }

    public Gesture getGestureForMove(String move) {
        List<Gesture> gestures = this.GetItems();

        for(int i=0;i<gestures.size();i++) {
            Gesture g  = gestures.get(i);

            if(g.getMove().equals(move)) {
                return g;
            }
        }

        return null;
    }

    public List<Gesture> FastGetGestures() {
        synchronized (_cacheSync) {
            if (_cache == null) {
                _cache = GetItems();
            }

            return _cache;
        }
    }

    void clearCache() {
        synchronized (_cacheSync) {
            _cache = null;
        }
    }

    public Gesture getValidMoveForDescription(MoveDescription moveDescription) {
        List<Gesture> gestures = FastGetGestures();

        if(moveDescription == null){
            return null;
        }

        if(!moveDescription.isValid()){
            return null;
        }

        for(int i=0;i<gestures.size();i++) {
            if(gestures.get(i).getMove().equals(moveDescription.getMove())) {
                return gestures.get(i);
            }
        }

        return null;
    }
}
