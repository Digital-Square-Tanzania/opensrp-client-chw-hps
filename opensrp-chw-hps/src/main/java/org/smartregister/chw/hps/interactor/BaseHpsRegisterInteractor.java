package org.smartregister.chw.hps.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.hps.contract.HpsRegisterContract;
import org.smartregister.chw.hps.util.AppExecutors;
import org.smartregister.chw.hps.util.HpsUtil;

public class BaseHpsRegisterInteractor implements HpsRegisterContract.Interactor {

    private AppExecutors appExecutors;

    @VisibleForTesting
    BaseHpsRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseHpsRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void saveRegistration(final String jsonString, final HpsRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = () -> {
            try {
                HpsUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            appExecutors.mainThread().execute(() -> callBack.onRegistrationSaved());
        };
        appExecutors.diskIO().execute(runnable);
    }
}
