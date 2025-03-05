package org.smartregister.chw.hps.listener;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.chw.hps.R;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class HpsBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public HpsBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        super.onNavigationItemSelected(item);

        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == R.id.action_home) {
            baseRegisterActivity.switchToBaseFragment();
        } else if (item.getItemId() == R.id.action_household_services) {
            baseRegisterActivity.switchToFragment(1);
        } else if (item.getItemId() == R.id.action_mobilization_session) {
            baseRegisterActivity.switchToFragment(2);
        } else if (item.getItemId() == R.id.action_death_register) {
            baseRegisterActivity.switchToFragment(3);
        } else if (item.getItemId() == R.id.action_annual_census_register) {
            baseRegisterActivity.switchToFragment(4);
        }

        return true;
    }
}