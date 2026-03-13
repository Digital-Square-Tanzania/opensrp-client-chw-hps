package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
import static org.smartregister.AllConstants.OPTIONS;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class HpsPreventiveServicesActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {

    private static final String GLOBAL = "global";
    private static final String AGE = "age";
    private static final String GENDER = "gender";
    private static final String PREVENTIVE_SERVICES = "preventive_services";
    private static final String FEMALE = "female";
    private static final String FAMILY_PLANNING_PILLS = "family_planning_pills";
    private static final String IRON_FOLIC_TABLETS = "iron_folic_tablets";

    private static final Set<String> PREVENTIVE_SERVICES_UNDER_5 = new HashSet<>(Arrays.asList(
            "vitamin_a_supplements",
            "other_services"
    ));

    private static final Set<String> PREVENTIVE_SERVICES_5_TO_17 = new HashSet<>(Arrays.asList(
            "counselling",
            "family_planning_pills",
            "iron_folic_tablets",
            "deworming_medication",
            "male_condoms",
            "female_condoms",
            "post_exposure_prophylaxis",
            "pre_exposure_prophylaxis",
            "fortified_foods",
            "distribution_mosquito_nets",
            "distribution_water_purification_tablets",
            "psychological_counselling",
            "preliminary_screening_tb",
            "gender_based_violence_screening",
            "collection_sputum_stool_samples",
            "dispensing_arv_medication",
            "preventive_medication_ntd",
            "first_aid_services",
            "environment_hygiene_sanitation",
            "food_hygiene_safety",
            "other_services"
    ));

    private static final Set<String> PREVENTIVE_SERVICES_18_AND_ABOVE = new HashSet<>(Arrays.asList(
            "counselling",
            "family_planning_pills",
            "iron_folic_tablets",
            "deworming_medication",
            "male_condoms",
            "female_condoms",
            "post_exposure_prophylaxis",
            "pre_exposure_prophylaxis",
            "fortified_foods",
            "distribution_mosquito_nets",
            "hiv_self_test_kits",
            "distribution_water_purification_tablets",
            "psychological_counselling",
            "preliminary_screening_tb",
            "gender_based_violence_screening",
            "collection_sputum_stool_samples",
            "dispensing_arv_medication",
            "preventive_medication_ntd",
            "first_aid_services",
            "environment_hygiene_sanitation",
            "food_hygiene_safety",
            "other_services"
    ));

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
                JSONObject global = jsonObject.optJSONObject(GLOBAL);
                if (global != null) {
                    global.put(AGE, memberObject.getAge());
                    global.put(GENDER, memberObject.getGender());
                }

                filterFieldOptionsByAllowedKeys(getFieldsArray(jsonObject), PREVENTIVE_SERVICES,
                        getAllowedPreventiveServiceKeys(memberObject));
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
            return null;
        }
    }

    private JSONArray getFieldsArray(JSONObject jsonObject) {
        JSONObject stepOne = jsonObject.optJSONObject(STEP1);
        return stepOne == null ? null : stepOne.optJSONArray(FIELDS);
    }

    private Set<String> getAllowedPreventiveServiceKeys(MemberObject memberObject) {
        if (memberObject == null) {
            return null;
        }

        int age = memberObject.getAge();
        Set<String> allowedKeys;
        if (age < 5) {
            allowedKeys = new HashSet<>(PREVENTIVE_SERVICES_UNDER_5);
        } else if (age < 18) {
            allowedKeys = new HashSet<>(PREVENTIVE_SERVICES_5_TO_17);
        } else {
            allowedKeys = new HashSet<>(PREVENTIVE_SERVICES_18_AND_ABOVE);
        }

        if (!isFemaleAtLeast12(memberObject)) {
            allowedKeys.remove(FAMILY_PLANNING_PILLS);
            allowedKeys.remove(IRON_FOLIC_TABLETS);
        }

        return allowedKeys;
    }

    private boolean isFemaleAtLeast12(MemberObject memberObject) {
        return memberObject != null
                && memberObject.getAge() >= 12
                && FEMALE.equalsIgnoreCase(memberObject.getGender());
    }

    private void filterFieldOptionsByAllowedKeys(JSONArray fieldsArray, String fieldKey, Set<String> allowedKeys) throws JSONException {
        if (fieldsArray == null || allowedKeys == null) {
            return;
        }

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
            if (option == null || !allowedKeys.contains(option.optString(KEY))) {
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
        return null;
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
