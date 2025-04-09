package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static org.smartregister.AllConstants.OPTIONS;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HpsCurativeServicesActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {
    protected String jsonPayload;

    protected String wereCurativeServicesProvided;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;


    public HpsCurativeServicesActionHelper(Context context, MemberObject memberObject) {
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
            JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);

            JSONObject global = jsonObject.getJSONObject("global");

            global.put("gender", memberObject.getGender());

            // Check age for client to have MUAC option
            int age = new Period(new DateTime(memberObject.getAge()),
                    new DateTime()).getYears();
            //Get key field and options
            JSONObject testConducted = JsonFormUtils.getFieldJSONObject(fieldsArray, "diseases_test_conducted");
            JSONArray testConductedOptions = testConducted.getJSONArray(OPTIONS);

            if (age >= 5) {
                for (int i = 0; i < testConductedOptions.length(); i++) {
                    JSONObject option = testConductedOptions.getJSONObject(i);
                    if (option.getString(KEY).equals("arm_circumference")) {
                        testConductedOptions.remove(i);
                    }
                }
            }

            // Check age for treatment provided has RUTF option
            JSONObject treatmentProvided = JsonFormUtils.getFieldJSONObject(fieldsArray, "treatment_provided");
            JSONArray treatmentProvidedOptions = treatmentProvided.getJSONArray(OPTIONS);

            if (age >= 5) {
                for (int i = 0; i < treatmentProvidedOptions.length(); i++) {
                    JSONObject option = treatmentProvidedOptions.getJSONObject(i);
                    if (option.getString(KEY).equals("food_supplements")) {
                        treatmentProvidedOptions.remove(i);
                    }
                }
            }

            //Check age for treatment provided has oral rehydration solutions
             JSONObject oralRehydration = JsonFormUtils.getFieldJSONObject(fieldsArray,"treatment_provided");
             JSONArray  oralRehydrationOptions = oralRehydration.getJSONArray(OPTIONS);

             if (age >= 5){
                 for (int i = 0; i < oralRehydrationOptions.length(); i++) {
                     JSONObject option = oralRehydrationOptions.getJSONObject(i);
                     if (option.getString(KEY).equals("oral_rehydration_solutions")) {
                         oralRehydrationOptions.remove(i);
                     }
                 }
             }

            //Check age for treatment provided has amoxicillin DT
            JSONObject amoxicillinDT = JsonFormUtils.getFieldJSONObject(fieldsArray,"treatment_provided");
            JSONArray amoxicillinDTOptions = amoxicillinDT.getJSONArray(OPTIONS);

            if (age >= 5){
                for (int i = 0; i < amoxicillinDTOptions.length(); i++) {
                    JSONObject option = amoxicillinDTOptions.getJSONObject(i);
                    if (option.getString(KEY).equals("amoxicillin_dt")) {
                        amoxicillinDTOptions.remove(i);
                    }
                }
            }

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
            wereCurativeServicesProvided = JsonFormUtils.getValue(jsonObject, "provision_of_curative_services");

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
        if (StringUtils.isNotBlank(wereCurativeServicesProvided)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
