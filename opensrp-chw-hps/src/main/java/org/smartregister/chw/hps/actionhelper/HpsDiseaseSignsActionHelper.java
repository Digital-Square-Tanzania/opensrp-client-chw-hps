package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.CHILD_EXCESSIVE_CRYING;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.DISEASE_SIGNS_FEMALE;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.DISEASE_SIGNS_MALE;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;

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

public class HpsDiseaseSignsActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {
    protected String jsonPayload;

    protected String diseases_signs;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;

    public HpsDiseaseSignsActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    private String normalizeGender(String gender) {
        if (StringUtils.isBlank(gender)) {
            return "";
        }

        if ("male".equalsIgnoreCase(gender) || "m".equalsIgnoreCase(gender)) {
            return "male";
        }

        if ("female".equalsIgnoreCase(gender) || "f".equalsIgnoreCase(gender)) {
            return "female";
        }

        return gender;
    }

    private JSONArray removeOption(JSONArray options, String optionKey) throws JSONException {
        if (options == null) {
            return null;
        }

        JSONArray filtered = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            JSONObject option = options.optJSONObject(i);
            if (option == null) {
                continue;
            }

            String key = option.optString("key", "");
            if (optionKey.equalsIgnoreCase(key)) {
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
            JSONObject global = jsonObject.optJSONObject("global");
            if (global == null) {
                global = new JSONObject();
                jsonObject.put("global", global);
            }

            if (memberObject != null) {
                int ageYears = memberObject.getAge();
                global.put("gender", normalizeGender(memberObject.getGender()));
                global.put("age", ageYears);

                if (ageYears > 5) {
                    JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);

                    JSONObject maleField = JsonFormUtils.getFieldJSONObject(fieldsArray, DISEASE_SIGNS_MALE);
                    if (maleField != null) {
                        JSONArray filtered = removeOption(maleField.optJSONArray("options"), CHILD_EXCESSIVE_CRYING);
                        if (filtered != null) {
                            maleField.put("options", filtered);
                        }
                    }

                    JSONObject femaleField = JsonFormUtils.getFieldJSONObject(fieldsArray, DISEASE_SIGNS_FEMALE);
                    if (femaleField != null) {
                        JSONArray filtered = removeOption(femaleField.optJSONArray("options"), CHILD_EXCESSIVE_CRYING);
                        if (filtered != null) {
                            femaleField.put("options", filtered);
                        }
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
            diseases_signs = JsonFormUtils.getValue(jsonObject, "has_diseases_signs_and_symptoms");
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


        if (StringUtils.isNotBlank(diseases_signs)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
