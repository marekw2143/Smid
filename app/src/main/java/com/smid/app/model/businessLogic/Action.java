package com.smid.app.model.businessLogic;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.smid.app.ContactInterface.ContactDataFetcher;
import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;
import com.smid.app.smid.R;
import com.smid.app.externalServices.phoneCall.PhoneCallRegistrator;
import com.smid.app.externalServices.phoneCall.PhoneCall_PerformCall;
import com.smid.app.model.ModelBase;
import com.smid.app.modelManagers.ExternalServiceModelManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by marek on 02.04.16.
 */
public class Action extends ModelBase {
    private String name;

    private TypeOfAction actionType;

    private Uri contactUri;

    private long externalServiceId;

    private String externalServiceObjectId;



    public void Execute(final Context ctx) {
        try {
            if (GD_CONSTS.IsTaskerPluginOnly(ctx)) {

                return;
            } else {

                if (getActionType() == TypeOfAction.External) {
                    ExternalServiceModelManager externalServiceModelManager = new ExternalServiceModelManager(ctx);

                    long externalServiceId = getExternalServiceId();

                    ExternalService es = externalServiceModelManager.GetItemById(externalServiceId);

                    String executeIntent = es.getOptionExecutorIntent();


                    if (executeIntent.equals(PhoneCallRegistrator.executor)) {
                        Intent i = new Intent(ctx, PhoneCall_PerformCall.class);

                        ctx.bindService(i, new ServiceConnection() {
                            @Override
                            public void onServiceConnected(ComponentName name, IBinder service) {
                                IExternalService srv = ((PhoneCall_PerformCall.PhoneCallBinder) service).getService();

                                srv.call(getExternalServiceObjectId());
                            }

                            @Override
                            public void onServiceDisconnected(ComponentName name) {
                                Helper.logException(ctx, "Error during communication with external service - onServiceDisconnected called");
                            }

                        }, ctx.BIND_AUTO_CREATE);
                    } else {
                        Intent i = new Intent(executeIntent);

                        i.putExtra(GD_CONSTS.EXTERNAL_SERVICE_EXTERNAL_OPTION_ID, getExternalServiceObjectId());
                        i.setFlags(FLAG_ACTIVITY_NEW_TASK);

                        ctx.startActivity(i);
                    }
                } else {
                    ContactDataFetcher fetcher = new ContactDataFetcher();

                    if (true || getActionType() == TypeOfAction.Phone) {
                        String phoneNumber = fetcher.GetContactNumber(ctx, getContactUri());

                        try {
                            if (phoneNumber != null) { // TODO: == null !!!
                                Helper.showErrorMessage(ctx, getInvalidPhoneString(ctx));
                                return;
                            }
                        } catch (Exception ex) {
                            Helper.showErrorMessage(ctx, ctx.getString(R.string.Action_Error_During_Executing_Action));
                            Helper.logException(ctx, ex);

                            return;
                        }

                        Helper.Log("calling to ", phoneNumber);

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + phoneNumber));
                        callIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(callIntent);

                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ctx.startActivity(callIntent);
                        } // todo - upewnij sie czemu to jest wymagane ???

                    } else if (getActionType() == TypeOfAction.SMS) {

                    }
                }
            }
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
        }

    }

// getters & setters
    public Uri getContactUri() {
        return contactUri;
    }

    public void setContactUri(Uri contactUri) {
        this.contactUri = contactUri;
    }

    public TypeOfAction getActionType() {
        if(externalServiceId  > 0) {
            return TypeOfAction.External;
        }

        return actionType;
    }

    public void setActionType(TypeOfAction actionType) {
        this.actionType = actionType;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * id,name
     * @return
     */
    @Override
    public String GetTextRepresentation(Context context) {
        TypeOfAction actionType = getActionType();
        if(actionType== null) {
            Helper.showOkMessage(context, context.getString(R.string.Action_UnkownActionType), null);
        }

        switch (actionType) {
            case Phone:
                return "Phone to " + new ContactDataFetcher().GetContactName(context, getContactUri());

            case External:
                ExternalServiceModelManager externalServiceModelManager = new ExternalServiceModelManager(context);
                ExternalService es = externalServiceModelManager.GetItemById(getExternalServiceId());
                return es.getFriendlyName() + " : " + getName();
            default:
                return "Unknown";
        }
    }

    protected String getInvalidPhoneString(Context ctx) {
        ContactDataFetcher contactDataFetcher = new ContactDataFetcher();
        return ctx.getString(R.string.Action_Phone_Invalid_Phone_Number_1) + " " + contactDataFetcher.GetContactName(ctx, getContactUri())
                +ctx.getString(R.string.Action_Phone_Invalid_Phone_Number_2);

    }


    public long getExternalServiceId() {
        return externalServiceId;
    }

    public void setExternalServiceId(long externalServiceId) {
        this.externalServiceId = externalServiceId;
    }

    public String getExternalServiceObjectId() {
        return externalServiceObjectId;
    }

    public void setExternalServiceObjectId(String externalServiceObjectId) {
        this.externalServiceObjectId = externalServiceObjectId;
    }
}
