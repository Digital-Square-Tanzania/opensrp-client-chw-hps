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
        String HPS_CLIENT_ENROLLMENT = "HPS Client Enrollment";
        String HPS_HOUSEHOLD_ENROLLMENT = "HPS Household Enrollment";
        String HPS_CLIENT_FOLLOW_UP_VISIT = "HPS Client Follow-up Visit";
        String HPS_HOUSEHOLD_VISIT = "HPS Household Services";
        String VOID_EVENT = "Void Event";
        String CLOSE_HPS_SERVICE = "Close Hps Service";
        String HPS_MOBILIZATION = "HPS Mobilization";
        String HPS_DEATH_REGISTRATION = "HPS Death Registration";
        String HPS_ANNUAL_CENSUS = "HPS Annual Census";

    }

    interface FORMS {
        String HPS_CLIENT_ENROLLMENT = "hps_individual_enrollment";
        String HPS_HOUSEHOLD_ENROLLMENT = "hps_household_enrollment";
        String HPS_FOLLOW_UP_VISIT = "hps_followup_visit";
        String HPS_HOUSEHOLD_VISIT = "hps_household_services";
        String HPS_DEATH_REGISTRATION = "hps_death_registration";
        String HPS_MOBILIZATION = "hps_mobilization";
        String HPS_ANNUAL_CENSUS = "hps_annual_census";
    }

    interface HPS_FOLLOWUP_FORMS {
        String CLIENT_CRITERIA = "hps_client_criteria";
        String EDUCATION_ON_BEHAVIOURAL_CHANGE = "hps_education_on_behavioural_change";
        String HPS_PREVENTIVE_SERVICES = "hps_preventive_services";
        String HPS_CURATIVE_SERVICES = "hps_curative_services";
        String HPS_REFERRAL_SERVICES = "hps_referral_services";
    }

    interface TABLES {
        String HPS_CLIENT_REGISTER = "ec_hps_client_register";
        String HPS_HOUSEHOLD_REGISTER = "ec_hps_household_register";
        String HPS_CLIENT_SERVICES = "ec_hps_client_services";
        String HPS_HOUSEHOLD_SERVICES = "ec_hps_household_services";
        String HPS_MOBILIZATION_SESSIONS = "ec_hps_mobilization";
        String HPS_DEATH_REGISTER = "ec_hps_death_register";
        String HPS_ANNUAL_CENSUS_REGISTER = "ec_hps_annual_census_register";
    }

    interface ACTIVITY_PAYLOAD {
        String BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String FAMILY_BASE_ENTITY_ID = "FAMILY_BASE_ENTITY_ID";
        String HPS_FORM_NAME = "HPS_FORM_NAME";
        String MEMBER_PROFILE_OBJECT = "MemberObject";
        String EDIT_MODE = "editMode";
        String PROFILE_TYPE = "profile_type";
        String ACTION = "ACTION";
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
