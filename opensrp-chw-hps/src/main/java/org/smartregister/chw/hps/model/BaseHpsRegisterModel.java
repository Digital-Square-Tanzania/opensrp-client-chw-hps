package org.smartregister.chw.hps.model;

import org.json.JSONObject;
import org.smartregister.chw.hps.contract.HpsRegisterContract;
import org.smartregister.chw.hps.util.HpsJsonFormUtils;

public class BaseHpsRegisterModel implements HpsRegisterContract.Model {

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject jsonObject = HpsJsonFormUtils.getFormAsJson(formName);
        HpsJsonFormUtils.getRegistrationForm(jsonObject, entityId, currentLocationId);

        return jsonObject;
    }

}
