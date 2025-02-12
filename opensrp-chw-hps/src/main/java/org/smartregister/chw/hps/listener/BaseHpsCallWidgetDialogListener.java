package org.smartregister.chw.hps.listener;


import android.view.View;

import org.smartregister.chw.hps.fragment.BaseHpsCallDialogFragment;
import org.smartregister.chw.hps.R;

public class BaseHpsCallWidgetDialogListener implements View.OnClickListener {

    private BaseHpsCallDialogFragment callDialogFragment;

    public BaseHpsCallWidgetDialogListener(BaseHpsCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.hps_call_close) {
            callDialogFragment.dismiss();
        }
    }
}
