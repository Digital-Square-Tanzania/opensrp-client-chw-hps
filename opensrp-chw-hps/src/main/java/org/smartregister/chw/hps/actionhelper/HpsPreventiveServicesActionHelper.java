package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.AllConstants.OPTIONS;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.HIV_SELF_TEST_KITS;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.PREVENTIVE_SERVICES_ABOVE_18;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.PREVENTIVE_SERVICES_ABOVE_5_BELOW_18;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.FAMILY_PLANNING_PILLS;
import static org.smartregister.chw.hps.util.Constants.OPTIONS_FIELDS.IRON_FOLIC_TABLETS;
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
import org.smartregister.client.utils.constants.JsonFormConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class HpsPreventiveServicesActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {

    protected String jsonPayload;

    protected String preventiveServicesProvided;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;


    public HpsPreventiveServicesActionHelper(Context context, MemberObject memberObject) {
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
            if (memberObject != null) {
                JSONObject global = jsonObject.optJSONObject("global");
                if (global != null) {
                    global.put("age", memberObject.getAge());
                    global.put("gender", memberObject.getGender());
                }

                if (!isFemaleAndAtLeast12()) {
                    JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);
                    removeRestrictedOptions(fieldsArray, PREVENTIVE_SERVICES_ABOVE_5_BELOW_18);
                    removeRestrictedOptions(fieldsArray, PREVENTIVE_SERVICES_ABOVE_18);
                }

                if (isAgeBelow18()) {
                    JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);
                    removeOptionByKey(fieldsArray, PREVENTIVE_SERVICES_ABOVE_5_BELOW_18, HIV_SELF_TEST_KITS);
                    removeOptionByKey(fieldsArray, PREVENTIVE_SERVICES_ABOVE_18, HIV_SELF_TEST_KITS);
                }
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }

    private boolean isFemaleAndAtLeast12() {
        return memberObject != null
                && memberObject.getAge() >= 12
                && "female".equalsIgnoreCase(memberObject.getGender());
    }

    private boolean isAgeBelow18() {
        return memberObject != null && memberObject.getAge() < 18;
    }

    private void removeRestrictedOptions(JSONArray fieldsArray, String fieldKey) throws JSONException {
        removeOptionByKey(fieldsArray, fieldKey, FAMILY_PLANNING_PILLS);
        removeOptionByKey(fieldsArray, fieldKey, IRON_FOLIC_TABLETS);
    }

    private void removeOptionByKey(JSONArray fieldsArray, String fieldKey, String optionKey) throws JSONException {
        JSONObject fieldObject = JsonFormUtils.getFieldJSONObject(fieldsArray, fieldKey);
        if (fieldObject == null) {
            return;
        }

        JSONArray options = fieldObject.optJSONArray(OPTIONS);
        if (options == null) {
            return;
        }

        for (int i = options.length() - 1; i >= 0; i--) {
            JSONObject option = options.optJSONObject(i);
            if (option != null && optionKey.equals(option.optString(KEY))) {
                options.remove(i);
            }
        }
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            preventiveServicesProvided = JsonFormUtils.getValue(jsonObject, "preventive_services_provided");
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
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);

            JSONObject preventiveServices = JsonFormUtils.getFieldJSONObject(fieldsArray, "preventive_services");
            String value = preventiveServices.getString(VALUE);

            List<String> valuesList = new ArrayList<>();
            JSONArray valuesArray = new JSONArray(value);

            for (int i = 0; i < valuesArray.length(); i++) {
                Object item = valuesArray.get(i);
                if (item instanceof JSONObject) {
                    valuesList.add(((JSONObject) item).optString(KEY));
                } else if (item instanceof String) {
                    valuesList.add((String) item);
                }
            }

            if (!isFemaleAndAtLeast12()) {
                for (int i = valuesList.size() - 1; i >= 0; i--) {
                    String selectedKey = valuesList.get(i);
                    if (FAMILY_PLANNING_PILLS.equals(selectedKey)
                            || IRON_FOLIC_TABLETS.equals(selectedKey)) {
                        valuesList.remove(i);
                    }
                }
            }

            if (isAgeBelow18()) {
                for (int i = valuesList.size() - 1; i >= 0; i--) {
                    if (HIV_SELF_TEST_KITS.equals(valuesList.get(i))) {
                        valuesList.remove(i);
                    }
                }
            }

            preventiveServices.put(JsonFormConstants.VALUE, valuesList.toString());
            return jsonObject.toString();
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseHpsVisitAction.Status evaluateStatusOnPayload() {

        if (StringUtils.isNotBlank(preventiveServicesProvided)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
