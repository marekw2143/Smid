package com.smid.app.modelManagers;

import android.content.Context;

import com.smid.app.GD_CONSTS;
import com.smid.app.model.businessLogic.ExternalService;
import com.smid.app.payments.PaymentHelper;

import java.util.List;

/**
 * Created by marek on 29.06.16.
 */
public class ExternalServiceModelManager extends ModelManager<ExternalService> {
    public ExternalServiceModelManager(Context context) {
        super(context);
    }

    @Override
    protected String getModelDirectory() {
        return GD_CONSTS.DIRECTORY_EXTERNAL_SERVICES;
    }

    @Override
    protected String getFilenamePattern() {
        return GD_CONSTS.FILENAME_PATTERN_EXTERNAL_SERVICES;
    }

    @Override
    protected String SerializeModel(ExternalService model) {
        StringBuilder sb = new StringBuilder();

        sb.append(Long.toString(model.getId()));
        sb.append("\n");
        sb.append(model.getFriendlyName());
        sb.append("\n");
        sb.append(model.getOptionExecutorIntent());
        sb.append("\n");
        sb.append(model.getOptionsFetcherIntent());

        return sb.toString();
    }

    @Override
    protected ExternalService DeserializeModel(String representation, String fullFilePath) {
        ExternalService externalService = new ExternalService();
        String[] parts = representation.split("\n");

        long idPart = Long.parseLong(parts[0]);
        String friendlyName = parts[1];
        String optionExecutorIntent = parts[2];
        String optionsFetcherIntent = parts[3];

        externalService.setId(idPart);
        externalService.setFriendlyName(friendlyName);
        externalService.setOptionExecutorIntent(optionExecutorIntent);
        externalService.setOptionsFetcherIntent(optionsFetcherIntent);

        return externalService;
    }

    @Override
    public void Persist(ExternalService model) throws GestureNameAlreadyExistsException, NullGestureNameException, ExternalServiceWithSameComponentAlreadyExists {
        List<ExternalService> serviceList = GetItems();

        for(int i=0;i<serviceList.size();i++) {
            ExternalService es = serviceList.get(i);

            if(es.getOptionExecutorIntent().equals(model.getOptionExecutorIntent())) {
                throw new ExternalServiceWithSameComponentAlreadyExists("executor intent");
            }

            if(es.getOptionsFetcherIntent().equals(model.getOptionsFetcherIntent())) {
                throw new ExternalServiceWithSameComponentAlreadyExists("options fetcher intent");
            }

            if(es.getFriendlyName().equals(model.getFriendlyName())) {
                throw new ExternalServiceWithSameComponentAlreadyExists("friendly name");
            }
        }

        try {
            super.Persist(model);
        } catch (NeedsPaymentException e) {
            PaymentHelper.showAlertNeedsBuying(context);

            return;
        }
    }
}
