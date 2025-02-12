package org.smartregister.chw.hps_sample.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.hps.contract.HpsRegisterFragmentContract;
import org.smartregister.chw.hps.presenter.BaseHpsRegisterFragmentPresenter;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.chw.hps.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseHpsRegisterFragmentPresenterTest {
    @Mock
    protected HpsRegisterFragmentContract.View view;

    @Mock
    protected HpsRegisterFragmentContract.Model model;

    private BaseHpsRegisterFragmentPresenter baseHpsRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseHpsRegisterFragmentPresenter = new BaseHpsRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseHpsRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals(" ec_hps_enrollment.is_closed = 0 ", baseHpsRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(hps_test_date,7,4)|| '-' || SUBSTR(hps_test_date,4,2) || '-' || SUBSTR(hps_test_date,1,2),'')) as integer) between 7 and 14) ", baseHpsRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(Constants.TABLES.HPS_ENROLLMENT + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseHpsRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(Constants.TABLES.HPS_ENROLLMENT, baseHpsRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseHpsRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams(Constants.TABLES.HPS_ENROLLMENT, null, null);
        Mockito.verify(view).initializeQueryParams(Constants.TABLES.HPS_ENROLLMENT, null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}