package org.smartregister.chw.hps_sample.interactor;

import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.interactor.BaseHpsServiceVisitInteractor;
import org.smartregister.chw.hps_sample.activity.EntryActivity;


public class HpsServiceVisitInteractor extends BaseHpsServiceVisitInteractor {
    public HpsServiceVisitInteractor(String visitType) {
        super(visitType);
    }

    @Override
    public MemberObject getMemberClient(String memberID, String profileType) {
        return EntryActivity.getSampleMember();
    }
}
