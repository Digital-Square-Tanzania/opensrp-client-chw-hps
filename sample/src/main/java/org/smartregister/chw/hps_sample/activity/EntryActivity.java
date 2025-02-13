package org.smartregister.chw.hps_sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import org.smartregister.chw.hps.contract.BaseHpsVisitContract;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.util.DBConstants;
import org.smartregister.chw.hps_sample.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.SecuredActivity;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class EntryActivity extends SecuredActivity implements View.OnClickListener, BaseHpsVisitContract.VisitView {
    private static MemberObject hpsMemberObject;

    public static MemberObject getSampleMember() {
        if (hpsMemberObject == null) {
            hpsMemberObject = new MemberObject();
            hpsMemberObject.setFirstName("Glory");
            hpsMemberObject.setLastName("Juma");
            hpsMemberObject.setMiddleName("Ali");
            hpsMemberObject.setGender("Female");
            hpsMemberObject.setMartialStatus("Married");
            hpsMemberObject.setDob("1982-01-18T03:00:00.000+03:00");
            hpsMemberObject.setAddress("Mwembweni");
            hpsMemberObject.setUniqueId("3503504");
            hpsMemberObject.setBaseEntityId("3503504");
            hpsMemberObject.setFamilyBaseEntityId("3503504");
        }

        return hpsMemberObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.hps_activity).setOnClickListener(this);
        findViewById(R.id.hps_home_visit).setOnClickListener(this);
        findViewById(R.id.hps_profile).setOnClickListener(this);
    }

    @Override
    protected void onCreation() {
        Timber.v("onCreation");
    }

    @Override
    protected void onResumption() {
        Timber.v("onCreation");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hps_activity:
                startActivity(new Intent(this, HpsRegisterActivity.class));
                break;
            case R.id.hps_home_visit:
                HpsServiceActivity.startHpsVisitActivity(this, "12345", false);
                break;
            case R.id.hps_profile:
                HpsMemberProfileActivity.startMe(this, "12345");
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogOptionUpdated(String jsonString) {
        Timber.v("onDialogOptionUpdated %s", jsonString);
    }

    @Override
    public Context getMyContext() {
        return this;
    }
}