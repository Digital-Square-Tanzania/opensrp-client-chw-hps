package org.smartregister.chw.hps.interactor;


import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.hps.HpsLibrary;
import org.smartregister.chw.hps.R;
import org.smartregister.chw.hps.actionhelper.HpsClientCriteriaActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsCurativeServicesActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsEducationOnBehaviouralChangeActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsPreventiveServicesActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsReferralServicesActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsRemarksActionHelper;
import org.smartregister.chw.hps.contract.BaseHpsVisitContract;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.AppExecutors;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.sync.helper.ECSyncHelper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class BaseHpsServiceVisitInteractor extends BaseHpsVisitInteractor {

    private final HpsLibrary hpsLibrary;
    private final LinkedHashMap<String, BaseHpsVisitAction> actionList;
    protected BaseHpsVisitContract.InteractorCallBack callBack;
    protected AppExecutors appExecutors;
    String visitType;
    private ECSyncHelper syncHelper;
    private Context mContext;


    @VisibleForTesting
    public BaseHpsServiceVisitInteractor(AppExecutors appExecutors, HpsLibrary HpsLibrary, ECSyncHelper syncHelper) {
        this.appExecutors = appExecutors;
        this.hpsLibrary = HpsLibrary;
        this.syncHelper = syncHelper;
        this.actionList = new LinkedHashMap<>();
    }

    public BaseHpsServiceVisitInteractor(String visitType) {
        this(new AppExecutors(), HpsLibrary.getInstance(), HpsLibrary.getInstance().getEcSyncHelper());
        this.visitType = visitType;
    }

    @Override
    protected String getCurrentVisitType() {
        if (StringUtils.isNotBlank(visitType)) {
            return visitType;
        }
        return super.getCurrentVisitType();
    }

    @Override
    protected void populateActionList(BaseHpsVisitContract.InteractorCallBack callBack) {
        this.callBack = callBack;
        final Runnable runnable = () -> {
            try {
                evaluateHpsClientCriteria(details);
                evaluateHpsEducationOnBehavioralChange(details);
                evaluateOtherHpsServices(details);
                evaluateCurativeServices(details);
                evaluateReferralServices(details);
            } catch (BaseHpsVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateHpsClientCriteria(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {
        HpsClientCriteriaActionHelper actionHelper = new HpsClientCriteriaActionHelper(mContext, memberObject){

            @Override
            public BaseHpsVisitAction.Status evaluateStatusOnPayload() {
                if (StringUtils.isNotBlank(clientCriteria)) {
                    try {
                        new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
                        evaluateHpsRemarks(details);
                    } catch(Exception e){
                        Timber.e(e.toString());
                    }
                    return BaseHpsVisitAction.Status.COMPLETED;
                }
                return BaseHpsVisitAction.Status.PENDING;
            }
        };
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.client_criteria))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.CLIENT_CRITERIA)
                .build();
        actionList.put(context.getString(R.string.client_criteria), action);
    }

    private void evaluateHpsEducationOnBehavioralChange(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsEducationOnBehaviouralChangeActionHelper actionHelper = new HpsEducationOnBehaviouralChangeActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_education_on_behavioural_change))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.EDUCATION_ON_BEHAVIOURAL_CHANGE)
                .build();
        actionList.put(context.getString(R.string.hps_education_on_behavioural_change), action);
    }

    private void evaluateOtherHpsServices(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsPreventiveServicesActionHelper actionHelper = new HpsPreventiveServicesActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_preventive_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HPS_PREVENTIVE_SERVICES)
                .build();
        actionList.put(context.getString(R.string.hps_preventive_services), action);
    }

    private void evaluateCurativeServices(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsCurativeServicesActionHelper actionHelper = new HpsCurativeServicesActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_curative_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HPS_CURATIVE_SERVICES)
                .build();
        actionList.put(context.getString(R.string.hps_curative_services), action);
    }

    private void evaluateReferralServices(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsReferralServicesActionHelper actionHelper = new HpsReferralServicesActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_referral_services))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HPS_REFERRAL_SERVICES)
                .build();
        actionList.put(context.getString(R.string.hps_referral_services), action);
    }

    private void evaluateHpsRemarks(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsRemarksActionHelper actionHelper = new HpsRemarksActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_remarks))
                .withOptional(false)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HPS_REMARKS)
                .build();
        actionList.put(context.getString(R.string.hps_remarks), action);
    }


    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.HPS_CLIENT_FOLLOW_UP_VISIT;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.HPS_CLIENT_SERVICES;
    }

}
