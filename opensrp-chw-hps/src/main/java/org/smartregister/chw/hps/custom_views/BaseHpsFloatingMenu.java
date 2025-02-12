package org.smartregister.chw.hps.custom_views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.fragment.BaseHpsCallDialogFragment;
import org.smartregister.chw.hps.R;

public class BaseHpsFloatingMenu extends LinearLayout implements View.OnClickListener {
    private MemberObject MEMBER_OBJECT;

    public BaseHpsFloatingMenu(Context context, MemberObject MEMBER_OBJECT) {
        super(context);
        initUi();
        this.MEMBER_OBJECT = MEMBER_OBJECT;
    }

    protected void initUi() {
        inflate(getContext(), R.layout.view_hps_floating_menu, this);
        FloatingActionButton fab = findViewById(R.id.hps_fab);
        if (fab != null)
            fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.hps_fab) {
            Activity activity = (Activity) getContext();
            BaseHpsCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }  else if (view.getId() == R.id.refer_to_facility_layout) {
            Activity activity = (Activity) getContext();
            BaseHpsCallDialogFragment.launchDialog(activity, MEMBER_OBJECT);
        }
    }
}