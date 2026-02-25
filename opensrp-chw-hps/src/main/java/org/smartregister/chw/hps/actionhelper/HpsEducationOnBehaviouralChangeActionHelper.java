package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.TYPE_OF_EDUCATION_PROVIDED;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;
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

public class HpsEducationOnBehaviouralChangeActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {
    protected String jsonPayload;

    protected String healthEducationProvided;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;

    private static final String KEY_GLOBAL = "global";
    private static final String KEY_GLOBAL_AGE = "age";
    private static final String KEY_GLOBAL_SEX = "sex";
    private static final String KEY_OPTIONS = "options";


    public HpsEducationOnBehaviouralChangeActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    private String normalizeSex(String sex) {
        if (StringUtils.isBlank(sex)) {
            return "";
        }

        if ("male".equalsIgnoreCase(sex) || "m".equalsIgnoreCase(sex)) {
            return "male";
        }

        if ("female".equalsIgnoreCase(sex) || "f".equalsIgnoreCase(sex)) {
            return "female";
        }

        return sex;
    }

    private JSONArray filterEducationOptionsByAge(JSONArray options, int ageYears) throws JSONException {
        if (options == null) {
            return null;
        }

        boolean allowSexualTransmissionInfections = ageYears >= 5;
        boolean allowFamilyPlanning = ageYears >= 10;
        boolean allowAncDangerSigns = ageYears >= 12;

        JSONArray filtered = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.optJSONObject(i);
            if (option == null) {
                continue;
            }

            String key = option.optString("key", "");
            if ("sexual_transmission_infections".equalsIgnoreCase(key) && !allowSexualTransmissionInfections) {
                continue;
            }
            if ("family_planning".equalsIgnoreCase(key) && !allowFamilyPlanning) {
                continue;
            }
            if ("anc_danger_signs".equalsIgnoreCase(key) && !allowAncDangerSigns) {
                continue;
            }

            filtered.put(option);
        }
        return filtered;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            // Ensure global vars exist for any future rules usage.
            JSONObject global = jsonObject.optJSONObject(KEY_GLOBAL);
            if (global == null) {
                global = new JSONObject();
                jsonObject.put(KEY_GLOBAL, global);
            }
            if (memberObject != null) {
                global.put(KEY_GLOBAL_AGE, memberObject.getAge());
                global.put(KEY_GLOBAL_SEX, normalizeSex(memberObject.getGender()));
            }

            // Option-level relevance is not supported for multi_select_list; filter options here.
            if (memberObject != null) {
                JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);
                JSONObject educationField = JsonFormUtils.getFieldJSONObject(fieldsArray, TYPE_OF_EDUCATION_PROVIDED);
                if (educationField != null) {
                    JSONArray options = educationField.optJSONArray(KEY_OPTIONS);
                    JSONArray filteredOptions = filterEducationOptionsByAge(options, memberObject.getAge());
                    if (filteredOptions != null) {
                        educationField.put(KEY_OPTIONS, filteredOptions);
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
            healthEducationProvided = JsonFormUtils.getValue(jsonObject, "provision_of_preventive_services");
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


        if (StringUtils.isNotBlank(healthEducationProvided)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
