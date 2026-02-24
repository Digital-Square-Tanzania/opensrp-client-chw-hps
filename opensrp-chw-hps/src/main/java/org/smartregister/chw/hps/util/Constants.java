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
        String HPS_ADVERTISEMENT_FEEDBACK = "HPS Advertisement Feedback";
    }

    interface FORMS {
        String HPS_CLIENT_ENROLLMENT = "hps_individual_enrollment";
        String HPS_HOUSEHOLD_ENROLLMENT = "hps_household_enrollment";
        String HPS_FOLLOW_UP_VISIT = "hps_followup_visit";
        String HPS_HOUSEHOLD_VISIT = "hps_household_services";
        String HPS_DEATH_REGISTRATION = "hps_death_registration";
        String HPS_MOBILIZATION = "hps_mobilization";
        String HPS_ADVERTISEMENT_FEEDBACK = "hps_advertisement_feedback";
        String HPS_ANNUAL_CENSUS = "hps_annual_census";
    }

    interface HPS_FOLLOWUP_FORMS {
        String CLIENT_CRITERIA = "hps_client_criteria";
        String EDUCATION_ON_BEHAVIOURAL_CHANGE = "hps_education_on_behavioural_change";
        String HPS_PREVENTIVE_SERVICES = "hps_preventive_services";
        String HPS_CURATIVE_SERVICES = "hps_curative_services";
        String HPS_REFERRAL_SERVICES = "hps_referral_services";
        String HPS_REMARKS = "hps_remarks";
        String HPS_DISEASE_SIGNS = "hps_disease_signs";

    }

    interface TABLES {
        String HPS_CLIENT_REGISTER = "ec_hps_client_register";
        String HPS_HOUSEHOLD_REGISTER = "ec_hps_household_register";
        String HPS_CLIENT_SERVICES = "ec_hps_client_services";
        String HPS_HOUSEHOLD_SERVICES = "ec_hps_household_services";
        String HPS_MOBILIZATION_SESSIONS = "ec_hps_mobilization";
        String HPS_ADVERTISEMENT_FEEDBACK = "ec_hps_advertisement_feedback";
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

    interface OPTIONS_FIELDS {
        String PREVENTIVE_SERVICES_ABOVE_5_BELOW_18 = "preventive_services_above_5_years_below_18_years";
        String PREVENTIVE_SERVICES_ABOVE_18 = "preventive_services_above_18_years";
        String FAMILY_PLANNING_PILLS = "family_planning_pills";
        String IRON_FOLIC_TABLETS = "iron_folic_tablets";
        String HIV_SELF_TEST_KITS = "hiv_self_test_kits";
        String TYPE_OF_EDUCATION_PROVIDED = "type_of_education_provided";
        String DISEASE_SIGNS_MALE = "diseases_signs_and_symptoms_male";
        String DISEASE_SIGNS_FEMALE = "diseases_signs_and_symptoms_female";
        String CHILD_EXCESSIVE_CRYING = "child_excessive_crying";


    }

    interface JSON_FORM_KEYS {
        String GLOBAL = "global";
    }

    interface HPS_DISEASE_SIGNS_FIELDS {
        String HAS_DISEASE_SIGNS_AND_SYMPTOMS = "has_diseases_signs_and_symptoms";
        String SYMPTOMS = "symptoms";
        String DISEASE_SIGNS_AND_SYMPTOMS = "diseases_signs_and_symptoms";
        String MALARIA_MRDT = "malaria_mrdt";
        String MALARIA_MRDT_RESULT = "malaria_mrdt_result";
        String MUAC_STATUS = "muac_status";
        String MUAC_MM = "muac_mm";
        String ARM_CIRCUMFERENCE = "arm_circumference";
    }

    interface HPS_SYMPTOM_KEYS {
        String NAUSEA_AND_VOMITING = "nausea_and_vomiting";
        String DIARRHOEA = "diarrhoea";
        String DIFFICULT_IN_BREATHING = "difficult_in_breathing";
        String COUGH = "cough";
        String HEADACHE = "headache";
        String FEVER = "fever";
    }

    interface HPS_CURATIVE_SERVICES_FIELDS {
        String DISEASE_TESTS_CONDUCTED = "diseases_test_conducted";
        String TREATMENT_PROVIDED = "treatment_provided";
        String PROVISION_OF_CURATIVE_SERVICES = "provision_of_curative_services";
        String TREATMENT_PROVIDED_MALARIA_POSITIVE = "treatment_provided_malaria_positive";
    }

    interface HPS_TREATMENT_OPTION_KEYS {
        String ORAL_REHYDRATION_SOLUTIONS = "oral_rehydration_solutions";
        String PED_ZINC = "ped_zinc";
        String AMOXICILLIN_DT = "amoxicillin_dt";
        String MALARIA_DRUGS = "malaria_drugs";
        String ANTI_PAIN = "anti_pain";
        String FOOD_SUPPLEMENTS = "food_supplements";
    }

    interface HPS_THRESHOLDS {
        double MUAC_MALNOURISHED_THRESHOLD_MM = 125d;
    }

}
