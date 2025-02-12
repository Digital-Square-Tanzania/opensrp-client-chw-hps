package org.smartregister.chw.hps.presenter;

import org.json.JSONObject;
import org.smartregister.chw.hps.contract.BaseHpsVisitContract;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.HpsJsonFormUtils;
import org.smartregister.util.FormUtils;
import org.smartregister.chw.hps.R;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import timber.log.Timber;

public class BaseHpsVisitPresenter implements BaseHpsVisitContract.Presenter, BaseHpsVisitContract.InteractorCallBack {

    protected WeakReference<BaseHpsVisitContract.View> view;
    protected BaseHpsVisitContract.Interactor interactor;
    protected MemberObject memberObject;

    public BaseHpsVisitPresenter(MemberObject memberObject, BaseHpsVisitContract.View view, BaseHpsVisitContract.Interactor interactor) {
        this.view = new WeakReference<>(view);
        this.interactor = interactor;
        this.memberObject = memberObject;
    }

    @Override
    public void startForm(String formName, String memberID, String currentLocationId) {
        try {
            if (view.get() != null) {
                JSONObject jsonObject = FormUtils.getInstance(view.get().getContext()).getFormJson(formName);
                HpsJsonFormUtils.getRegistrationForm(jsonObject, memberID, currentLocationId);
                view.get().startFormActivity(jsonObject);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public boolean validateStatus() {
        return false;
    }

    @Override
    public void initialize() {
        view.get().displayProgressBar(true);
        view.get().redrawHeader(memberObject);
        interactor.calculateActions(view.get(), memberObject, this);
    }

    @Override
    public void submitVisit() {
        if (view.get() != null) {
            view.get().displayProgressBar(true);
            interactor.submitVisit(view.get().getEditMode(), memberObject.getBaseEntityId(), view.get().getHpsVisitActions(), this);
        }
    }

    @Override
    public void reloadMemberDetails(String memberID, String profileType) {
        view.get().displayProgressBar(true);
        interactor.reloadMemberDetails(memberID, profileType, this);
    }

    @Override
    public void onMemberDetailsReloaded(MemberObject memberObject) {
        if (view.get() != null) {
            this.memberObject = memberObject;

            view.get().displayProgressBar(false);
            view.get().onMemberDetailsReloaded(memberObject);
        }
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        Timber.v("onRegistrationSaved");
    }

    @Override
    public void preloadActions(LinkedHashMap<String, BaseHpsVisitAction> map) {
        if (view.get() != null)
            view.get().initializeActions(map);
    }

    @Override
    public void onSubmitted(String results) {
        if (view.get() != null) {
            view.get().displayProgressBar(false);
            if (results != null) {
                view.get().submittedAndClose(results);
            } else {
                view.get().displayToast(view.get().getContext().getString(R.string.error_unable_save_visit));
            }
        }
    }
}
