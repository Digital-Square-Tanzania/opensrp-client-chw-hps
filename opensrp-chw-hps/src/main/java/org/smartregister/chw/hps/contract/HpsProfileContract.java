package org.smartregister.chw.hps.contract;

import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public interface HpsProfileContract {
    interface View extends InteractorCallBack {

        void setProfileViewWithData();

        void setOverDueColor();

        void openMedicalHistory();

        void openUpcomingService();

        void openFamilyDueServices();

        void showProgressBar(boolean status);

        void hideView();

        void openFollowupVisit();

    }

    interface Presenter {

        void fillProfileData(@Nullable MemberObject memberObject);

        void saveForm(String jsonString);

        @Nullable
        View getView();

        void refreshProfileBottom();

        void recordHpsButton(String visitState);
    }

    interface Interactor {

        void refreshProfileInfo(MemberObject memberObject, InteractorCallBack callback);

        void saveRegistration(String jsonString, final InteractorCallBack callBack);
    }


    interface InteractorCallBack {

        void refreshMedicalHistory(boolean hasHistory);

        void refreshUpComingServicesStatus(String service, AlertStatus status, Date date);

        void refreshFamilyStatus(AlertStatus status);

        void startServiceForm();


        void continueService();

    }
}