package org.smartregister.chw.hps_sample.presenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.chw.hps.contract.HpsProfileContract;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.presenter.BaseHpsProfilePresenter;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BaseHpsProfilePresenterTest {

    @Mock
    private HpsProfileContract.View view = Mockito.mock(HpsProfileContract.View.class);

    @Mock
    private HpsProfileContract.Interactor interactor = Mockito.mock(HpsProfileContract.Interactor.class);

    @Mock
    private MemberObject hpsMemberObject = new MemberObject();

    private BaseHpsProfilePresenter profilePresenter = new BaseHpsProfilePresenter(view, interactor, hpsMemberObject);


    @Test
    public void fillProfileDataCallsSetProfileViewWithDataWhenPassedMemberObject() {
        profilePresenter.fillProfileData(hpsMemberObject);
        verify(view).setProfileViewWithData();
    }

    @Test
    public void fillProfileDataDoesntCallsSetProfileViewWithDataIfMemberObjectEmpty() {
        profilePresenter.fillProfileData(null);
        verify(view, never()).setProfileViewWithData();
    }

    @Test
    public void malariaTestDatePeriodIsLessThanSeven() {
        profilePresenter.recordHpsButton("");
        verify(view).hideView();
    }

    @Test
    public void malariaTestDatePeriodIsMoreThanFourteen() {
        profilePresenter.recordHpsButton("EXPIRED");
        verify(view).hideView();
    }

    @Test
    public void refreshProfileBottom() {
        profilePresenter.refreshProfileBottom();
        verify(interactor).refreshProfileInfo(hpsMemberObject, profilePresenter.getView());
    }

    @Test
    public void saveForm() {
        profilePresenter.saveForm(null);
        verify(interactor).saveRegistration(null, view);
    }
}
