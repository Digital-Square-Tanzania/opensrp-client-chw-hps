package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.EDITABLE;
import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.READ_ONLY;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;
import static org.smartregister.client.utils.constants.JsonFormConstants.VALUE;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hps.dao.HpsDao;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * This class provide a helper that process client criteria
 */
public class HpsClientCriteriaActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {

    protected static String clientCriteria;
    protected String jsonPayload;
    protected String baseEntityId;
    protected Context context;
    protected MemberObject memberObject;

    /**
     * Constructor class
     * @param context the context of application
     * @param memberObject the member object
     */
    public HpsClientCriteriaActionHelper(Context context, MemberObject memberObject) {
        this.context = context;
        this.memberObject = memberObject;
    }

    /**
     * This method will modify the json form after being loaded
     * @param jsonPayload the json form
     * @param context the app context
     * @param map the list of visit details
     */
    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
    }

    /**
     *
     * @return String
     */
    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);

            //Handle Visit Type
            String visitTypeValue = HpsDao.individualClientHasAnyVisit(memberObject.getBaseEntityId()) ? "return_visit" : "new_visit";
            //Client has a visit, set visit type
            JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);
            JSONObject visitType = JsonFormUtils.getFieldJSONObject(fieldsArray,"visit_type");
            visitType.put(VALUE,visitTypeValue);
            visitType.put(READ_ONLY,true);
            visitType.put(EDITABLE, false);
            jsonPayload = jsonObject.toString();


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
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        //overridden
    }

    /**
     *  This method will set client criteria
     * @param jsonPayload the json form
     */
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
    public String getPreProcessedSubTitle() {
        return null;
    }

    /**
     * This method return the scheduled status for the client
     * @return BaseHpsVisitAction.ScheduleStatus
     */
    @Override
    public BaseHpsVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    /**
     * This method will return the string payload after being post processed
     * @param jsonPayload the json form
     * @return String
     */
    @Override
    public String postProcess(String jsonPayload) {
        return null;
    }

    /**
     * This method returns the evaluated subtitle
     * @return String
     */
    @Override
    public String evaluateSubTitle() {
        return null;
    }

    /**
     * This method evaluate the status on the json form payload
     * @return BaseHpsVisitAction.Status
     */
    @Override
    public BaseHpsVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isNotBlank(clientCriteria)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }
}
