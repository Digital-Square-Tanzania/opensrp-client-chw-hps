package org.smartregister.chw.hps.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.hps.HpsLibrary;
import org.smartregister.chw.hps.R;
import org.smartregister.chw.hps.adapter.BaseHpsVisitAdapter;
import org.smartregister.chw.hps.contract.BaseHpsVisitContract;
import org.smartregister.chw.hps.dao.HpsDao;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.interactor.BaseHpsVisitInteractor;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.presenter.BaseHpsVisitPresenter;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.view.activity.SecuredActivity;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

public class BaseHpsVisitActivity extends SecuredActivity implements BaseHpsVisitContract.View, View.OnClickListener {

    private static final String TAG = BaseHpsVisitActivity.class.getCanonicalName();
    protected static String profileType;
    protected Map<String, BaseHpsVisitAction> actionList = new LinkedHashMap<>();
    protected BaseHpsVisitContract.Presenter presenter;
    protected MemberObject memberObject;
    protected String baseEntityID;
    protected Boolean isEditMode = false;
    protected RecyclerView.Adapter mAdapter;
    protected ProgressBar progressBar;
    protected TextView tvSubmit;
    protected TextView tvTitle;
    protected String current_action;
    protected String confirmCloseTitle;
    protected String confirmCloseMessage;

