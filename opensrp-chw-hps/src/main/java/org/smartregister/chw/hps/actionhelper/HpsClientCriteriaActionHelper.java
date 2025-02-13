package org.smartregister.chw.hps.actionhelper;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.JsonFormUtils;
import org.smartregister.chw.hps.util.VisitUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HpsClientCategoryActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {

    protected static String systolic;
    protected String jsonPayload;
    protected String baseEntityId;
    protected Context context;

    protected MemberObject memberObject;


    public HpsClientCategoryActionHelper(Context context, MemberObject memberObject) {
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

            int age = new Period(new DateTime(memberObject.getAge()),
                    new DateTime()).getYears();

            String known_allergies = HpsEducationOnBehaviouralChangeActionHelper
                    .known_allergies;

            global.put("known_allergies", known_allergies);
            global.put("age", age);
            Timber.tag("AGE mtu").d(String.valueOf(age));

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONObject global = jsonObject.getJSONObject("global");
            global.put("sex", memberObject.getGender());
            systolic = JsonFormUtils.getValue(jsonObject, "systolic");

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
        if (systolic.equalsIgnoreCase(VisitUtils.Complete)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        if (systolic.equalsIgnoreCase(VisitUtils.Ongoing)) {
            return BaseHpsVisitAction.Status.PARTIALLY_COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        //overridden
    }

}
