package org.smartregister.chw.hps_sample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.vijay.jsonwizard.activities.JsonWizardFormActivity;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.factory.FileSourceFactoryHelper;


import org.json.JSONObject;
import org.smartregister.chw.hps.activity.BaseHpsProfileActivity;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.Visit;
import org.smartregister.chw.hps.util.Constants;


import timber.log.Timber;


public class HpsMemberProfileActivity extends BaseHpsProfileActivity {
    private Visit enrollmentVisit = null;
    private Visit serviceVisit = null;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, HpsMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivity(intent);
    }

    @Override
    protected MemberObject getMemberObject(String baseEntityId) {
        return EntryActivity.getSampleMember();
    }

    @Override
    public void openFollowupVisit() {
        try {
            HpsServiceActivity.startHpsVisitActivity(this, "12345", false);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void startForm(String formName) throws Exception {
        JSONObject jsonForm = FileSourceFactoryHelper.getFileSource("").getFormFromFile(getApplicationContext(), formName);

        String currentLocationId = "Tanzania";
        if (jsonForm != null) {
            jsonForm.getJSONObject("metadata").put("encounter_location", currentLocationId);
            Intent intent = new Intent(this, JsonWizardFormActivity.class);
            intent.putExtra("json", jsonForm.toString());

            Form form = new Form();
            form.setWizard(true);
            form.setNextLabel("Next");
            form.setPreviousLabel("Previous");
            form.setSaveLabel("Save");
            form.setHideSaveLabel(true);

            intent.putExtra("form", form);
            startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);

        }

    }




//    public void startFormActivity(JSONObject jsonForm) {
//        Intent intent = org.smartregister.chw.core.utils.Utils.formActivityIntent(this, jsonForm.toString());
//        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
//    }

    @Override
    public void startServiceForm() {
        HpsServiceActivity.startHpsVisitActivity(this, memberObject.getBaseEntityId(), false);
    }


    @Override
    public void continueService() {

    }

    @Override
    protected Visit getServiceVisit() {
        return serviceVisit;
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        delayRefreshSetupViews();
    }


    private void delayRefreshSetupViews() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::setupViews, 300);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(Constants.JSON_FORM_EXTRA.EVENT_TYPE);
                switch (encounterType) {
                    case Constants.EVENT_TYPE.HPS_SERVICES:
                        serviceVisit = new Visit();
                        serviceVisit.setProcessed(true);
                        serviceVisit.setJson(jsonString);
                        break;

                }
            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }
}
