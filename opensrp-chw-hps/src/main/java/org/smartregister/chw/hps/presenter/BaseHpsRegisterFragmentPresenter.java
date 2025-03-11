package org.smartregister.chw.hps.presenter;

import static org.apache.commons.lang3.StringUtils.trim;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.hps.contract.HpsRegisterFragmentContract;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.chw.hps.util.DBConstants;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.TreeSet;

public class BaseHpsRegisterFragmentPresenter implements HpsRegisterFragmentContract.Presenter {

    protected WeakReference<HpsRegisterFragmentContract.View> viewReference;

    protected HpsRegisterFragmentContract.Model model;

    protected RegisterConfiguration config;

    protected Set<View> visibleColumns = new TreeSet<>();
    protected String viewConfigurationIdentifier;

    public BaseHpsRegisterFragmentPresenter(HpsRegisterFragmentContract.View view, HpsRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public String getMainCondition() {
        return " "+getMainTable()+".is_closed = 0 AND does_the_client_consent_to_be_enrolled_in_hps_services = 'yes' ";
    }

    @Override
    public String getDefaultSortQuery() {
        return getMainTable() + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            this.visibleColumns = model.getRegisterActiveColumns(viewConfigurationIdentifier);
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = getMainTable();
        mainCondition = trim(getMainCondition()).equals("") ? mainCondition : getMainCondition();
        String countSelect = model.countSelect(tableName, mainCondition);
        String mainSelect = model.mainSelect(tableName, mainCondition);

        if (getView() != null) {

            getView().initializeQueryParams(tableName, countSelect, mainSelect);
            getView().initializeAdapter(visibleColumns);

            getView().countExecute();
            getView().filterandSortInInitializeQueries();
        }
    }

    protected HpsRegisterFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void startSync() {
//        implement

    }

    @Override
    public void searchGlobally(String s) {
//        implement

    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.HPS_CLIENT_REGISTER;
    }

    @Override
    public String getDueFilterCondition() {
        return " (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(hps_test_date,7,4)|| '-' || SUBSTR(hps_test_date,4,2) || '-' || SUBSTR(hps_test_date,1,2),'')) as integer) between 7 and 14) ";
    }
}
