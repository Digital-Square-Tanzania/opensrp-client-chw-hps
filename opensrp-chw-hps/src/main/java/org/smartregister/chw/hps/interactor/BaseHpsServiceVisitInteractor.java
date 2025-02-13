package org.smartregister.chw.hps.interactor;


import android.content.Context;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.hps.R;
import org.smartregister.chw.hps.HpsLibrary;
import org.smartregister.chw.hps.actionhelper.HpsActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsBehavioralChangeEducationActionHelper;
import org.smartregister.chw.hps.actionhelper.HpsMedicalHistoryActionHelper;
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

    protected BaseHpsVisitContract.InteractorCallBack callBack;

    String visitType;
    private final HpsLibrary hpsLibrary;
    private final LinkedHashMap<String, BaseHpsVisitAction> actionList;
    protected AppExecutors appExecutors;
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
                evaluateHpsMedicalHistory(details);
                evaluateHpsPhysicalExam(details);
                evaluateHpsHTS(details);
                evaluateHpsBehavioralChangeEducation(details);

            } catch (BaseHpsVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    private void evaluateHpsMedicalHistory(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsMedicalHistoryActionHelper actionHelper = new HpsMedicalHistory(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_medical_history))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.MEDICAL_HISTORY)
                .build();
        actionList.put(context.getString(R.string.hps_medical_history), action);

    }

    private void evaluateHpsPhysicalExam(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsPhysicalExamActionHelper actionHelper = new HpsPhysicalExamActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_physical_examination))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.PHYSICAL_EXAMINATION)
                .build();
        actionList.put(context.getString(R.string.hps_physical_examination), action);
    }

    private void evaluateHpsHTS(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsActionHelper actionHelper = new HpsActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_hts))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HTS)
                .build();
        actionList.put(context.getString(R.string.hps_hts), action);
    }
    private void evaluateHpsBehavioralChangeEducation(Map<String, List<VisitDetail>> details) throws BaseHpsVisitAction.ValidationException {

        HpsBehavioralChangeEducationActionHelper actionHelper = new HpsBehavioralChangeEducationActionHelper(mContext, memberObject);
        BaseHpsVisitAction action = getBuilder(context.getString(R.string.hps_education_behavioral_change))
                .withOptional(true)
                .withDetails(details)
                .withHelper(actionHelper)
                .withFormName(Constants.HPS_FOLLOWUP_FORMS.HPS_EDUCATION_BEHAVIORAL_CHANGE)
                .build();
        actionList.put(context.getString(R.string.hps_education_behavioral_change), action);
    }

    @Override
    protected String getEncounterType() {
        return Constants.EVENT_TYPE.HPS_SERVICES;
    }

    @Override
    protected String getTableName() {
        return Constants.TABLES.HPS_SERVICE;
    }

    private class HpsMedicalHistory extends org.smartregister.chw.hps.actionhelper.HpsMedicalHistoryActionHelper {


        public HpsMedicalHistory(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateHpsPhysicalExam(details);
                    evaluateHpsHTS(details);
                } catch (BaseHpsVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

    private class HpsPhysicalExamActionHelper extends org.smartregister.chw.hps.actionhelper.HpsPhysicalExamActionHelper {

        public HpsPhysicalExamActionHelper(Context context, MemberObject memberObject) {
            super(context, memberObject);
        }

        @Override
        public String postProcess(String s) {
            if (StringUtils.isNotBlank(medical_history)) {
                try {
                    evaluateHpsHTS(details);
                } catch (BaseHpsVisitAction.ValidationException e) {
                    e.printStackTrace();
                }
            }
            new AppExecutors().mainThread().execute(() -> callBack.preloadActions(actionList));
            return super.postProcess(s);
        }

    }

}
