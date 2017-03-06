package com.smid.app.externalServices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smid.app.GD_CONSTS;
import com.smid.app.Helper;
import com.smid.app.smid.R;
import com.smid.app.model.businessLogic.ExternalService;
import com.smid.app.modelManagers.ExternalServiceModelManager;
import com.smid.app.modelManagers.ExternalServiceWithSameComponentAlreadyExists;

public class ExternalServiceRegistration extends AppCompatActivity {

    String friendlyName = null;

    String optionsFetcherIntent = null;

    String optionExecutorIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_external_service_registration);


            Intent i = getIntent();

            Bundle b = i.getExtras();

            if (b != null) {
                Object esFriendlyName = b.get(GD_CONSTS.EXTERNAL_SERVICE_FRIENDLY_NAME);

                if (esFriendlyName != null) {
                    friendlyName = esFriendlyName.toString();
                }

                Object esOExecutor = b.get(GD_CONSTS.EXTERNAL_SERVICE_OPTIONS_EXECUTOR_INTENT);
                if (esOExecutor != null) {
                    optionExecutorIntent = esOExecutor.toString();
                }

                Object esOFetcher = b.get(GD_CONSTS.EXTERNAL_SERVICE_OPTIONS_FETCHER_INTENT);
                if (esOFetcher != null) {
                    optionsFetcherIntent = esOFetcher.toString();
                }
            }

            TextView tv = (TextView) getMainLayout().findViewById(R.id.ExternalServiceRegistration_Description);

            StringBuilder sb = new StringBuilder(200);

            sb.append(getString(R.string.ExternalServiceRegistration_Description_1));
            sb.append(" \"");
            sb.append(friendlyName);
            sb.append("\" ");
            sb.append(getString(R.string.ExternalServiceRegistration_Description_2));

            tv.setText(sb.toString());
        } catch (Exception ex) {
            Helper.logException(this, ex);
        }
    }

    protected ViewGroup getMainLayout () {
        return (ViewGroup) findViewById(R.id.ExternalServiceRegistration_MainLayout);
    }

    public void onClick(View v){
        if(v.getId() == R.id.btnYes) {
            try {
                RegisterSrevice(this, friendlyName, optionsFetcherIntent, optionExecutorIntent);
            } catch (ExternalServiceWithSameComponentAlreadyExists ex) {
                StringBuilder text = new StringBuilder(getString(R.string.ExternalServiceRegistration_ErrorWhileRegistering));
                text.append(" ");
                text.append(ex.getComponentName());
                text.append(" ");
                text.append(getString(R.string.ExternalServiceRegistration_ErrorWhileRegistering_2));
                Helper.showOkMessage(this, text.toString(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExternalServiceRegistration.this.finish();
                    }
                });

                Helper.logException(this, ex);
            } catch (Exception e) {
                Helper.logException(this, e);
            }

        } else if (v.getId() == R.id.btnNo){
            Helper.showOkMessage(this, getString(R.string.ExternalServiceRegistration_NotAddingExternalService), null);
        }
    }

    public static void RegisterSrevice(Context ctx, String friendlyName, String fetcherIntent, String executorIntent) throws Exception {
        try {
            ExternalService es = new ExternalService();
            ExternalServiceModelManager externalServiceModelManager = new ExternalServiceModelManager(ctx);

            es.setFriendlyName(friendlyName);
            es.setOptionsFetcherIntent(fetcherIntent);
            es.setOptionExecutorIntent(executorIntent);

            externalServiceModelManager.Persist(es);
        } catch (Exception ex) {
            Helper.logException(ctx, ex);
            throw ex;
        }
    }
}
