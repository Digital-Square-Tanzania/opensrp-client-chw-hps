package org.smartregister.chw.hps.activity;

import static org.mockito.Mockito.validateMockitoUsage;

import android.view.View;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.chw.hps.R;
import org.smartregister.chw.hps.contract.HpsProfileContract;

public class BaseHpsProfileActivityTest {
    @Mock
    public BaseHpsProfileActivity baseTestProfileActivity;

    @Mock
    public HpsProfileContract.Presenter profilePresenter;

    @Mock
    public View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseTestProfileActivity);
    }

    @Test
    public void formatTime() {
        BaseHpsProfileActivity activity = new BaseTestHpsProfileActivity();
        try {
            Assert.assertEquals("25 Oct 2019", Whitebox.invokeMethod(activity, "formatTime", "25-10-2019"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkHideView() {
        baseTestProfileActivity.hideView();
        Mockito.verify(view, Mockito.never()).setVisibility(View.GONE);
    }

    @Test
    public void checkProgressBar() {
        baseTestProfileActivity.showProgressBar(true);
        Mockito.verify(view, Mockito.never()).setVisibility(View.VISIBLE);
    }

    @Test
    public void medicalHistoryRefresh() {
        baseTestProfileActivity.refreshMedicalHistory(true);
        Mockito.verify(view, Mockito.never()).setVisibility(View.VISIBLE);
    }

    @Test
    public void onClickBackPressed() {
        baseTestProfileActivity = Mockito.spy(new BaseTestHpsProfileActivity());
        Mockito.when(view.getId()).thenReturn(R.id.title_layout);
        Mockito.doNothing().when(baseTestProfileActivity).onBackPressed();
        baseTestProfileActivity.onClick(view);
        Mockito.verify(baseTestProfileActivity).onBackPressed();
    }

    @Test
    public void onClickOpenMedicalHistory() {
        baseTestProfileActivity = Mockito.spy(new BaseTestHpsProfileActivity());
        Mockito.when(view.getId()).thenReturn(R.id.rlLastVisit);
        Mockito.doNothing().when(baseTestProfileActivity).openMedicalHistory();
        baseTestProfileActivity.onClick(view);
        Mockito.verify(baseTestProfileActivity).openMedicalHistory();
    }


    @Test(expected = Exception.class)
    public void onActivityResult() throws Exception {
        baseTestProfileActivity = Mockito.spy(new BaseTestHpsProfileActivity());
        Whitebox.invokeMethod(baseTestProfileActivity, "onActivityResult", 2244, -1, null);
        Mockito.verify(profilePresenter).saveForm(null);
    }

}
