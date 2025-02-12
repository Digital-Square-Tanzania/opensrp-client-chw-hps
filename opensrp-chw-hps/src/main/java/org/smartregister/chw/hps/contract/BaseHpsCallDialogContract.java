package org.smartregister.chw.hps.contract;

import android.content.Context;

public interface BaseHpsCallDialogContract {

    interface View {
        void setPendingCallRequest(Dialer dialer);
        Context getCurrentContext();
    }

    interface Dialer {
        void callMe();
    }
}
