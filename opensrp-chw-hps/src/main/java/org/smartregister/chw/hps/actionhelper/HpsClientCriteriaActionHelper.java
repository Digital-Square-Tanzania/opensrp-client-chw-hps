package org.smartregister.chw.hps.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HpsClientCriteriaActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {

    protected static String clientCriteria;
    protected String jsonPayload;
    protected String baseEntityId;
    protected Context context;

    protected MemberObject memberObject;


    public HpsClientCriteriaActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");

            global.put("sex", memberObject.getGender());

            int age = new Period(new DateTime(memberObject.getAge()),
                    new DateTime()).getYears();
            global.put("age", age);
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            clientCriteria = JsonFormUtils.getValue(jsonObject, "client_criteria");

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public BaseHpsVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseHpsVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isNotBlank(clientCriteria)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        //overridden
    }

}
