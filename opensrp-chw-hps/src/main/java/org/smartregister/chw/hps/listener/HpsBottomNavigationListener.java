package org.smartregister.chw.hps.listener;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.smartregister.chw.hps.R;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class HpsBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    BaseRegisterActivity baseRegisterActivity;

    public HpsBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);

        baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == R.id.action_home) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_household_services) {
            baseRegisterActivity.switchToFragment(1);
        } else if (item.getItemId() == R.id.action_mobilization_session) {
            baseRegisterActivity.switchToFragment(2);
        } else if (item.getItemId() == R.id.action_death_register) {
            baseRegisterActivity.switchToFragment(3);
        } else if (item.getItemId() == R.id.action_more_items) {
            showBottomSheet();
        }

        return true;
    }

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = context.getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // Optionally, you can find views and set listeners inside the bottom sheet:
        bottomSheetView.findViewById(R.id.action_annual_census_register).setOnClickListener(view -> {
            baseRegisterActivity.switchToFragment(4);
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.action_advertisement_feedback).setOnClickListener(view -> {
            baseRegisterActivity.switchToFragment(5);
            bottomSheetDialog.dismiss();
        });
    }
}