package org.smartregister.chw.hps.util;

public interface Constants {

    int REQUEST_CODE_GET_JSON = 2244;
    String ENCOUNTER_TYPE = "encounter_type";
    String STEP_ONE = "step1";
    String STEP_TWO = "step2";
    String HPS_VISIT_GROUP = "hps_visit_group";


    interface JSON_FORM_EXTRA {
        String JSON = "json";
        String ENCOUNTER_TYPE = "encounter_type";
        String EVENT_TYPE = "eventType";
    }

    interface EVENT_TYPE {
        String HPS_ENROLLMENT = "Hps Enrollment";
        String HPS_SERVICES = "Hps Services";
        String HPS_FOLLOW_UP_VISIT = "Hps Follow-up Visit";
        String VOID_EVENT = "Void Event";
        String CLOSE_HPS_SERVICE = "Close Hps Service";

    }

    interface FORMS {
        String HPS_REGISTRATION = "hps_enrollment";
        String HPS_FOLLOW_UP_VISIT = "hps_followup_visit";
    }

    interface HPS_FOLLOWUP_FORMS {
        String CLIENT_CRITERIA = "hps_client_criteria";
        String EDUCATION_ON_BEHAVIOURAL_CHANGE = "hps_education_on_behavioural_change";
        String HPS_PREVENTIVE_SERVICES = "hps_preventive_services";
        String HPS_CURATIVE_SERVICES = "hps_curative_services";
        String HPS_REFERRAL_SERVICES = "hps_referral_services";
    }

    interface TABLES {
        String HPS_ENROLLMENT = "ec_hps_enrollment";
        String HPS_SERVICE = "ec_hps_services";
    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String HPS_FORM_NAME = "HPS_FORM_NAME";
        String MEMBER_PROFILE_OBJECT = "MemberObject";
        String EDIT_MODE = "editMode";
        String PROFILE_TYPE = "profile_type";

    }

    interface ACTIVITY_PAYLOAD_TYPE {
        String REGISTRATION = "REGISTRATION";
        String FOLLOW_UP_VISIT = "FOLLOW_UP_VISIT";
    }

    interface CONFIGURATION {
        String HPS_ENROLLMENT = "hps_enrollment";
    }

    interface HPS_MEMBER_OBJECT {
        String MEMBER_OBJECT = "memberObject";
    }

    interface PROFILE_TYPES {
        String HPS_PROFILE = "hps_profile";
    }

}