    public static void startMe(Activity activity, String baseEntityID, Boolean isEditMode) {
        Intent intent = new Intent(activity, BaseHpsVisitActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.EDIT_MODE, isEditMode);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE, profileType);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_hps_visit_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            memberObject = (MemberObject) getIntent().getSerializableExtra(Constants.ACTIVITY_PAYLOAD.MEMBER_PROFILE_OBJECT);
            isEditMode = getIntent().getBooleanExtra(Constants.ACTIVITY_PAYLOAD.EDIT_MODE, false);
            baseEntityID = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID);
            profileType = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE);
            memberObject = getMemberObject(baseEntityID);

        }

        confirmCloseTitle = getString(R.string.confirm_form_close);
        confirmCloseMessage = getString(R.string.confirm_form_close_explanation);
        setUpView();
        displayProgressBar(true);
        registerPresenter();
        if (presenter != null) {
            if (StringUtils.isNotBlank(baseEntityID)) {
                presenter.reloadMemberDetails(baseEntityID, profileType);
            } else {
                presenter.initialize();
            }
        }
    }

    protected MemberObject getMemberObject(String baseEntityId) {
        return HpsDao.getMember(baseEntityId);
    }

    public void setUpView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.close).setOnClickListener(this);
        tvSubmit = findViewById(R.id.customFontTextViewSubmit);
        tvSubmit.setOnClickListener(this);
        tvTitle = findViewById(R.id.customFontTextViewName);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new BaseHpsVisitAdapter(this, this, (LinkedHashMap) actionList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        redrawVisitUI();
    }

    protected void registerPresenter() {
        presenter = new BaseHpsVisitPresenter(memberObject, this, new BaseHpsVisitInteractor());
    }


    @Override
    public void initializeActions(LinkedHashMap<String, BaseHpsVisitAction> map) {
        //Clearing the action List before recreation
          actionList.clear();

        //Necessary evil to rearrange the actions according to a specific arrangement
        if (map.containsKey(getString(R.string.client_criteria))){
          BaseHpsVisitAction clientCriteriaAction = map.get(getString(R.string.client_criteria));
          actionList.put(getString(R.string.client_criteria), clientCriteriaAction);
        }
        if (map.containsKey(getString(R.string.hps_education_on_behavioural_change))){
            BaseHpsVisitAction educationOnBehaviouralChangeAction = map.get(getString(R.string.hps_education_on_behavioural_change));
            actionList.put(getString(R.string.hps_education_on_behavioural_change), educationOnBehaviouralChangeAction);
        }
        if (map.containsKey(getString(R.string.hps_preventive_services))){
            BaseHpsVisitAction preventiveServicesAction = map.get(getString(R.string.hps_preventive_services));
            actionList.put(getString(R.string.hps_preventive_services), preventiveServicesAction);
        }
        if (map.containsKey(getString(R.string.hps_disease_signs))) {
            BaseHpsVisitAction diseaseSigns = map.get(getString(R.string.hps_disease_signs));
            actionList.put(getString(R.string.hps_disease_signs), diseaseSigns);
        }
        if (map.containsKey(getString(R.string.hps_curative_services))){
            BaseHpsVisitAction investigativeServicesAction = map.get(getString(R.string.hps_curative_services));
            actionList.put(getString(R.string.hps_curative_services), investigativeServicesAction);
        }
        if (map.containsKey(getString(R.string.hps_referral_services))){
            BaseHpsVisitAction referralServicesAction = map.get(getString(R.string.hps_referral_services));
            actionList.put(getString(R.string.hps_referral_services), referralServicesAction);
        }
        if (map.containsKey(getString(R.string.hps_remarks))){
            BaseHpsVisitAction remarksAction = map.get(getString(R.string.hps_remarks));
            actionList.put(getString(R.string.hps_remarks), remarksAction);
        }
        //====================End of Necessary evil ====================================

//        actionList.putAll(map);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        displayProgressBar(false);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void displayToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Boolean getEditMode() {
        return isEditMode;
    }

    @Override
    public void onMemberDetailsReloaded(MemberObject memberObject) {
        this.memberObject = memberObject;
        presenter.initialize();
        redrawHeader(memberObject);
    }

    @Override
    protected void onCreation() {
        Timber.v("Empty onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("Empty onResumption");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close) {
            displayExitDialog(() -> close());
        } else if (v.getId() == R.id.customFontTextViewSubmit) {
            submitVisit();
        }
    }

    @Override
    public BaseHpsVisitContract.Presenter presenter() {
        return presenter;
    }

    @Override
    public Form getFormConfig() {
        return null;
    }

    @Override
    public void startForm(BaseHpsVisitAction hpsHomeVisitAction) {
        current_action = hpsHomeVisitAction.getTitle();

        if (StringUtils.isNotBlank(hpsHomeVisitAction.getJsonPayload())) {
            try {
                JSONObject jsonObject = new JSONObject(hpsHomeVisitAction.getJsonPayload());
                startFormActivity(jsonObject);
            } catch (Exception e) {
                Timber.e(e);
                String locationId = HpsLibrary.getInstance().context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(hpsHomeVisitAction.getFormName(), memberObject.getBaseEntityId(), locationId);
            }
        } else {
            String locationId = HpsLibrary.getInstance().context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            presenter().startForm(hpsHomeVisitAction.getFormName(), memberObject.getBaseEntityId(), locationId);
        }
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, JsonFormActivity.class);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        if (getFormConfig() != null) {
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getFormConfig());
        }

        startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void startFragment(BaseHpsVisitAction hpsHomeVisitAction) {
        current_action = hpsHomeVisitAction.getTitle();

        if (hpsHomeVisitAction.getDestinationFragment() != null)
            hpsHomeVisitAction.getDestinationFragment().show(getSupportFragmentManager(), current_action);
    }

    @Override
    public void redrawHeader(MemberObject memberObject) {
        int age = memberObject.getAge();
        tvTitle.setText(MessageFormat.format("{0}, {1}",
                memberObject.getFullName(),
                String.valueOf(age)));
    }

    @Override
    public void redrawVisitUI() {
        boolean valid = !actionList.isEmpty();
        for (Map.Entry<String, BaseHpsVisitAction> entry : actionList.entrySet()) {
            BaseHpsVisitAction action = entry.getValue();
            if (
                    (!action.isOptional() && (action.getActionStatus() == BaseHpsVisitAction.Status.PENDING && action.isValid()))
                            || !action.isEnabled()
            ) {
                valid = false;
                break;
            }
        }

        int res_color = valid ? R.color.white : R.color.light_grey;
        tvSubmit.setTextColor(getResources().getColor(res_color));
        tvSubmit.setOnClickListener(valid ? this : null); // update listener to null

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void displayProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public Map<String, BaseHpsVisitAction> getHpsVisitActions() {
        return actionList;
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void submittedAndClose(String results) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        close();
    }

    @Override
    public BaseHpsVisitContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void submitVisit() {
        getPresenter().submitVisit();
    }

    @Override
    public void onDialogOptionUpdated(String jsonString) {
        BaseHpsVisitAction hpsVisitAction = actionList.get(current_action);
        if (hpsVisitAction != null) {
            hpsVisitAction.setJsonPayload(jsonString);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            redrawVisitUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_GET_JSON) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                    BaseHpsVisitAction baseHpsVisitAction = actionList.get(current_action);
                    if (baseHpsVisitAction != null) {
                        baseHpsVisitAction.setJsonPayload(jsonString);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {

                BaseHpsVisitAction baseHpsVisitAction = actionList.get(current_action);
                if (baseHpsVisitAction != null)
                    baseHpsVisitAction.evaluateStatus();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        // update the adapter after every payload
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            redrawVisitUI();
        }
    }

    @Override
    public Context getMyContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        displayExitDialog(BaseHpsVisitActivity.this::finish);
    }

    protected void displayExitDialog(final Runnable onConfirm) {
        AlertDialog dialog = new AlertDialog.Builder(this, com.vijay.jsonwizard.R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                .setMessage(confirmCloseMessage).setNegativeButton(com.vijay.jsonwizard.R.string.yes, (dialog1, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                }).setPositiveButton(com.vijay.jsonwizard.R.string.no, (dialog2, which) -> Timber.d("No button on dialog in %s", JsonFormActivity.class.getCanonicalName())).create();

        dialog.show();
    }
}
