package org.smartregister.chw.hps.interactor;

import androidx.annotation.VisibleForTesting;

import org.smartregister.chw.hps.contract.HpsProfileContract;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.util.AppExecutors;
import org.smartregister.chw.hps.util.HpsUtil;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public class BaseHpsProfileInteractor implements HpsProfileContract.Interactor {
    protected AppExecutors appExecutors;

    @VisibleForTesting
    BaseHpsProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseHpsProfileInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void refreshProfileInfo(MemberObject memberObject, HpsProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshFamilyStatus(AlertStatus.normal);
            callback.refreshMedicalHistory(true);
            callback.refreshUpComingServicesStatus("Hps Visit", AlertStatus.normal, new Date());
        });
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final String jsonString, final HpsProfileContract.InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                HpsUtil.saveFormEvent(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        appExecutors.diskIO().execute(runnable);
    }
}
