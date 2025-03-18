package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static com.vijay.jsonwizard.constants.JsonFormConstants.VALUE;
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

    protected String ecMaterialsDistributed;

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
            ecMaterialsDistributed = JsonFormUtils.getValue(jsonObject, "provide_iec_materials");
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
                JSONObject obj = valuesArray.getJSONObject(i);
                valuesList.add(obj.getString(KEY));
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

        if (StringUtils.isNotBlank(ecMaterialsDistributed)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }
}
