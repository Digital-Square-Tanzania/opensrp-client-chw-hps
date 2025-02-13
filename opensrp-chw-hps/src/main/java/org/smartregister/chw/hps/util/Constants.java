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
        String HPS_OTHER_SERVICES = "hps_other_services";
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

    interface VALUES {
        String NONE = "none";
        String CHORDAE = "chordae";
        String HIV = "hiv";
        String RBG = "random_blood_glucose_test";
        String FBG = "fast_blood_glucose_test";
        String HYPERTENSION = "hypertension";
        String SILICON_OR_LEXAN = "silicon_or_lexan";
        String NEGATIVE = "negative";
        String SATISFACTORY = "satisfactory";
        String NEEDS_FOLLOWUP = "needs_followup";
        String YES = "yes";
    }

    interface TABLE_COLUMN {
        String GENITAL_EXAMINATION = "genital_examination";
        String SYSTOLIC = "systolic";
        String DIASTOLIC = "diastolic";
        String ANY_COMPLAINTS = "any_complaints";
        String CLIENT_DIAGNONISED_WITH = "is_client_diagnosed_with_any";
        String COMPLICATION_TYPE = "type_complication";
        String HEMATOLOGICAL_DISEASE_SYMPTOMS = "any_hematological_disease_symptoms";
        String KNOWN_ALLEGIES = "known_allergies";
        String HIV_RESULTS = "hiv_result";
        String HIV_VIRAL_LOAD = "hiv_viral_load_text";
        String TYPE_OF_BLOOD_FOR_GLUCOSE_TEST = "type_of_blood_for_glucose_test";
        String BLOOD_FOR_GLUCOSE = "blood_for_glucose";
        String DISCHARGE_CONDITION = "discharge_condition";
        String IS_MALE_PROCEDURE_CIRCUMCISION_CONDUCTED = "is_male_procedure_circumcision_conducted";
    }

}
