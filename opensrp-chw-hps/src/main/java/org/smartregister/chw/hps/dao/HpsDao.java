package org.smartregister.chw.hps.dao;

import android.annotation.SuppressLint;

import org.smartregister.chw.hps.domain.HpsAnnualCensusRegisterModel;
import org.smartregister.chw.hps.domain.HpsDeathRegisterModel;
import org.smartregister.chw.hps.domain.HpsMobilizationSessionModel;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.dao.AbstractDao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HpsDao extends AbstractDao {
    private static final SimpleDateFormat df = new SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
    );

    private static final DataMap<MemberObject> memberObjectMap = cursor -> {

        MemberObject memberObject = new MemberObject();

        memberObject.setFirstName(getCursorValue(cursor, "first_name", ""));
        memberObject.setMiddleName(getCursorValue(cursor, "middle_name", ""));
        memberObject.setLastName(getCursorValue(cursor, "last_name", ""));
        memberObject.setAddress(getCursorValue(cursor, "village_town"));
        memberObject.setGender(getCursorValue(cursor, "gender"));
        memberObject.setMartialStatus(getCursorValue(cursor, "marital_status"));
        memberObject.setUniqueId(getCursorValue(cursor, "unique_id", ""));
        memberObject.setDob(getCursorValue(cursor, "dob"));
        memberObject.setFamilyBaseEntityId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setRelationalId(getCursorValue(cursor, "relational_id", ""));
        memberObject.setPrimaryCareGiver(getCursorValue(cursor, "primary_caregiver"));
        memberObject.setFamilyName(getCursorValue(cursor, "family_name", ""));
        memberObject.setPhoneNumber(getCursorValue(cursor, "phone_number", ""));
        memberObject.setHpsTestDate(getCursorValueAsDate(cursor, "hps_test_date", df));
        memberObject.setBaseEntityId(getCursorValue(cursor, "base_entity_id", ""));
        memberObject.setFamilyHead(getCursorValue(cursor, "family_head", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "pcg_phone_number", ""));
        memberObject.setFamilyHeadPhoneNumber(getCursorValue(cursor, "family_head_phone_number", ""));
        memberObject.setEnrollmentDate(getCursorValue(cursor,
                "enrollment_date",
                ""));

        String familyHeadName = getCursorValue(cursor, "family_head_first_name", "") + " "
                + getCursorValue(cursor, "family_head_middle_name", "");

        familyHeadName =
                (familyHeadName.trim() + " " + getCursorValue(cursor, "family_head_last_name", "")).trim();
        memberObject.setFamilyHeadName(familyHeadName);

        String familyPcgName = getCursorValue(cursor, "pcg_first_name", "") + " "
                + getCursorValue(cursor, "pcg_middle_name", "");

        familyPcgName =
                (familyPcgName.trim() + " " + getCursorValue(cursor, "pcg_last_name", "")).trim();
        memberObject.setPrimaryCareGiverName(familyPcgName);

        return memberObject;
    };

    public static String getEnrollmentDate(String baseEntityId) {
        String sql = "SELECT enrollment_date FROM ec_hps_client_register p " +
                " WHERE p.base_entity_id = '" + baseEntityId + "' ORDER BY enrollment_date DESC LIMIT 1";

        DataMap<String> dataMap = cursor -> getCursorValue(cursor, "enrollment_date");

        List<String> res = readData(sql, dataMap);
        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0);
        }
        return "";
    }

    public static int getVisitNumber(String baseEntityID) {
        String sql = "SELECT visit_number  FROM ec_hps_follow_up_visit WHERE entity_id='" + baseEntityID + "' ORDER BY visit_number DESC LIMIT 1";
        DataMap<Integer> map = cursor -> getCursorIntValue(cursor, "visit_number");
        List<Integer> res = readData(sql, map);

        if (res != null && !res.isEmpty() && res.get(0) != null) {
            return res.get(0) + 1;
        } else
            return 0;
    }

    public static boolean isRegisteredForHps(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM ec_hps_client_register p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.does_the_client_consent_to_be_enrolled_in_hps_services = 'yes' AND p.is_closed = 0 ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static boolean isHouseholdRegisteredForHps(String baseEntityID) {
        String sql = "SELECT count(p.base_entity_id) count FROM " + Constants.TABLES.HPS_HOUSEHOLD_REGISTER + " p " +
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.does_the_household_consent_to_be_enrolled_in_hps_services = 'yes' AND p.is_closed = 0 ";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.size() != 1)
            return false;

        return res.get(0) > 0;
    }

    public static Integer getHpsFamilyMembersCount(String familyBaseEntityId) {
        String sql = "SELECT count(emc.base_entity_id) count FROM ec_hps_client_register emc " +
                "INNER Join ec_family_member fm on fm.base_entity_id = emc.base_entity_id " +
                "WHERE fm.relational_id = '" + familyBaseEntityId + "' AND fm.is_closed = 0 " +
                "AND emc.is_closed = 0 AND emc.hps = 1";

        DataMap<Integer> dataMap = cursor -> getCursorIntValue(cursor, "count");

        List<Integer> res = readData(sql, dataMap);
        if (res == null || res.isEmpty())
            return 0;
        return res.get(0);
    }

    public static MemberObject getMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join " + Constants.TABLES.HPS_CLIENT_REGISTER + " mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static MemberObject getHouseholdMember(String baseEntityID) {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join " + Constants.TABLES.HPS_HOUSEHOLD_REGISTER + " mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 AND m.base_entity_id ='" + baseEntityID + "' ";
        List<MemberObject> res = readData(sql, memberObjectMap);
        if (res == null || res.size() != 1)
            return null;

        return res.get(0);
    }

    public static List<MemberObject> getMembers() {
        String sql = "select " +
                "m.base_entity_id , " +
                "m.unique_id , " +
                "m.relational_id , " +
                "m.dob , " +
                "m.first_name , " +
                "m.middle_name , " +
                "m.last_name , " +
                "m.gender , " +
                "m.marital_status , " +
                "m.phone_number , " +
                "m.other_phone_number , " +
                "f.first_name as family_name ," +
                "f.primary_caregiver , " +
                "f.family_head , " +
                "f.village_town ," +
                "fh.first_name as family_head_first_name , " +
                "fh.middle_name as family_head_middle_name , " +
                "fh.last_name as family_head_last_name, " +
                "fh.phone_number as family_head_phone_number ,  " +
                "pcg.first_name as pcg_first_name , " +
                "pcg.last_name as pcg_last_name , " +
                "pcg.middle_name as pcg_middle_name , " +
                "pcg.phone_number as  pcg_phone_number , " +
                "mr.* " +
                "from ec_family_member m " +
                "inner join ec_family f on m.relational_id = f.base_entity_id " +
                "inner join ec_hps_client_register mr on mr.base_entity_id = m.base_entity_id " +
                "left join ec_family_member fh on fh.base_entity_id = f.family_head " +
                "left join ec_family_member pcg on pcg.base_entity_id = f.primary_caregiver " +
                "where mr.is_closed = 0 ";

        return readData(sql, memberObjectMap);
    }


    public static List<HpsMobilizationSessionModel> getHpsMobilizationSessions() {
        String sql = "SELECT * FROM " + Constants.TABLES.HPS_MOBILIZATION_SESSIONS;

        @SuppressLint("Range")
        DataMap<HpsMobilizationSessionModel> dataMap = cursor -> {
            HpsMobilizationSessionModel model = new HpsMobilizationSessionModel();
            model.setSessionId(cursor.getString(cursor.getColumnIndex("id")));
            model.setDateOfGathering(cursor.getString(cursor.getColumnIndex("date_of_gathering")));
            model.setTheMethodOfEducationAndAwarenessUsed(
                    cursor.getString(cursor.getColumnIndex("method_of_education_and_awareness_used"))
            );
            model.setAreaWhereMobilizationTookPlace(
                    cursor.getString(cursor.getColumnIndex("area_where_mobilization_took_place"))
            );
            model.setNumberOfFemalesWhoAttended(
                    cursor.getString(cursor.getColumnIndex("number_of_females_who_attended"))
            );
            model.setNumberOfMalesWhoAttended(
                    cursor.getString(cursor.getColumnIndex("number_of_males_who_attended"))
            );
            model.setWasEducationProvided(
                    cursor.getString(cursor.getColumnIndex("was_education_provided"))
            );
            model.setEducationProvided(
                    cursor.getString(cursor.getColumnIndex("education_provided"))
            );
            model.setInformationEducationAndCommunicationMaterials(
                    cursor.getString(cursor.getColumnIndex("information_education_and_communication_materials"))
            );
            model.setBrochureMaterials(
                    cursor.getString(cursor.getColumnIndex("brochure_materials"))
            );
            model.setNumberOfBrochuresProvided(
                    cursor.getString(cursor.getColumnIndex("number_of_brochures_provided"))
            );
            model.setPosterMaterials(
                    cursor.getString(cursor.getColumnIndex("poster_materials"))
            );
            model.setNumberOfPostersProvided(
                    cursor.getString(cursor.getColumnIndex("number_of_posters_provided"))
            );
            model.setLeafletMaterials(
                    cursor.getString(cursor.getColumnIndex("leaflet_materials"))
            );
            model.setNumberOfLeafletProvided(
                    cursor.getString(cursor.getColumnIndex("number_of_leaflet_provided"))
            );
            model.setOtherIecMaterials(
                    cursor.getString(cursor.getColumnIndex("other_iec_materials"))
            );
            model.setNumberOfOtherIecProvided(
                    cursor.getString(cursor.getColumnIndex("number_of_other_iec_provided"))
            );
            return model;
        };

        List<HpsMobilizationSessionModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) return null;
        return res;
    }


    public static List<HpsDeathRegisterModel> getHpsDeathRegisterRecords() {
        String sql = "SELECT * FROM " + Constants.TABLES.HPS_DEATH_REGISTER+" WHERE is_closed = 0 AND dod IS NOT NULL AND dob IS NOT NULL";

        @SuppressLint("Range")
        DataMap<HpsDeathRegisterModel> dataMap = cursor -> {
            HpsDeathRegisterModel model = new HpsDeathRegisterModel();
            model.setDeathId(cursor.getString(cursor.getColumnIndex("id")));
            model.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
            model.setMiddleName(cursor.getString(cursor.getColumnIndex("middle_name")));
            model.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
            model.setDob(cursor.getString(cursor.getColumnIndex("dob")));
            model.setDod(cursor.getString(cursor.getColumnIndex("dod")));
            model.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            model.setCauseOfDeath(cursor.getString(cursor.getColumnIndex("cause_of_death")));
            return model;
        };

        List<HpsDeathRegisterModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) return null;
        return res;
    }

    public static void saveHpsMobilization(String baseEntityId,
                                           String dateOfGathering,
                                           String methodOfEducationAndAwarenessUsed,
                                           String areaWhereMobilizationTookPlace,
                                           String numberOfFemalesWhoAttended,
                                           String numberOfMalesWhoAttended,
                                           String wasEducationProvided,
                                           String educationProvided,
                                           String informationEducationAndCommunicationMaterial,
                                           String brochureMaterials,
                                           String numberOfBrochuresProvided,
                                           String posterMaterials,
                                           String numberOfPostersProvided,
                                           String leafletMaterials,
                                           String numberOfLeafletProvided,
                                           String otherIecMaterials,
                                           String numberOfOtherIecProvided,
                                           long lastInteractedWith) {

        String sql = "INSERT INTO ec_hps_mobilization " +
                "    (id, base_entity_id, date_of_gathering, method_of_education_and_awareness_used, " +
                "     area_where_mobilization_took_place, number_of_females_who_attended, number_of_males_who_attended, " +
                "     was_education_provided, education_provided, information_education_and_communication_materials, " +
                "     brochure_materials, number_of_brochures_provided, poster_materials, number_of_posters_provided, " +
                "     leaflet_materials, number_of_leaflet_provided, other_iec_materials, number_of_other_iec_provided, " +
                "     last_interacted_with) " +
                "VALUES ('" + baseEntityId + "', '" + baseEntityId + "', '" + dateOfGathering + "', '" + methodOfEducationAndAwarenessUsed + "', '" +
                areaWhereMobilizationTookPlace + "', '" + numberOfFemalesWhoAttended + "', '" + numberOfMalesWhoAttended + "', '" +
                wasEducationProvided + "', '" + educationProvided + "', '" + informationEducationAndCommunicationMaterial + "', '" +
                brochureMaterials + "', '" + numberOfBrochuresProvided + "', '" + posterMaterials + "', '" +
                numberOfPostersProvided + "', '" + leafletMaterials + "', '" + numberOfLeafletProvided + "', '" +
                otherIecMaterials + "', '" + numberOfOtherIecProvided + "', " + lastInteractedWith + ") " +
                "ON CONFLICT (id) DO UPDATE " +
                "SET date_of_gathering = '" + dateOfGathering + "', " +
                "    method_of_education_and_awareness_used = '" + methodOfEducationAndAwarenessUsed + "', " +
                "    area_where_mobilization_took_place = '" + areaWhereMobilizationTookPlace + "', " +
                "    number_of_females_who_attended = '" + numberOfFemalesWhoAttended + "', " +
                "    number_of_males_who_attended = '" + numberOfMalesWhoAttended + "', " +
                "    was_education_provided = '" + wasEducationProvided + "', " +
                "    education_provided = '" + educationProvided + "', " +
                "    information_education_and_communication_materials = '" + informationEducationAndCommunicationMaterial + "', " +
                "    brochure_materials = '" + brochureMaterials + "', " +
                "    number_of_brochures_provided = '" + numberOfBrochuresProvided + "', " +
                "    poster_materials = '" + posterMaterials + "', " +
                "    number_of_posters_provided = '" + numberOfPostersProvided + "', " +
                "    leaflet_materials = '" + leafletMaterials + "', " +
                "    number_of_leaflet_provided = '" + numberOfLeafletProvided + "', " +
                "    other_iec_materials = '" + otherIecMaterials + "', " +
                "    number_of_other_iec_provided = '" + numberOfOtherIecProvided + "', " +
                "    last_interacted_with = " + lastInteractedWith;

        updateDB(sql);
    }


    public static void saveHpsDeathRegister(String baseEntityId,
                                            String dod,
                                            String firstName,
                                            String middleName,
                                            String lastName,
                                            String dob,
                                            String sex,
                                            String causeOfDeath,
                                            String causeOfDeathSpecify,
                                            long lastInteractedWith) {

        String sql = "INSERT INTO ec_hps_death_register " +
                "    (id, base_entity_id, dod, first_name, middle_name, last_name, dob, sex, cause_of_death, cause_of_death_specify, last_interacted_with) " +
                "VALUES ('" + baseEntityId + "', '" + baseEntityId + "', '" + dod + "', '" + firstName + "', '" + middleName + "', '" + lastName + "', '" + dob + "', '" + sex + "', '" + causeOfDeath + "', '" + causeOfDeathSpecify + "', " + lastInteractedWith + ") " +
                "ON CONFLICT (id) DO UPDATE " +
                "SET dod = '" + dod + "', " +
                "    first_name = '" + firstName + "', " +
                "    middle_name = '" + middleName + "', " +
                "    last_name = '" + lastName + "', " +
                "    dob = '" + dob + "', " +
                "    sex = '" + sex + "', " +
                "    cause_of_death = '" + causeOfDeath + "', " +
                "    cause_of_death_specify = '" + causeOfDeathSpecify + "', " +
                "    last_interacted_with = " + lastInteractedWith;

        updateDB(sql);
    }


    public static void saveHpsAnnualCensusRegisterModel(
            String baseEntityId,
            String year,
            String selectAgeGroup,
            String numberOfMaleByAgeGroupUnder1,
            String numberOfFemaleByAgeGroupUnder1,
            String numberOfMaleByAgeGroup1_4,
            String numberOfFemaleByAgeGroup1_4,
            String numberOfMaleByAgeGroup5_14,
            String numberOfFemaleByAgeGroup5_14,
            String numberOfMaleByAgeGroup15_49,
            String numberOfFemaleByAgeGroup15_49,
            String numberOfMaleByAgeGroup50_59,
            String numberOfFemaleByAgeGroup50_59,
            String numberOfMaleByAgeGroup60Plus,
            String numberOfFemaleByAgeGroup60Plus,
            String numberOfHouseHoldWithRoadAccess,
            String numberOfHouseHoldsWithAtLeastOneLandlineOrMobilePhone,
            String numberOfHouseHoldWithBasicNutritionSourceVegetable,
            String numberOfHouseHoldWithBasicNutritionSourceFruitTrees,
            String numberOfHouseHoldWithBasicNutritionSourceDomesticAnimal,
            String selectCentersCategory,
            String numberOfPreSchoolsGovernment,
            String numberOfPrimarySchoolsGovernment,
            String numberOfSecondarySchoolsGovernment,
            String numberOfUniversitiesGovernment,
            String numberOfSpecialNeedsSchoolsGovernment,
            String numberOfDispensaryGovernment,
            String numberOfHealthCentersGovernment,
            String numberOfHospitalGovernment,
            String numberOfSpecialClinicsGovernment,
            String numberOfLaboratoryGovernment,
            String numberOfPharmacyGovernment,
            String numberOfADDOGovernment,
            String numberOfMaternityHomeGovernment,
            String numberOfOrphanCareCentersGovernment,
            String numberOfCentersForChildrenWithDisabilitiesGovernment,
            String numberOfCbecdcRehabilitationCentreGovernment,
            String numberOfDayCareCentersGovernment,
            String numberOfElderlyCareCentersGovernment,
            String numberOfPreSchoolsFaithBasedOrganisation,
            String numberOfPrimarySchoolsFaithBasedOrganisation,
            String numberOfSecondarySchoolsFaithBasedOrganisation,
            String numberOfUniversitiesFaithBasedOrganisation,
            String numberOfSpecialNeedsSchoolsFaithBasedOrganisation,
            String numberOfDispensaryFaithBasedOrganisation,
            String numberOfHealthCentersFaithBasedOrganisation,
            String numberOfHospitalFaithBasedOrganisation,
            String numberOfSpecialClinicsFaithBasedOrganisation,
            String numberOfLaboratoryFaithBasedOrganisation,
            String numberOfPharmacyFaithBasedOrganisation,
            String numberOfADDOFaithBasedOrganisation,
            String numberOfMaternityHomeFaithBasedOrganisation,
            String numberOfOrphanCareCentersFaithBasedOrganisation,
            String numberOfCentersForChildrenWithDisabilitiesFaithBasedOrganisation,
            String numberOfCbecdcRehabilitationCentreFaithBasedOrganisation,
            String numberOfDayCareCentersFaithBasedOrganisation,
            String numberOfElderlyCareCentersFaithBasedOrganisation,
            String numberOfPreSchoolsPublic,
            String numberOfPrimarySchoolsPublic,
            String numberOfSecondarySchoolsPublic,
            String numberOfUniversitiesPublic,
            String numberOfSpecialNeedsSchoolsPublic,
            String numberOfDispensaryPublic,
            String numberOfHealthCentersPublic,
            String numberOfHospitalPublic,
            String numberOfSpecialClinicsPublic,
            String numberOfLaboratoryPublic,
            String numberOfPharmacyPublic,
            String numberOfADDOPublic,
            String numberOfMaternityHomePublic,
            String numberOfOrphanCareCentersPublic,
            String numberOfCentersForChildrenWithDisabilitiesPublic,
            String numberOfCbecdcRehabilitationCentrePublic,
            String numberOfDayCareCentersPublic,
            String numberOfElderlyCareCentersPublic,
            String numberOfPreSchoolsPrivate,
            String numberOfPrimarySchoolsPrivate,
            String numberOfSecondarySchoolsPrivate,
            String numberOfUniversitiesPrivate,
            String numberOfSpecialNeedsSchoolsPrivate,
            String numberOfDispensaryPrivate,
            String numberOfHealthCentersPrivate,
            String numberOfHospitalPrivate,
            String numberOfSpecialClinicsPrivate,
            String numberOfLaboratoryPrivate,
            String numberOfPharmacyPrivate,
            String numberOfADDOPrivate,
            String numberOfMaternityHomePrivate,
            String numberOfOrphanCareCentersPrivate,
            String numberOfCentersForChildrenWithDisabilitiesPrivate,
            String numberOfCbecdcRehabilitationCentrePrivate,
            String numberOfDayCareCentersPrivate,
            String numberOfElderlyCareCentersPrivate,
            String numberOfFoodShopVisited,
            String numberOfRestaurantsVisited,
            String numberOfButcheriesVisited,
            String numberOfBarAndClubsVisited,
            String numberOfGuestHouseVisited,
            String numberOfLocaFoodVendorsVisited,
            String numberOfMarketsVisited,
            String numberOfPublicToiletsVisited,
            String numberOfBusStationsVisited,
            String numberOfPrimarySchoolsVisited,
            String numberOfSecondarySchoolsVisited,
            String numberOfHospitalVisited,
            String numberOfHealthCentersVisited,
            String numberOfDispensariesVisited,
            String numberOfOfficesVisited,
            String numberOfUniversitiesCollegeVisited,
            String numberOfFoodShopThatMetTheStandards,
            String numberOfRestaurantsThatMetTheStandards,
            String numberOfButcheriesThatMetTheStandards,
            String numberOfBarAndClubsThatMetTheStandards,
            String numberOfGuestHouseThatMetTheStandards,
            String numberOfLocaFoodVendorsThatMetTheStandards,
            String numberOfMarketsThatMetTheStandards,
            String numberOfPublicToiletsThatMetTheStandards,
            String numberOfBusStationsThatMetTheStandards,
            String numberOfPrimarySchoolsThatMetTheStandards,
            String numberOfSecondarySchoolsThatMetTheStandards,
            String numberOfHospitalThatMetTheStandards,
            String numberOfHealthCentersThatMetTheStandards,
            String numberOfDispensariesThatMetTheStandards,
            String numberOfOfficesThatMetTheStandards,
            String numberOfUniversitiesCollegeThatMetTheStandards,
            String numberOfInspectedAgricultureAreas,
            String numberOfInspectedLivestockKeepingAreas,
            String numberOfInspectedFishingAreas,
            String numberOfInspectedIndustriesAreas,
            String numberOfInspectedOfficesAreas,
            String numberOfInspectedTransportationAreas,
            String numberOfOtherInspectedAreas,
            String numberOfAgricultureAreasInspectedWithRiskIndicators,
            String numberOfLivestockKeepingAreasInspectedWithRiskIndicators,
            String numberOfFishingAreasInspectedWithRiskIndicators,
            String numberOfIndustriesAreasInspectedWithRiskIndicators,
            String numberOfOfficesAreasInspectedWithRiskIndicators,
            String numberOfTransportationAreasInspectedWithRiskIndicators,
            String numberOfOtherAreasInspectedWithRiskIndicators,
            String numberOfInspectedGrains,
            String numberOfInspectedLegumes,
            String numberOfInspectedMeat,
            String numberOfInspectedFishing,
            String numberOfInspectedAlcoholicBeverages,
            String numberOfInspectedNonAlcoholicBeverages,
            String numberOfGrainsDiscarded,
            String numberOfLegumesDiscarded,
            String numberOfMeatDiscarded,
            String numberOfFishingDiscarded,
            String numberOfAlcoholicBeverageDiscarded,
            String numberOfNonAlcoholicBeverageDiscarded,
            String healthReportsAffectingPeopleInWorkplacesRespiratoryDiseases,
            String healthReportsAffectingPeopleInWorkplacesToxicChemicals,
            String healthReportsAffectingPeopleInWorkplacesBurns,
            String healthReportsAffectingPeopleInWorkplacesHearingLoss,
            String healthReportsAffectingPeopleInWorkplacesEyeProblems,
            String healthReportsAffectingPeopleInWorkplacesOtherEffects,
            String amountOfSolidWasteGeneratedAnnuallyTons,
            String amountOfSolidWasteDisposedAtADesignatedSiteAnnuallyTons,
            String numberOfWasteCollectionEquipmentVehicles,
            String numberOfWasteCollectionEquipmentTractors,
            String numberOfWasteCollectionEquipmentCarts,
            String numberOfWasteCollectionEquipmentWheelbarrows,
            String numberOfWasteCollectionEquipmentOthers,
            String numberOfAreasSprayedWithPesticidesPonds,
            String numberOfAreasSprayedWithPesticidesCans,
            String numberOfAreasSprayedWithPesticidesDrums,
            String numberOfAreasSprayedWithPesticidesBarrels,
            String numberOfAreasSprayedWithPesticidesCoconutShells,
            String numberOfTimesSprayingWasDonePonds,
            String numberOfTimesSprayingWasDoneCans,
            String numberOfTimesSprayingWasDoneDrums,
            String numberOfTimesSprayingWasDoneBarrels,
            String numberOfTimesSprayingWasDoneCoconutShells,
            String typesOfPesticidesUsedPonds,
            String typesOfPesticidesUsedCans,
            String typesOfPesticidesUsedDrums,
            String typesOfPesticidesUsedBarrels,
            String typesOfPesticidesUsedCoconutShells,
            String amountOfPesticideUsedPonds,
            String amountOfPesticideUsedCans,
            String amountOfPesticideUsedDrums,
            String amountOfPesticideUsedBarrels,
            String amountOfPesticideUsedCoconutShells,
            String numberOfHouseHolds,
            String numberOfMaleCapableOfEngagingInEconomicActivities,
            String numberOfFemaleCapableOfEngagingInEconomicActivities,
            String numberOfMaleEngagedInEconomicActivities,
            String numberOfFemaleEngagedInEconomicActivities,
            String numberOfHouseholdsMostCommonlyUseTapAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseRiverAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseShallowWellAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseWaterPondAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseSmallDamAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseLakeAsSourcesOfWater,
            String numberOfHouseholdsMostCommonlyUseSpringAsSourcesOfWater,
            String numberOfHouseholdsUsingElectricityAsSourceOfEnergyForLighting,
            String numberOfHouseholdsUsingSolarAsSourceOfEnergyForLighting,
            String numberOfHouseholdsUsingKerosineAsSourceOfEnergyForLighting,
            String numberOfHouseholdsUsingKoroboiAsSourceOfEnergyForLighting,
            String numberOfHouseholdsUsingOtherSourceOfEnergyForLighting,
            String numberOfHouseholdsUsingElectricityAsSourceOfCookingEnergy,
            String numberOfHouseholdsUsingSolarAsSourceOfCookingEnergy,
            String numberOfHouseholdsUsingKerosineAsSourceOfCookingEnergy,
            String numberOfHouseholdsUsingGasAsSourceOfCookingEnergy,
            String numberOfHouseholdsUsingCharcoalAsSourceOfCookingEnergy,
            String numberOfHouseholdsUsingFirewoodAsSourceOfCookingEnergy,
            String numberOfHealthCommitteeMembersForEffectiveCommitteeMeetings,
            String numberOfCommitteeMembersAttendedFisrtQuarter,
            String numberOfRegisteredAlternativeMedicineServiceProviders,
            String numberOfRegisteredTraditionalMedicineServiceProviders,
            String numberOfUnregisteredAlternativeMedicineServiceProviders,
            String numberOfUnregisteredTraditionalMedicineServiceProviders,
            long lastInteractedWith
    ) {
        String sql = "INSERT INTO ec_hps_annual_census_register ("
                + "id, base_entity_id, year, select_age_group, number_of_male_by_age_group_under1, number_of_female_by_age_group_under1, "
                + "number_of_male_by_age_group_1_4, number_of_female_by_age_group_1_4, number_of_male_by_age_group_5_14, number_of_female_by_age_group_5_14, "
                + "number_of_male_by_age_group_15_49, number_of_female_by_age_group_15_49, number_of_male_by_age_group_50_59, number_of_female_by_age_group_50_59, "
                + "number_of_male_by_age_group_60_plus, number_of_female_by_age_group_60_plus, number_of_house_hold_with_road_access, "
                + "number_of_house_holds_with_at_least_one_landline_or_mobile_phone, number_of_house_hold_with_basic_nutrition_source_vegetable, "
                + "number_of_house_hold_with_basic_nutrition_source_fruit_trees, number_of_house_hold_with_basic_nutrition_source_domestic_animal, "
                + "select_centers_category, number_of_pre_schools_government, number_of_primary_schools_government, number_of_secondary_schools_government, "
                + "number_of_universities_government, number_of_special_needs_schools_government, number_of_dispensary_government, number_of_health_centers_government, "
                + "number_of_hospital_government, number_of_special_clinics_government, number_of_laboratory_government, number_of_pharmacy_government, "
                + "number_of_addo_government, number_of_maternity_home_government, number_of_orphan_care_centers_government, "
                + "number_of_centers_for_children_with_disabilities_government, number_of_cbecdc_rehabilitation_centre_government, "
                + "number_of_day_care_centers_government, number_of_elderly_care_centers_government, number_of_pre_schools_faith_based_organisation, "
                + "number_of_primary_schools_faith_based_organisation, number_of_secondary_schools_faith_based_organisation, "
                + "number_of_universities_faith_based_organisation, number_of_special_needs_schools_faith_based_organisation, "
                + "number_of_dispensary_faith_based_organisation, number_of_health_centers_faith_based_organisation, number_of_hospital_faith_based_organisation, "
                + "number_of_special_clinics_faith_based_organisation, number_of_laboratory_faith_based_organisation, number_of_pharmacy_faith_based_organisation, "
                + "number_of_addo_faith_based_organisation, number_of_maternity_home_faith_based_organisation, number_of_orphan_care_centers_faith_based_organisation, "
                + "number_of_centers_for_children_with_disabilities_faith_based_organisation, number_of_cbecdc_rehabilitation_centre_faith_based_organisation, "
                + "number_of_day_care_centers_faith_based_organisation, number_of_elderly_care_centers_faith_based_organisation, "
                + "number_of_pre_schools_public, number_of_primary_schools_public, number_of_secondary_schools_public, number_of_universities_public, "
                + "number_of_special_needs_schools_public, number_of_dispensary_public, number_of_health_centers_public, number_of_hospital_public, "
                + "number_of_special_clinics_public, number_of_laboratory_public, number_of_pharmacy_public, number_of_addo_public, number_of_maternity_home_public, "
                + "number_of_orphan_care_centers_public, number_of_centers_for_children_with_disabilities_public, number_of_cbecdc_rehabilitation_centre_public, "
                + "number_of_day_care_centers_public, number_of_elderly_care_centers_public, number_of_pre_schools_private, number_of_primary_schools_private, "
                + "number_of_secondary_schools_private, number_of_universities_private, number_of_special_needs_schools_private, number_of_dispensary_private, "
                + "number_of_health_centers_private, number_of_hospital_private, number_of_special_clinics_private, number_of_laboratory_private, "
                + "number_of_pharmacy_private, number_of_addo_private, number_of_maternity_home_private, number_of_orphan_care_centers_private, "
                + "number_of_centers_for_children_with_disabilities_private, number_of_cbecdc_rehabilitation_centre_private, number_of_day_care_centers_private, "
                + "number_of_elderly_care_centers_private, number_of_food_shop_visited, number_of_restaurants_visited, number_of_butcheries_visited, "
                + "number_of_bar_and_clubs_visited, number_of_guest_house_visited, number_of_loca_food_vendors_visited, number_of_markets_visited, "
                + "number_of_public_toilets_visited, number_of_bus_stations_visited, number_of_primary_schools_visited, number_of_secondary_schools_visited, "
                + "number_of_hospital_visited, number_of_health_centers_visited, number_of_dispensaries_visited, number_of_offices_visited, "
                + "number_of_universities_college_visited, number_of_food_shop_that_met_the_standards, number_of_restaurants_that_met_the_standards, "
                + "number_of_butcheries_that_met_the_standards, number_of_bar_and_clubs_that_met_the_standards, number_of_guest_house_that_met_the_standards, "
                + "number_of_loca_food_vendors_that_met_the_standards, number_of_markets_that_met_the_standards, number_of_public_toilets_that_met_the_standards, "
                + "number_of_bus_stations_that_met_the_standards, number_of_primary_schools_that_met_the_standards, number_of_secondary_schools_that_met_the_standards, "
                + "number_of_hospital_that_met_the_standards, number_of_health_centers_that_met_the_standards, number_of_dispensaries_that_met_the_standards, "
                + "number_of_offices_that_met_the_standards, number_of_universities_college_that_met_the_standards, number_of_inspected_agriculture_areas, "
                + "number_of_inspected_livestock_keeping_areas, number_of_inspected_fishing_areas, number_of_inspected_industries_areas, "
                + "number_of_inspected_offices_areas, number_of_inspected_transportation_areas, number_of_other_inspected_areas, "
                + "number_of_agriculture_areas_inspected_with_risk_indicators, number_of_livestock_keeping_areas_inspected_with_risk_indicators, "
                + "number_of_fishing_areas_inspected_with_risk_indicators, number_of_industries_areas_inspected_with_risk_indicators, "
                + "number_of_offices_areas_inspected_with_risk_indicators, number_of_transportation_areas_inspected_with_risk_indicators, "
                + "number_of_other_areas_inspected_with_risk_indicators, number_of_inspected_grains, number_of_inspected_legumes, "
                + "number_of_inspected_meat, number_of_inspected_fishing, number_of_inspected_alcoholic_beverages, "
                + "number_of_inspected_non_alcoholic_beverages, number_of_grains_discarded, number_of_legumes_discarded, number_of_meat_discarded, "
                + "number_of_fishing_discarded, number_of_alcoholic_beverage_discarded, number_of_non_alcoholic_beverage_discarded, "
                + "health_reports_affecting_people_in_workplaces_respiratory_diseases, health_reports_affecting_people_in_workplaces_toxic_chemicals, "
                + "health_reports_affecting_people_in_workplaces_burns, health_reports_affecting_people_in_workplaces_hearing_loss, "
                + "health_reports_affecting_people_in_workplaces_eye_problems, health_reports_affecting_people_in_workplaces_other_effects, "
                + "amount_of_solid_waste_generated_annually_tons, amount_of_solid_waste_disposed_at_a_designated_site_annually_tons, "
                + "number_of_waste_collection_equipment_vehicles, number_of_waste_collection_equipment_tractors, "
                + "number_of_waste_collection_equipment_carts, number_of_waste_collection_equipment_wheelbarrows, "
                + "number_of_waste_collection_equipment_others, number_of_areas_sprayed_with_pesticides_ponds, "
                + "number_of_areas_sprayed_with_pesticides_cans, number_of_areas_sprayed_with_pesticides_drums, "
                + "number_of_areas_sprayed_with_pesticides_barrels, number_of_areas_sprayed_with_pesticides_coconut_shells, "
                + "number_of_times_spraying_was_done_ponds, number_of_times_spraying_was_done_cans, number_of_times_spraying_was_done_drums, "
                + "number_of_times_spraying_was_done_barrels, number_of_times_spraying_was_done_coconut_shells, "
                + "types_of_pesticides_used_ponds, types_of_pesticides_used_cans, types_of_pesticides_used_drums, "
                + "types_of_pesticides_used_barrels, types_of_pesticides_used_coconut_shells, amount_of_pesticide_used_ponds, "
                + "amount_of_pesticide_used_cans, amount_of_pesticide_used_drums, amount_of_pesticide_used_barrels, "
                + "amount_of_pesticide_used_coconut_shells, "
                + "number_of_house_hold, number_of_male_capable_of_engaging_in_economic_activities, number_of_female_capable_of_engaging_in_economic_activities, number_of_male_engaged_in_economic_activities, number_of_female_engaged_in_economic_activities, " +
                " number_of_households_most_commonly_use_tap_as_sources_of_water,number_of_households_most_commonly_use_river_as_sources_of_water,number_of_households_most_commonly_use_shallow_well_as_sources_of_water,number_of_households_most_commonly_use_water_pond_as_sources_of_water,number_of_households_most_commonly_use_small_dam_as_sources_of_water,number_of_households_most_commonly_use_lake_as_sources_of_water,number_of_households_most_commonly_use_spring_as_sources_of_water,number_of_households_using_electricity_as_source_of_energy_for_lighting,number_of_households_using_solar_as_source_of_energy_for_lighting,number_of_households_using_kerosine_as_source_of_energy_for_lighting,number_of_households_using_koroboi_as_source_of_energy_for_lighting,number_of_households_using_other_source_of_energy_for_lighting,number_of_households_using_electricity_as_source_of_cooking_energy,number_of_households_using_solar_as_source_of_cooking_energy,number_of_households_using_kerosine_as_source_of_cooking_energy,number_of_households_using_gas_as_source_of_cooking_energy,number_of_households_using_charcoal_as_source_of_cooking_energy,number_of_households_using_firewood_as_source_of_cooking_energy," +
                " number_of_health_committee_members_for_effective_committee_meetings,number_of_committee_members_attended_fisrt_quarter,number_of_registered_alternative_medicine_service_providers,number_of_registered_traditional_medicine_service_providers,number_of_unregistered_alternative_medicine_service_providers,number_of_unregistered_traditional_medicine_service_providers, last_interacted_with) VALUES ("
                + "'" + baseEntityId + "', "
                + "'" + baseEntityId + "', "
                + "'" + year + "', "
                + "'" + selectAgeGroup + "', "
                + "'" + numberOfMaleByAgeGroupUnder1 + "', "
                + "'" + numberOfFemaleByAgeGroupUnder1 + "', "
                + "'" + numberOfMaleByAgeGroup1_4 + "', "
                + "'" + numberOfFemaleByAgeGroup1_4 + "', "
                + "'" + numberOfMaleByAgeGroup5_14 + "', "
                + "'" + numberOfFemaleByAgeGroup5_14 + "', "
                + "'" + numberOfMaleByAgeGroup15_49 + "', "
                + "'" + numberOfFemaleByAgeGroup15_49 + "', "
                + "'" + numberOfMaleByAgeGroup50_59 + "', "
                + "'" + numberOfFemaleByAgeGroup50_59 + "', "
                + "'" + numberOfMaleByAgeGroup60Plus + "', "
                + "'" + numberOfFemaleByAgeGroup60Plus + "', "
                + "'" + numberOfHouseHoldWithRoadAccess + "', "
                + "'" + numberOfHouseHoldsWithAtLeastOneLandlineOrMobilePhone + "', "
                + "'" + numberOfHouseHoldWithBasicNutritionSourceVegetable + "', "
                + "'" + numberOfHouseHoldWithBasicNutritionSourceFruitTrees + "', "
                + "'" + numberOfHouseHoldWithBasicNutritionSourceDomesticAnimal + "', "
                + "'" + selectCentersCategory + "', "
                + "'" + numberOfPreSchoolsGovernment + "', "
                + "'" + numberOfPrimarySchoolsGovernment + "', "
                + "'" + numberOfSecondarySchoolsGovernment + "', "
                + "'" + numberOfUniversitiesGovernment + "', "
                + "'" + numberOfSpecialNeedsSchoolsGovernment + "', "
                + "'" + numberOfDispensaryGovernment + "', "
                + "'" + numberOfHealthCentersGovernment + "', "
                + "'" + numberOfHospitalGovernment + "', "
                + "'" + numberOfSpecialClinicsGovernment + "', "
                + "'" + numberOfLaboratoryGovernment + "', "
                + "'" + numberOfPharmacyGovernment + "', "
                + "'" + numberOfADDOGovernment + "', "
                + "'" + numberOfMaternityHomeGovernment + "', "
                + "'" + numberOfOrphanCareCentersGovernment + "', "
                + "'" + numberOfCentersForChildrenWithDisabilitiesGovernment + "', "
                + "'" + numberOfCbecdcRehabilitationCentreGovernment + "', "
                + "'" + numberOfDayCareCentersGovernment + "', "
                + "'" + numberOfElderlyCareCentersGovernment + "', "
                + "'" + numberOfPreSchoolsFaithBasedOrganisation + "', "
                + "'" + numberOfPrimarySchoolsFaithBasedOrganisation + "', "
                + "'" + numberOfSecondarySchoolsFaithBasedOrganisation + "', "
                + "'" + numberOfUniversitiesFaithBasedOrganisation + "', "
                + "'" + numberOfSpecialNeedsSchoolsFaithBasedOrganisation + "', "
                + "'" + numberOfDispensaryFaithBasedOrganisation + "', "
                + "'" + numberOfHealthCentersFaithBasedOrganisation + "', "
                + "'" + numberOfHospitalFaithBasedOrganisation + "', "
                + "'" + numberOfSpecialClinicsFaithBasedOrganisation + "', "
                + "'" + numberOfLaboratoryFaithBasedOrganisation + "', "
                + "'" + numberOfPharmacyFaithBasedOrganisation + "', "
                + "'" + numberOfADDOFaithBasedOrganisation + "', "
                + "'" + numberOfMaternityHomeFaithBasedOrganisation + "', "
                + "'" + numberOfOrphanCareCentersFaithBasedOrganisation + "', "
                + "'" + numberOfCentersForChildrenWithDisabilitiesFaithBasedOrganisation + "', "
                + "'" + numberOfCbecdcRehabilitationCentreFaithBasedOrganisation + "', "
                + "'" + numberOfDayCareCentersFaithBasedOrganisation + "', "
                + "'" + numberOfElderlyCareCentersFaithBasedOrganisation + "', "
                + "'" + numberOfPreSchoolsPublic + "', "
                + "'" + numberOfPrimarySchoolsPublic + "', "
                + "'" + numberOfSecondarySchoolsPublic + "', "
                + "'" + numberOfUniversitiesPublic + "', "
                + "'" + numberOfSpecialNeedsSchoolsPublic + "', "
                + "'" + numberOfDispensaryPublic + "', "
                + "'" + numberOfHealthCentersPublic + "', "
                + "'" + numberOfHospitalPublic + "', "
                + "'" + numberOfSpecialClinicsPublic + "', "
                + "'" + numberOfLaboratoryPublic + "', "
                + "'" + numberOfPharmacyPublic + "', "
                + "'" + numberOfADDOPublic + "', "
                + "'" + numberOfMaternityHomePublic + "', "
                + "'" + numberOfOrphanCareCentersPublic + "', "
                + "'" + numberOfCentersForChildrenWithDisabilitiesPublic + "', "
                + "'" + numberOfCbecdcRehabilitationCentrePublic + "', "
                + "'" + numberOfDayCareCentersPublic + "', "
                + "'" + numberOfElderlyCareCentersPublic + "', "
                + "'" + numberOfPreSchoolsPrivate + "', "
                + "'" + numberOfPrimarySchoolsPrivate + "', "
                + "'" + numberOfSecondarySchoolsPrivate + "', "
                + "'" + numberOfUniversitiesPrivate + "', "
                + "'" + numberOfSpecialNeedsSchoolsPrivate + "', "
                + "'" + numberOfDispensaryPrivate + "', "
                + "'" + numberOfHealthCentersPrivate + "', "
                + "'" + numberOfHospitalPrivate + "', "
                + "'" + numberOfSpecialClinicsPrivate + "', "
                + "'" + numberOfLaboratoryPrivate + "', "
                + "'" + numberOfPharmacyPrivate + "', "
                + "'" + numberOfADDOPrivate + "', "
                + "'" + numberOfMaternityHomePrivate + "', "
                + "'" + numberOfOrphanCareCentersPrivate + "', "
                + "'" + numberOfCentersForChildrenWithDisabilitiesPrivate + "', "
                + "'" + numberOfCbecdcRehabilitationCentrePrivate + "', "
                + "'" + numberOfDayCareCentersPrivate + "', "
                + "'" + numberOfElderlyCareCentersPrivate + "', "
                + "'" + numberOfFoodShopVisited + "', "
                + "'" + numberOfRestaurantsVisited + "', "
                + "'" + numberOfButcheriesVisited + "', "
                + "'" + numberOfBarAndClubsVisited + "', "
                + "'" + numberOfGuestHouseVisited + "', "
                + "'" + numberOfLocaFoodVendorsVisited + "', "
                + "'" + numberOfMarketsVisited + "', "
                + "'" + numberOfPublicToiletsVisited + "', "
                + "'" + numberOfBusStationsVisited + "', "
                + "'" + numberOfPrimarySchoolsVisited + "', "
                + "'" + numberOfSecondarySchoolsVisited + "', "
                + "'" + numberOfHospitalVisited + "', "
                + "'" + numberOfHealthCentersVisited + "', "
                + "'" + numberOfDispensariesVisited + "', "
                + "'" + numberOfOfficesVisited + "', "
                + "'" + numberOfUniversitiesCollegeVisited + "', "
                + "'" + numberOfFoodShopThatMetTheStandards + "', "
                + "'" + numberOfRestaurantsThatMetTheStandards + "', "
                + "'" + numberOfButcheriesThatMetTheStandards + "', "
                + "'" + numberOfBarAndClubsThatMetTheStandards + "', "
                + "'" + numberOfGuestHouseThatMetTheStandards + "', "
                + "'" + numberOfLocaFoodVendorsThatMetTheStandards + "', "
                + "'" + numberOfMarketsThatMetTheStandards + "', "
                + "'" + numberOfPublicToiletsThatMetTheStandards + "', "
                + "'" + numberOfBusStationsThatMetTheStandards + "', "
                + "'" + numberOfPrimarySchoolsThatMetTheStandards + "', "
                + "'" + numberOfSecondarySchoolsThatMetTheStandards + "', "
                + "'" + numberOfHospitalThatMetTheStandards + "', "
                + "'" + numberOfHealthCentersThatMetTheStandards + "', "
                + "'" + numberOfDispensariesThatMetTheStandards + "', "
                + "'" + numberOfOfficesThatMetTheStandards + "', "
                + "'" + numberOfUniversitiesCollegeThatMetTheStandards + "', "
                + "'" + numberOfInspectedAgricultureAreas + "', "
                + "'" + numberOfInspectedLivestockKeepingAreas + "', "
                + "'" + numberOfInspectedFishingAreas + "', "
                + "'" + numberOfInspectedIndustriesAreas + "', "
                + "'" + numberOfInspectedOfficesAreas + "', "
                + "'" + numberOfInspectedTransportationAreas + "', "
                + "'" + numberOfOtherInspectedAreas + "', "
                + "'" + numberOfAgricultureAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfLivestockKeepingAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfFishingAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfIndustriesAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfOfficesAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfTransportationAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfOtherAreasInspectedWithRiskIndicators + "', "
                + "'" + numberOfInspectedGrains + "', "
                + "'" + numberOfInspectedLegumes + "', "
                + "'" + numberOfInspectedMeat + "', "
                + "'" + numberOfInspectedFishing + "', "
                + "'" + numberOfInspectedAlcoholicBeverages + "', "
                + "'" + numberOfInspectedNonAlcoholicBeverages + "', "
                + "'" + numberOfGrainsDiscarded + "', "
                + "'" + numberOfLegumesDiscarded + "', "
                + "'" + numberOfMeatDiscarded + "', "
                + "'" + numberOfFishingDiscarded + "', "
                + "'" + numberOfAlcoholicBeverageDiscarded + "', "
                + "'" + numberOfNonAlcoholicBeverageDiscarded + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesRespiratoryDiseases + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesToxicChemicals + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesBurns + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesHearingLoss + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesEyeProblems + "', "
                + "'" + healthReportsAffectingPeopleInWorkplacesOtherEffects + "', "
                + "'" + amountOfSolidWasteGeneratedAnnuallyTons + "', "
                + "'" + amountOfSolidWasteDisposedAtADesignatedSiteAnnuallyTons + "', "
                + "'" + numberOfWasteCollectionEquipmentVehicles + "', "
                + "'" + numberOfWasteCollectionEquipmentTractors + "', "
                + "'" + numberOfWasteCollectionEquipmentCarts + "', "
                + "'" + numberOfWasteCollectionEquipmentWheelbarrows + "', "
                + "'" + numberOfWasteCollectionEquipmentOthers + "', "
                + "'" + numberOfAreasSprayedWithPesticidesPonds + "', "
                + "'" + numberOfAreasSprayedWithPesticidesCans + "', "
                + "'" + numberOfAreasSprayedWithPesticidesDrums + "', "
                + "'" + numberOfAreasSprayedWithPesticidesBarrels + "', "
                + "'" + numberOfAreasSprayedWithPesticidesCoconutShells + "', "
                + "'" + numberOfTimesSprayingWasDonePonds + "', "
                + "'" + numberOfTimesSprayingWasDoneCans + "', "
                + "'" + numberOfTimesSprayingWasDoneDrums + "', "
                + "'" + numberOfTimesSprayingWasDoneBarrels + "', "
                + "'" + numberOfTimesSprayingWasDoneCoconutShells + "', "
                + "'" + typesOfPesticidesUsedPonds + "', "
                + "'" + typesOfPesticidesUsedCans + "', "
                + "'" + typesOfPesticidesUsedDrums + "', "
                + "'" + typesOfPesticidesUsedBarrels + "', "
                + "'" + typesOfPesticidesUsedCoconutShells + "', "
                + "'" + amountOfPesticideUsedPonds + "', "
                + "'" + amountOfPesticideUsedCans + "', "
                + "'" + amountOfPesticideUsedDrums + "', "
                + "'" + amountOfPesticideUsedBarrels + "', "
                + "'" + amountOfPesticideUsedCoconutShells + "', "
                + "'" + numberOfHouseHolds + "', "
                + "'" + numberOfMaleCapableOfEngagingInEconomicActivities + "', "
                + "'" + numberOfFemaleCapableOfEngagingInEconomicActivities + "', "
                + "'" + numberOfMaleEngagedInEconomicActivities + "', "
                + "'" + numberOfFemaleEngagedInEconomicActivities + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseTapAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseRiverAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseShallowWellAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseWaterPondAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseSmallDamAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseLakeAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsMostCommonlyUseSpringAsSourcesOfWater + "', "
                + "'" + numberOfHouseholdsUsingElectricityAsSourceOfEnergyForLighting + "', "
                + "'" + numberOfHouseholdsUsingSolarAsSourceOfEnergyForLighting + "', "
                + "'" + numberOfHouseholdsUsingKerosineAsSourceOfEnergyForLighting + "', "
                + "'" + numberOfHouseholdsUsingKoroboiAsSourceOfEnergyForLighting + "', "
                + "'" + numberOfHouseholdsUsingOtherSourceOfEnergyForLighting + "', "
                + "'" + numberOfHouseholdsUsingElectricityAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHouseholdsUsingSolarAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHouseholdsUsingKerosineAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHouseholdsUsingGasAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHouseholdsUsingCharcoalAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHouseholdsUsingFirewoodAsSourceOfCookingEnergy + "', "
                + "'" + numberOfHealthCommitteeMembersForEffectiveCommitteeMeetings + "', "
                + "'" + numberOfCommitteeMembersAttendedFisrtQuarter + "', "
                + "'" + numberOfRegisteredAlternativeMedicineServiceProviders + "', "
                + "'" + numberOfRegisteredTraditionalMedicineServiceProviders + "', "
                + "'" + numberOfUnregisteredAlternativeMedicineServiceProviders + "', "
                + "'" + numberOfUnregisteredTraditionalMedicineServiceProviders + "', "
                + lastInteractedWith
                + ") ON CONFLICT (id) DO UPDATE SET "
                + "select_age_group = '" + selectAgeGroup + "', "
                + "number_of_male_by_age_group_under1 = '" + numberOfMaleByAgeGroupUnder1 + "', "
                + "number_of_female_by_age_group_under1 = '" + numberOfFemaleByAgeGroupUnder1 + "', "
                + "number_of_male_by_age_group_1_4 = '" + numberOfMaleByAgeGroup1_4 + "', "
                + "number_of_female_by_age_group_1_4 = '" + numberOfFemaleByAgeGroup1_4 + "', "
                + "number_of_male_by_age_group_5_14 = '" + numberOfMaleByAgeGroup5_14 + "', "
                + "number_of_female_by_age_group_5_14 = '" + numberOfFemaleByAgeGroup5_14 + "', "
                + "number_of_male_by_age_group_15_49 = '" + numberOfMaleByAgeGroup15_49 + "', "
                + "number_of_female_by_age_group_15_49 = '" + numberOfFemaleByAgeGroup15_49 + "', "
                + "number_of_male_by_age_group_50_59 = '" + numberOfMaleByAgeGroup50_59 + "', "
                + "number_of_female_by_age_group_50_59 = '" + numberOfFemaleByAgeGroup50_59 + "', "
                + "number_of_male_by_age_group_60_plus = '" + numberOfMaleByAgeGroup60Plus + "', "
                + "number_of_female_by_age_group_60_plus = '" + numberOfFemaleByAgeGroup60Plus + "', "
                + "number_of_house_hold_with_road_access = '" + numberOfHouseHoldWithRoadAccess + "', "
                + "number_of_house_holds_with_at_least_one_landline_or_mobile_phone = '" + numberOfHouseHoldsWithAtLeastOneLandlineOrMobilePhone + "', "
                + "number_of_house_hold_with_basic_nutrition_source_vegetable = '" + numberOfHouseHoldWithBasicNutritionSourceVegetable + "', "
                + "number_of_house_hold_with_basic_nutrition_source_fruit_trees = '" + numberOfHouseHoldWithBasicNutritionSourceFruitTrees + "', "
                + "number_of_house_hold_with_basic_nutrition_source_domestic_animal = '" + numberOfHouseHoldWithBasicNutritionSourceDomesticAnimal + "', "
                + "select_centers_category = '" + selectCentersCategory + "', "
                + "number_of_pre_schools_government = '" + numberOfPreSchoolsGovernment + "', "
                + "number_of_primary_schools_government = '" + numberOfPrimarySchoolsGovernment + "', "
                + "number_of_secondary_schools_government = '" + numberOfSecondarySchoolsGovernment + "', "
                + "number_of_universities_government = '" + numberOfUniversitiesGovernment + "', "
                + "number_of_special_needs_schools_government = '" + numberOfSpecialNeedsSchoolsGovernment + "', "
                + "number_of_dispensary_government = '" + numberOfDispensaryGovernment + "', "
                + "number_of_health_centers_government = '" + numberOfHealthCentersGovernment + "', "
                + "number_of_hospital_government = '" + numberOfHospitalGovernment + "', "
                + "number_of_special_clinics_government = '" + numberOfSpecialClinicsGovernment + "', "
                + "number_of_laboratory_government = '" + numberOfLaboratoryGovernment + "', "
                + "number_of_pharmacy_government = '" + numberOfPharmacyGovernment + "', "
                + "number_of_addo_government = '" + numberOfADDOGovernment + "', "
                + "number_of_maternity_home_government = '" + numberOfMaternityHomeGovernment + "', "
                + "number_of_orphan_care_centers_government = '" + numberOfOrphanCareCentersGovernment + "', "
                + "number_of_centers_for_children_with_disabilities_government = '" + numberOfCentersForChildrenWithDisabilitiesGovernment + "', "
                + "number_of_cbecdc_rehabilitation_centre_government = '" + numberOfCbecdcRehabilitationCentreGovernment + "', "
                + "number_of_day_care_centers_government = '" + numberOfDayCareCentersGovernment + "', "
                + "number_of_elderly_care_centers_government = '" + numberOfElderlyCareCentersGovernment + "', "
                + "number_of_pre_schools_faith_based_organisation = '" + numberOfPreSchoolsFaithBasedOrganisation + "', "
                + "number_of_primary_schools_faith_based_organisation = '" + numberOfPrimarySchoolsFaithBasedOrganisation + "', "
                + "number_of_secondary_schools_faith_based_organisation = '" + numberOfSecondarySchoolsFaithBasedOrganisation + "', "
                + "number_of_universities_faith_based_organisation = '" + numberOfUniversitiesFaithBasedOrganisation + "', "
                + "number_of_special_needs_schools_faith_based_organisation = '" + numberOfSpecialNeedsSchoolsFaithBasedOrganisation + "', "
                + "number_of_dispensary_faith_based_organisation = '" + numberOfDispensaryFaithBasedOrganisation + "', "
                + "number_of_health_centers_faith_based_organisation = '" + numberOfHealthCentersFaithBasedOrganisation + "', "
                + "number_of_hospital_faith_based_organisation = '" + numberOfHospitalFaithBasedOrganisation + "', "
                + "number_of_special_clinics_faith_based_organisation = '" + numberOfSpecialClinicsFaithBasedOrganisation + "', "
                + "number_of_laboratory_faith_based_organisation = '" + numberOfLaboratoryFaithBasedOrganisation + "', "
                + "number_of_pharmacy_faith_based_organisation = '" + numberOfPharmacyFaithBasedOrganisation + "', "
                + "number_of_addo_faith_based_organisation = '" + numberOfADDOFaithBasedOrganisation + "', "
                + "number_of_maternity_home_faith_based_organisation = '" + numberOfMaternityHomeFaithBasedOrganisation + "', "
                + "number_of_orphan_care_centers_faith_based_organisation = '" + numberOfOrphanCareCentersFaithBasedOrganisation + "', "
                + "number_of_centers_for_children_with_disabilities_faith_based_organisation = '" + numberOfCentersForChildrenWithDisabilitiesFaithBasedOrganisation + "', "
                + "number_of_cbecdc_rehabilitation_centre_faith_based_organisation = '" + numberOfCbecdcRehabilitationCentreFaithBasedOrganisation + "', "
                + "number_of_day_care_centers_faith_based_organisation = '" + numberOfDayCareCentersFaithBasedOrganisation + "', "
                + "number_of_elderly_care_centers_faith_based_organisation = '" + numberOfElderlyCareCentersFaithBasedOrganisation + "', "
                + "number_of_pre_schools_public = '" + numberOfPreSchoolsPublic + "', "
                + "number_of_primary_schools_public = '" + numberOfPrimarySchoolsPublic + "', "
                + "number_of_secondary_schools_public = '" + numberOfSecondarySchoolsPublic + "', "
                + "number_of_universities_public = '" + numberOfUniversitiesPublic + "', "
                + "number_of_special_needs_schools_public = '" + numberOfSpecialNeedsSchoolsPublic + "', "
                + "number_of_dispensary_public = '" + numberOfDispensaryPublic + "', "
                + "number_of_health_centers_public = '" + numberOfHealthCentersPublic + "', "
                + "number_of_hospital_public = '" + numberOfHospitalPublic + "', "
                + "number_of_special_clinics_public = '" + numberOfSpecialClinicsPublic + "', "
                + "number_of_laboratory_public = '" + numberOfLaboratoryPublic + "', "
                + "number_of_pharmacy_public = '" + numberOfPharmacyPublic + "', "
                + "number_of_addo_public = '" + numberOfADDOPublic + "', "
                + "number_of_maternity_home_public = '" + numberOfMaternityHomePublic + "', "
                + "number_of_orphan_care_centers_public = '" + numberOfOrphanCareCentersPublic + "', "
                + "number_of_centers_for_children_with_disabilities_public = '" + numberOfCentersForChildrenWithDisabilitiesPublic + "', "
                + "number_of_cbecdc_rehabilitation_centre_public = '" + numberOfCbecdcRehabilitationCentrePublic + "', "
                + "number_of_day_care_centers_public = '" + numberOfDayCareCentersPublic + "', "
                + "number_of_elderly_care_centers_public = '" + numberOfElderlyCareCentersPublic + "', "
                + "number_of_pre_schools_private = '" + numberOfPreSchoolsPrivate + "', "
                + "number_of_primary_schools_private = '" + numberOfPrimarySchoolsPrivate + "', "
                + "number_of_secondary_schools_private = '" + numberOfSecondarySchoolsPrivate + "', "
                + "number_of_universities_private = '" + numberOfUniversitiesPrivate + "', "
                + "number_of_special_needs_schools_private = '" + numberOfSpecialNeedsSchoolsPrivate + "', "
                + "number_of_dispensary_private = '" + numberOfDispensaryPrivate + "', "
                + "number_of_health_centers_private = '" + numberOfHealthCentersPrivate + "', "
                + "number_of_hospital_private = '" + numberOfHospitalPrivate + "', "
                + "number_of_special_clinics_private = '" + numberOfSpecialClinicsPrivate + "', "
                + "number_of_laboratory_private = '" + numberOfLaboratoryPrivate + "', "
                + "number_of_pharmacy_private = '" + numberOfPharmacyPrivate + "', "
                + "number_of_addo_private = '" + numberOfADDOPrivate + "', "
                + "number_of_maternity_home_private = '" + numberOfMaternityHomePrivate + "', "
                + "number_of_orphan_care_centers_private = '" + numberOfOrphanCareCentersPrivate + "', "
                + "number_of_centers_for_children_with_disabilities_private = '" + numberOfCentersForChildrenWithDisabilitiesPrivate + "', "
                + "number_of_cbecdc_rehabilitation_centre_private = '" + numberOfCbecdcRehabilitationCentrePrivate + "', "
                + "number_of_day_care_centers_private = '" + numberOfDayCareCentersPrivate + "', "
                + "number_of_elderly_care_centers_private = '" + numberOfElderlyCareCentersPrivate + "', "
                + "number_of_food_shop_visited = '" + numberOfFoodShopVisited + "', "
                + "number_of_restaurants_visited = '" + numberOfRestaurantsVisited + "', "
                + "number_of_butcheries_visited = '" + numberOfButcheriesVisited + "', "
                + "number_of_bar_and_clubs_visited = '" + numberOfBarAndClubsVisited + "', "
                + "number_of_guest_house_visited = '" + numberOfGuestHouseVisited + "', "
                + "number_of_loca_food_vendors_visited = '" + numberOfLocaFoodVendorsVisited + "', "
                + "number_of_markets_visited = '" + numberOfMarketsVisited + "', "
                + "number_of_public_toilets_visited = '" + numberOfPublicToiletsVisited + "', "
                + "number_of_bus_stations_visited = '" + numberOfBusStationsVisited + "', "
                + "number_of_primary_schools_visited = '" + numberOfPrimarySchoolsVisited + "', "
                + "number_of_secondary_schools_visited = '" + numberOfSecondarySchoolsVisited + "', "
                + "number_of_hospital_visited = '" + numberOfHospitalVisited + "', "
                + "number_of_health_centers_visited = '" + numberOfHealthCentersVisited + "', "
                + "number_of_dispensaries_visited = '" + numberOfDispensariesVisited + "', "
                + "number_of_offices_visited = '" + numberOfOfficesVisited + "', "
                + "number_of_universities_college_visited = '" + numberOfUniversitiesCollegeVisited + "', "
                + "number_of_food_shop_that_met_the_standards = '" + numberOfFoodShopThatMetTheStandards + "', "
                + "number_of_restaurants_that_met_the_standards = '" + numberOfRestaurantsThatMetTheStandards + "', "
                + "number_of_butcheries_that_met_the_standards = '" + numberOfButcheriesThatMetTheStandards + "', "
                + "number_of_bar_and_clubs_that_met_the_standards = '" + numberOfBarAndClubsThatMetTheStandards + "', "
                + "number_of_guest_house_that_met_the_standards = '" + numberOfGuestHouseThatMetTheStandards + "', "
                + "number_of_loca_food_vendors_that_met_the_standards = '" + numberOfLocaFoodVendorsThatMetTheStandards + "', "
                + "number_of_markets_that_met_the_standards = '" + numberOfMarketsThatMetTheStandards + "', "
                + "number_of_public_toilets_that_met_the_standards = '" + numberOfPublicToiletsThatMetTheStandards + "', "
                + "number_of_bus_stations_that_met_the_standards = '" + numberOfBusStationsThatMetTheStandards + "', "
                + "number_of_primary_schools_that_met_the_standards = '" + numberOfPrimarySchoolsThatMetTheStandards + "', "
                + "number_of_secondary_schools_that_met_the_standards = '" + numberOfSecondarySchoolsThatMetTheStandards + "', "
                + "number_of_hospital_that_met_the_standards = '" + numberOfHospitalThatMetTheStandards + "', "
                + "number_of_health_centers_that_met_the_standards = '" + numberOfHealthCentersThatMetTheStandards + "', "
                + "number_of_dispensaries_that_met_the_standards = '" + numberOfDispensariesThatMetTheStandards + "', "
                + "number_of_offices_that_met_the_standards = '" + numberOfOfficesThatMetTheStandards + "', "
                + "number_of_universities_college_that_met_the_standards = '" + numberOfUniversitiesCollegeThatMetTheStandards + "', "
                + "number_of_inspected_agriculture_areas = '" + numberOfInspectedAgricultureAreas + "', "
                + "number_of_inspected_livestock_keeping_areas = '" + numberOfInspectedLivestockKeepingAreas + "', "
                + "number_of_inspected_fishing_areas = '" + numberOfInspectedFishingAreas + "', "
                + "number_of_inspected_industries_areas = '" + numberOfInspectedIndustriesAreas + "', "
                + "number_of_inspected_offices_areas = '" + numberOfInspectedOfficesAreas + "', "
                + "number_of_inspected_transportation_areas = '" + numberOfInspectedTransportationAreas + "', "
                + "number_of_other_inspected_areas = '" + numberOfOtherInspectedAreas + "', "
                + "number_of_agriculture_areas_inspected_with_risk_indicators = '" + numberOfAgricultureAreasInspectedWithRiskIndicators + "', "
                + "number_of_livestock_keeping_areas_inspected_with_risk_indicators = '" + numberOfLivestockKeepingAreasInspectedWithRiskIndicators + "', "
                + "number_of_fishing_areas_inspected_with_risk_indicators = '" + numberOfFishingAreasInspectedWithRiskIndicators + "', "
                + "number_of_industries_areas_inspected_with_risk_indicators = '" + numberOfIndustriesAreasInspectedWithRiskIndicators + "', "
                + "number_of_offices_areas_inspected_with_risk_indicators = '" + numberOfOfficesAreasInspectedWithRiskIndicators + "', "
                + "number_of_transportation_areas_inspected_with_risk_indicators = '" + numberOfTransportationAreasInspectedWithRiskIndicators + "', "
                + "number_of_other_areas_inspected_with_risk_indicators = '" + numberOfOtherAreasInspectedWithRiskIndicators + "', "
                + "number_of_inspected_grains = '" + numberOfInspectedGrains + "', "
                + "number_of_inspected_legumes = '" + numberOfInspectedLegumes + "', "
                + "number_of_inspected_meat = '" + numberOfInspectedMeat + "', "
                + "number_of_inspected_fishing = '" + numberOfInspectedFishing + "', "
                + "number_of_inspected_alcoholic_beverages = '" + numberOfInspectedAlcoholicBeverages + "', "
                + "number_of_inspected_non_alcoholic_beverages = '" + numberOfInspectedNonAlcoholicBeverages + "', "
                + "number_of_grains_discarded = '" + numberOfGrainsDiscarded + "', "
                + "number_of_legumes_discarded = '" + numberOfLegumesDiscarded + "', "
                + "number_of_meat_discarded = '" + numberOfMeatDiscarded + "', "
                + "number_of_fishing_discarded = '" + numberOfFishingDiscarded + "', "
                + "number_of_alcoholic_beverage_discarded = '" + numberOfAlcoholicBeverageDiscarded + "', "
                + "number_of_non_alcoholic_beverage_discarded = '" + numberOfNonAlcoholicBeverageDiscarded + "', "
                + "health_reports_affecting_people_in_workplaces_respiratory_diseases = '" + healthReportsAffectingPeopleInWorkplacesRespiratoryDiseases + "', "
                + "health_reports_affecting_people_in_workplaces_toxic_chemicals = '" + healthReportsAffectingPeopleInWorkplacesToxicChemicals + "', "
                + "health_reports_affecting_people_in_workplaces_burns = '" + healthReportsAffectingPeopleInWorkplacesBurns + "', "
                + "health_reports_affecting_people_in_workplaces_hearing_loss = '" + healthReportsAffectingPeopleInWorkplacesHearingLoss + "', "
                + "health_reports_affecting_people_in_workplaces_eye_problems = '" + healthReportsAffectingPeopleInWorkplacesEyeProblems + "', "
                + "health_reports_affecting_people_in_workplaces_other_effects = '" + healthReportsAffectingPeopleInWorkplacesOtherEffects + "', "
                + "amount_of_solid_waste_generated_annually_tons = '" + amountOfSolidWasteGeneratedAnnuallyTons + "', "
                + "amount_of_solid_waste_disposed_at_a_designated_site_annually_tons = '" + amountOfSolidWasteDisposedAtADesignatedSiteAnnuallyTons + "', "
                + "number_of_waste_collection_equipment_vehicles = '" + numberOfWasteCollectionEquipmentVehicles + "', "
                + "number_of_waste_collection_equipment_tractors = '" + numberOfWasteCollectionEquipmentTractors + "', "
                + "number_of_waste_collection_equipment_carts = '" + numberOfWasteCollectionEquipmentCarts + "', "
                + "number_of_waste_collection_equipment_wheelbarrows = '" + numberOfWasteCollectionEquipmentWheelbarrows + "', "
                + "number_of_waste_collection_equipment_others = '" + numberOfWasteCollectionEquipmentOthers + "', "
                + "number_of_areas_sprayed_with_pesticides_ponds = '" + numberOfAreasSprayedWithPesticidesPonds + "', "
                + "number_of_areas_sprayed_with_pesticides_cans = '" + numberOfAreasSprayedWithPesticidesCans + "', "
                + "number_of_areas_sprayed_with_pesticides_drums = '" + numberOfAreasSprayedWithPesticidesDrums + "', "
                + "number_of_areas_sprayed_with_pesticides_barrels = '" + numberOfAreasSprayedWithPesticidesBarrels + "', "
                + "number_of_areas_sprayed_with_pesticides_coconut_shells = '" + numberOfAreasSprayedWithPesticidesCoconutShells + "', "
                + "number_of_times_spraying_was_done_ponds = '" + numberOfTimesSprayingWasDonePonds + "', "
                + "number_of_times_spraying_was_done_cans = '" + numberOfTimesSprayingWasDoneCans + "', "
                + "number_of_times_spraying_was_done_drums = '" + numberOfTimesSprayingWasDoneDrums + "', "
                + "number_of_times_spraying_was_done_barrels = '" + numberOfTimesSprayingWasDoneBarrels + "', "
                + "number_of_times_spraying_was_done_coconut_shells = '" + numberOfTimesSprayingWasDoneCoconutShells + "', "
                + "types_of_pesticides_used_ponds = '" + typesOfPesticidesUsedPonds + "', "
                + "types_of_pesticides_used_cans = '" + typesOfPesticidesUsedCans + "', "
                + "types_of_pesticides_used_drums = '" + typesOfPesticidesUsedDrums + "', "
                + "types_of_pesticides_used_barrels = '" + typesOfPesticidesUsedBarrels + "', "
                + "types_of_pesticides_used_coconut_shells = '" + typesOfPesticidesUsedCoconutShells + "', "
                + "amount_of_pesticide_used_ponds = '" + amountOfPesticideUsedPonds + "', "
                + "amount_of_pesticide_used_cans = '" + amountOfPesticideUsedCans + "', "
                + "amount_of_pesticide_used_drums = '" + amountOfPesticideUsedDrums + "', "
                + "amount_of_pesticide_used_barrels = '" + amountOfPesticideUsedBarrels + "', "
                + "amount_of_pesticide_used_coconut_shells = '" + amountOfPesticideUsedCoconutShells + "', "
                + "number_of_house_hold = '" + numberOfHouseHolds + "', "
                + "number_of_male_capable_of_engaging_in_economic_activities = '" + numberOfMaleCapableOfEngagingInEconomicActivities + "', "
                + "number_of_female_capable_of_engaging_in_economic_activities = '" + numberOfFemaleCapableOfEngagingInEconomicActivities + "', "
                + "number_of_male_engaged_in_economic_activities = '" + numberOfMaleEngagedInEconomicActivities + "', "
                + "number_of_female_engaged_in_economic_activities = '" + numberOfFemaleEngagedInEconomicActivities + "', "
                + "number_of_households_most_commonly_use_tap_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseTapAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_river_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseRiverAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_shallow_well_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseShallowWellAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_water_pond_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseWaterPondAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_small_dam_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseSmallDamAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_lake_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseLakeAsSourcesOfWater + "', "
                + "number_of_households_most_commonly_use_spring_as_sources_of_water = '" + numberOfHouseholdsMostCommonlyUseSpringAsSourcesOfWater + "', "
                + "number_of_households_using_electricity_as_source_of_energy_for_lighting = '" + numberOfHouseholdsUsingElectricityAsSourceOfEnergyForLighting + "', "
                + "number_of_households_using_solar_as_source_of_energy_for_lighting = '" + numberOfHouseholdsUsingSolarAsSourceOfEnergyForLighting + "', "
                + "number_of_households_using_kerosine_as_source_of_energy_for_lighting = '" + numberOfHouseholdsUsingKerosineAsSourceOfEnergyForLighting + "', "
                + "number_of_households_using_koroboi_as_source_of_energy_for_lighting = '" + numberOfHouseholdsUsingKoroboiAsSourceOfEnergyForLighting + "', "
                + "number_of_households_using_other_source_of_energy_for_lighting = '" + numberOfHouseholdsUsingOtherSourceOfEnergyForLighting + "', "
                + "number_of_households_using_electricity_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingElectricityAsSourceOfCookingEnergy + "', "
                + "number_of_households_using_solar_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingSolarAsSourceOfCookingEnergy + "', "
                + "number_of_households_using_kerosine_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingKerosineAsSourceOfCookingEnergy + "', "
                + "number_of_households_using_gas_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingGasAsSourceOfCookingEnergy + "', "
                + "number_of_households_using_charcoal_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingCharcoalAsSourceOfCookingEnergy + "', "
                + "number_of_households_using_firewood_as_source_of_cooking_energy = '" + numberOfHouseholdsUsingFirewoodAsSourceOfCookingEnergy + "', "
                + "number_of_health_committee_members_for_effective_committee_meetings = '" + numberOfHealthCommitteeMembersForEffectiveCommitteeMeetings + "', "
                + "number_of_committee_members_attended_fisrt_quarter = '" + numberOfCommitteeMembersAttendedFisrtQuarter + "', "
                + "number_of_registered_alternative_medicine_service_providers = '" + numberOfRegisteredAlternativeMedicineServiceProviders + "', "
                + "number_of_registered_traditional_medicine_service_providers = '" + numberOfRegisteredTraditionalMedicineServiceProviders + "', "
                + "number_of_unregistered_alternative_medicine_service_providers = '" + numberOfUnregisteredAlternativeMedicineServiceProviders + "', "
                + "number_of_unregistered_traditional_medicine_service_providers = '" + numberOfUnregisteredTraditionalMedicineServiceProviders + "', "
                + "last_interacted_with = " + lastInteractedWith;
        updateDB(sql);
    }

    public static List<HpsAnnualCensusRegisterModel> getHpsAnnualCensusRegisters() {
        String sql = "SELECT * FROM " + Constants.TABLES.HPS_ANNUAL_CENSUS_REGISTER;

        @SuppressLint("Range")
        DataMap<HpsAnnualCensusRegisterModel> dataMap = cursor -> {
            HpsAnnualCensusRegisterModel model = new HpsAnnualCensusRegisterModel();
            model.setBaseEntityId(cursor.getString(cursor.getColumnIndex("base_entity_id")));
            model.setYear(cursor.getString(cursor.getColumnIndex("year")));
            model.setSelectAgeGroup(cursor.getString(cursor.getColumnIndex("select_age_group")));
            model.setNumberOfMaleByAgeGroupUnder1(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_under1")));
            model.setNumberOfFemaleByAgeGroupUnder1(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_under1")));
            model.setNumberOfMaleByAgeGroup1_4(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_1_4")));
            model.setNumberOfFemaleByAgeGroup1_4(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_1_4")));
            model.setNumberOfMaleByAgeGroup5_14(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_5_14")));
            model.setNumberOfFemaleByAgeGroup5_14(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_5_14")));
            model.setNumberOfMaleByAgeGroup15_49(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_15_49")));
            model.setNumberOfFemaleByAgeGroup15_49(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_15_49")));
            model.setNumberOfMaleByAgeGroup50_59(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_50_59")));
            model.setNumberOfFemaleByAgeGroup50_59(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_50_59")));
            model.setNumberOfMaleByAgeGroup60Plus(cursor.getInt(cursor.getColumnIndex("number_of_male_by_age_group_60_plus")));
            model.setNumberOfFemaleByAgeGroup60Plus(cursor.getInt(cursor.getColumnIndex("number_of_female_by_age_group_60_plus")));
            model.setNumberOfHouseHoldWithRoadAccess(cursor.getInt(cursor.getColumnIndex("number_of_house_hold_with_road_access")));
            model.setNumberOfHouseHoldsWithAtLeastOneLandlineOrMobilePhone(
                    cursor.getInt(cursor.getColumnIndex("number_of_house_holds_with_at_least_one_landline_or_mobile_phone"))
            );
            model.setNumberOfHouseHoldWithBasicNutritionSourceVegetable(
                    cursor.getInt(cursor.getColumnIndex("number_of_house_hold_with_basic_nutrition_source_vegetable"))
            );
            model.setNumberOfHouseHoldWithBasicNutritionSourceFruitTrees(
                    cursor.getInt(cursor.getColumnIndex("number_of_house_hold_with_basic_nutrition_source_fruit_trees"))
            );
            model.setNumberOfHouseHoldWithBasicNutritionSourceDomesticAnimal(
                    cursor.getInt(cursor.getColumnIndex("number_of_house_hold_with_basic_nutrition_source_domestic_animal"))
            );
            model.setSelectCentersCategory(cursor.getString(cursor.getColumnIndex("select_centers_category")));
            model.setNumberOfPreSchoolsGovernment(cursor.getInt(cursor.getColumnIndex("number_of_pre_schools_government")));
            model.setNumberOfPrimarySchoolsGovernment(cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_government")));
            model.setNumberOfSecondarySchoolsGovernment(cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_government")));
            model.setNumberOfUniversitiesGovernment(cursor.getInt(cursor.getColumnIndex("number_of_universities_government")));
            model.setNumberOfSpecialNeedsSchoolsGovernment(cursor.getInt(cursor.getColumnIndex("number_of_special_needs_schools_government")));
            model.setNumberOfDispensaryGovernment(cursor.getInt(cursor.getColumnIndex("number_of_dispensary_government")));
            model.setNumberOfHealthCentersGovernment(cursor.getInt(cursor.getColumnIndex("number_of_health_centers_government")));
            model.setNumberOfHospitalGovernment(cursor.getInt(cursor.getColumnIndex("number_of_hospital_government")));
            model.setNumberOfSpecialClinicsGovernment(cursor.getInt(cursor.getColumnIndex("number_of_special_clinics_government")));
            model.setNumberOfLaboratoryGovernment(cursor.getInt(cursor.getColumnIndex("number_of_laboratory_government")));
            model.setNumberOfPharmacyGovernment(cursor.getInt(cursor.getColumnIndex("number_of_pharmacy_government")));
            model.setNumberOfADDOGovernment(cursor.getInt(cursor.getColumnIndex("number_of_addo_government")));
            model.setNumberOfMaternityHomeGovernment(cursor.getInt(cursor.getColumnIndex("number_of_maternity_home_government")));
            model.setNumberOfOrphanCareCentersGovernment(cursor.getInt(cursor.getColumnIndex("number_of_orphan_care_centers_government")));
            model.setNumberOfCentersForChildrenWithDisabilitiesGovernment(
                    cursor.getInt(cursor.getColumnIndex("number_of_centers_for_children_with_disabilities_government"))
            );
            model.setNumberOfCbecdcRehabilitationCentreGovernment(
                    cursor.getInt(cursor.getColumnIndex("number_of_cbecdc_rehabilitation_centre_government"))
            );
            model.setNumberOfDayCareCentersGovernment(cursor.getInt(cursor.getColumnIndex("number_of_day_care_centers_government")));
            model.setNumberOfElderlyCareCentersGovernment(cursor.getInt(cursor.getColumnIndex("number_of_elderly_care_centers_government")));
            model.setNumberOfPreSchoolsFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_pre_schools_faith_based_organisation"))
            );
            model.setNumberOfPrimarySchoolsFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_faith_based_organisation"))
            );
            model.setNumberOfSecondarySchoolsFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_faith_based_organisation"))
            );
            model.setNumberOfUniversitiesFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_universities_faith_based_organisation"))
            );
            model.setNumberOfSpecialNeedsSchoolsFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_special_needs_schools_faith_based_organisation"))
            );
            model.setNumberOfDispensaryFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_dispensary_faith_based_organisation"))
            );
            model.setNumberOfHealthCentersFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_health_centers_faith_based_organisation"))
            );
            model.setNumberOfHospitalFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_hospital_faith_based_organisation"))
            );
            model.setNumberOfSpecialClinicsFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_special_clinics_faith_based_organisation"))
            );
            model.setNumberOfLaboratoryFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_laboratory_faith_based_organisation"))
            );
            model.setNumberOfPharmacyFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_pharmacy_faith_based_organisation"))
            );
            model.setNumberOfADDOFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_addo_faith_based_organisation"))
            );
            model.setNumberOfMaternityHomeFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_maternity_home_faith_based_organisation"))
            );
            model.setNumberOfOrphanCareCentersFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_orphan_care_centers_faith_based_organisation"))
            );
            model.setNumberOfCentersForChildrenWithDisabilitiesFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_centers_for_children_with_disabilities_faith_based_organisation"))
            );
            model.setNumberOfCbecdcRehabilitationCentreFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_cbecdc_rehabilitation_centre_faith_based_organisation"))
            );
            model.setNumberOfDayCareCentersFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_day_care_centers_faith_based_organisation"))
            );
            model.setNumberOfElderlyCareCentersFaithBasedOrganisation(
                    cursor.getInt(cursor.getColumnIndex("number_of_elderly_care_centers_faith_based_organisation"))
            );
            model.setNumberOfPreSchoolsPublic(cursor.getInt(cursor.getColumnIndex("number_of_pre_schools_public")));
            model.setNumberOfPrimarySchoolsPublic(cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_public")));
            model.setNumberOfSecondarySchoolsPublic(cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_public")));
            model.setNumberOfUniversitiesPublic(cursor.getInt(cursor.getColumnIndex("number_of_universities_public")));
            model.setNumberOfSpecialNeedsSchoolsPublic(cursor.getInt(cursor.getColumnIndex("number_of_special_needs_schools_public")));
            model.setNumberOfDispensaryPublic(cursor.getInt(cursor.getColumnIndex("number_of_dispensary_public")));
            model.setNumberOfHealthCentersPublic(cursor.getInt(cursor.getColumnIndex("number_of_health_centers_public")));
            model.setNumberOfHospitalPublic(cursor.getInt(cursor.getColumnIndex("number_of_hospital_public")));
            model.setNumberOfSpecialClinicsPublic(cursor.getInt(cursor.getColumnIndex("number_of_special_clinics_public")));
            model.setNumberOfLaboratoryPublic(cursor.getInt(cursor.getColumnIndex("number_of_laboratory_public")));
            model.setNumberOfPharmacyPublic(cursor.getInt(cursor.getColumnIndex("number_of_pharmacy_public")));
            model.setNumberOfADDOPublic(cursor.getInt(cursor.getColumnIndex("number_of_addo_public")));
            model.setNumberOfMaternityHomePublic(cursor.getInt(cursor.getColumnIndex("number_of_maternity_home_public")));
            model.setNumberOfOrphanCareCentersPublic(cursor.getInt(cursor.getColumnIndex("number_of_orphan_care_centers_public")));
            model.setNumberOfCentersForChildrenWithDisabilitiesPublic(
                    cursor.getInt(cursor.getColumnIndex("number_of_centers_for_children_with_disabilities_public"))
            );
            model.setNumberOfCbecdcRehabilitationCentrePublic(
                    cursor.getInt(cursor.getColumnIndex("number_of_cbecdc_rehabilitation_centre_public"))
            );
            model.setNumberOfDayCareCentersPublic(cursor.getInt(cursor.getColumnIndex("number_of_day_care_centers_public")));
            model.setNumberOfElderlyCareCentersPublic(cursor.getInt(cursor.getColumnIndex("number_of_elderly_care_centers_public")));
            model.setNumberOfPreSchoolsPrivate(cursor.getInt(cursor.getColumnIndex("number_of_pre_schools_private")));
            model.setNumberOfPrimarySchoolsPrivate(cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_private")));
            model.setNumberOfSecondarySchoolsPrivate(cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_private")));
            model.setNumberOfUniversitiesPrivate(cursor.getInt(cursor.getColumnIndex("number_of_universities_private")));
            model.setNumberOfSpecialNeedsSchoolsPrivate(cursor.getInt(cursor.getColumnIndex("number_of_special_needs_schools_private")));
            model.setNumberOfDispensaryPrivate(cursor.getInt(cursor.getColumnIndex("number_of_dispensary_private")));
            model.setNumberOfHealthCentersPrivate(cursor.getInt(cursor.getColumnIndex("number_of_health_centers_private")));
            model.setNumberOfHospitalPrivate(cursor.getInt(cursor.getColumnIndex("number_of_hospital_private")));
            model.setNumberOfSpecialClinicsPrivate(cursor.getInt(cursor.getColumnIndex("number_of_special_clinics_private")));
            model.setNumberOfLaboratoryPrivate(cursor.getInt(cursor.getColumnIndex("number_of_laboratory_private")));
            model.setNumberOfPharmacyPrivate(cursor.getInt(cursor.getColumnIndex("number_of_pharmacy_private")));
            model.setNumberOfADDOPrivate(cursor.getInt(cursor.getColumnIndex("number_of_addo_private")));
            model.setNumberOfMaternityHomePrivate(cursor.getInt(cursor.getColumnIndex("number_of_maternity_home_private")));
            model.setNumberOfOrphanCareCentersPrivate(cursor.getInt(cursor.getColumnIndex("number_of_orphan_care_centers_private")));
            model.setNumberOfCentersForChildrenWithDisabilitiesPrivate(
                    cursor.getInt(cursor.getColumnIndex("number_of_centers_for_children_with_disabilities_private"))
            );
            model.setNumberOfCbecdcRehabilitationCentrePrivate(
                    cursor.getInt(cursor.getColumnIndex("number_of_cbecdc_rehabilitation_centre_private"))
            );
            model.setNumberOfDayCareCentersPrivate(cursor.getInt(cursor.getColumnIndex("number_of_day_care_centers_private")));
            model.setNumberOfElderlyCareCentersPrivate(cursor.getInt(cursor.getColumnIndex("number_of_elderly_care_centers_private")));
            model.setNumberOfFoodShopVisited(cursor.getInt(cursor.getColumnIndex("number_of_food_shop_visited")));
            model.setNumberOfRestaurantsVisited(cursor.getInt(cursor.getColumnIndex("number_of_restaurants_visited")));
            model.setNumberOfButcheriesVisited(cursor.getInt(cursor.getColumnIndex("number_of_butcheries_visited")));
            model.setNumberOfBarAndClubsVisited(cursor.getInt(cursor.getColumnIndex("number_of_bar_and_clubs_visited")));
            model.setNumberOfGuestHouseVisited(cursor.getInt(cursor.getColumnIndex("number_of_guest_house_visited")));
            model.setNumberOfLocaFoodVendorsVisited(cursor.getInt(cursor.getColumnIndex("number_of_loca_food_vendors_visited")));
            model.setNumberOfMarketsVisited(cursor.getInt(cursor.getColumnIndex("number_of_markets_visited")));
            model.setNumberOfPublicToiletsVisited(cursor.getInt(cursor.getColumnIndex("number_of_public_toilets_visited")));
            model.setNumberOfBusStationsVisited(cursor.getInt(cursor.getColumnIndex("number_of_bus_stations_visited")));
            model.setNumberOfPrimarySchoolsVisited(cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_visited")));
            model.setNumberOfSecondarySchoolsVisited(cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_visited")));
            model.setNumberOfHospitalVisited(cursor.getInt(cursor.getColumnIndex("number_of_hospital_visited")));
            model.setNumberOfHealthCentersVisited(cursor.getInt(cursor.getColumnIndex("number_of_health_centers_visited")));
            model.setNumberOfDispensariesVisited(cursor.getInt(cursor.getColumnIndex("number_of_dispensaries_visited")));
            model.setNumberOfOfficesVisited(cursor.getInt(cursor.getColumnIndex("number_of_offices_visited")));
            model.setNumberOfUniversitiesCollegeVisited(cursor.getInt(cursor.getColumnIndex("number_of_universities_college_visited")));
            model.setNumberOfFoodShopThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_food_shop_that_met_the_standards")));
            model.setNumberOfRestaurantsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_restaurants_that_met_the_standards")));
            model.setNumberOfButcheriesThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_butcheries_that_met_the_standards")));
            model.setNumberOfBarAndClubsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_bar_and_clubs_that_met_the_standards")));
            model.setNumberOfGuestHouseThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_guest_house_that_met_the_standards")));
            model.setNumberOfLocaFoodVendorsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_loca_food_vendors_that_met_the_standards")));
            model.setNumberOfMarketsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_markets_that_met_the_standards")));
            model.setNumberOfPublicToiletsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_public_toilets_that_met_the_standards")));
            model.setNumberOfBusStationsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_bus_stations_that_met_the_standards")));
            model.setNumberOfPrimarySchoolsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_primary_schools_that_met_the_standards")));
            model.setNumberOfSecondarySchoolsThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_secondary_schools_that_met_the_standards")));
            model.setNumberOfHospitalThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_hospital_that_met_the_standards")));
            model.setNumberOfHealthCentersThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_health_centers_that_met_the_standards")));
            model.setNumberOfDispensariesThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_dispensaries_that_met_the_standards")));
            model.setNumberOfOfficesThatMetTheStandards(cursor.getInt(cursor.getColumnIndex("number_of_offices_that_met_the_standards")));
            model.setNumberOfUniversitiesCollegeThatMetTheStandards(
                    cursor.getInt(cursor.getColumnIndex("number_of_universities_college_that_met_the_standards"))
            );
            model.setNumberOfInspectedAgricultureAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_agriculture_areas")));
            model.setNumberOfInspectedLivestockKeepingAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_livestock_keeping_areas")));
            model.setNumberOfInspectedFishingAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_fishing_areas")));
            model.setNumberOfInspectedIndustriesAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_industries_areas")));
            model.setNumberOfInspectedOfficesAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_offices_areas")));
            model.setNumberOfInspectedTransportationAreas(cursor.getInt(cursor.getColumnIndex("number_of_inspected_transportation_areas")));
            model.setNumberOfOtherInspectedAreas(cursor.getInt(cursor.getColumnIndex("number_of_other_inspected_areas")));
            model.setNumberOfAgricultureAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_agriculture_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfLivestockKeepingAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_livestock_keeping_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfFishingAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_fishing_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfIndustriesAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_industries_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfOfficesAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_offices_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfTransportationAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_transportation_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfOtherAreasInspectedWithRiskIndicators(
                    cursor.getInt(cursor.getColumnIndex("number_of_other_areas_inspected_with_risk_indicators"))
            );
            model.setNumberOfInspectedGrains(cursor.getInt(cursor.getColumnIndex("number_of_inspected_grains")));
            model.setNumberOfInspectedLegumes(cursor.getInt(cursor.getColumnIndex("number_of_inspected_legumes")));
            model.setNumberOfInspectedMeat(cursor.getInt(cursor.getColumnIndex("number_of_inspected_meat")));
            model.setNumberOfInspectedFishing(cursor.getInt(cursor.getColumnIndex("number_of_inspected_fishing")));
            model.setNumberOfInspectedAlcoholicBeverages(
                    cursor.getInt(cursor.getColumnIndex("number_of_inspected_alcoholic_beverages"))
            );
            model.setNumberOfInspectedNonAlcoholicBeverages(
                    cursor.getInt(cursor.getColumnIndex("number_of_inspected_non_alcoholic_beverages"))
            );
            model.setNumberOfGrainsDiscarded(cursor.getInt(cursor.getColumnIndex("number_of_grains_discarded")));
            model.setNumberOfLegumesDiscarded(cursor.getInt(cursor.getColumnIndex("number_of_legumes_discarded")));
            model.setNumberOfMeatDiscarded(cursor.getInt(cursor.getColumnIndex("number_of_meat_discarded")));
            model.setNumberOfFishingDiscarded(cursor.getInt(cursor.getColumnIndex("number_of_fishing_discarded")));
            model.setNumberOfAlcoholicBeverageDiscarded(
                    cursor.getInt(cursor.getColumnIndex("number_of_alcoholic_beverage_discarded"))
            );
            model.setNumberOfNonAlcoholicBeverageDiscarded(
                    cursor.getInt(cursor.getColumnIndex("number_of_non_alcoholic_beverage_discarded"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesRespiratoryDiseases(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_respiratory_diseases"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesToxicChemicals(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_toxic_chemicals"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesBurns(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_burns"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesHearingLoss(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_hearing_loss"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesEyeProblems(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_eye_problems"))
            );
            model.setHealthReportsAffectingPeopleInWorkplacesOtherEffects(
                    cursor.getInt(cursor.getColumnIndex("health_reports_affecting_people_in_workplaces_other_effects"))
            );
            model.setAmountOfSolidWasteGeneratedAnnuallyTons(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_solid_waste_generated_annually_tons"))
            );
            model.setAmountOfSolidWasteDisposedAtADesignatedSiteAnnuallyTons(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_solid_waste_disposed_at_a_designated_site_annually_tons"))
            );
            model.setNumberOfWasteCollectionEquipmentVehicles(
                    cursor.getInt(cursor.getColumnIndex("number_of_waste_collection_equipment_vehicles"))
            );
            model.setNumberOfWasteCollectionEquipmentTractors(
                    cursor.getInt(cursor.getColumnIndex("number_of_waste_collection_equipment_tractors"))
            );
            model.setNumberOfWasteCollectionEquipmentCarts(
                    cursor.getInt(cursor.getColumnIndex("number_of_waste_collection_equipment_carts"))
            );
            model.setNumberOfWasteCollectionEquipmentWheelbarrows(
                    cursor.getInt(cursor.getColumnIndex("number_of_waste_collection_equipment_wheelbarrows"))
            );
            model.setNumberOfWasteCollectionEquipmentOthers(
                    cursor.getInt(cursor.getColumnIndex("number_of_waste_collection_equipment_others"))
            );
            model.setNumberOfAreasSprayedWithPesticidesPonds(
                    cursor.getInt(cursor.getColumnIndex("number_of_areas_sprayed_with_pesticides_ponds"))
            );
            model.setNumberOfAreasSprayedWithPesticidesCans(
                    cursor.getInt(cursor.getColumnIndex("number_of_areas_sprayed_with_pesticides_cans"))
            );
            model.setNumberOfAreasSprayedWithPesticidesDrums(
                    cursor.getInt(cursor.getColumnIndex("number_of_areas_sprayed_with_pesticides_drums"))
            );
            model.setNumberOfAreasSprayedWithPesticidesBarrels(
                    cursor.getInt(cursor.getColumnIndex("number_of_areas_sprayed_with_pesticides_barrels"))
            );
            model.setNumberOfAreasSprayedWithPesticidesCoconutShells(
                    cursor.getInt(cursor.getColumnIndex("number_of_areas_sprayed_with_pesticides_coconut_shells"))
            );
            model.setNumberOfTimesSprayingWasDonePonds(
                    cursor.getInt(cursor.getColumnIndex("number_of_times_spraying_was_done_ponds"))
            );
            model.setNumberOfTimesSprayingWasDoneCans(
                    cursor.getInt(cursor.getColumnIndex("number_of_times_spraying_was_done_cans"))
            );
            model.setNumberOfTimesSprayingWasDoneDrums(
                    cursor.getInt(cursor.getColumnIndex("number_of_times_spraying_was_done_drums"))
            );
            model.setNumberOfTimesSprayingWasDoneBarrels(
                    cursor.getInt(cursor.getColumnIndex("number_of_times_spraying_was_done_barrels"))
            );
            model.setNumberOfTimesSprayingWasDoneCoconutShells(
                    cursor.getInt(cursor.getColumnIndex("number_of_times_spraying_was_done_coconut_shells"))
            );
            model.setTypesOfPesticidesUsedPonds(cursor.getString(cursor.getColumnIndex("types_of_pesticides_used_ponds")));
            model.setTypesOfPesticidesUsedCans(cursor.getString(cursor.getColumnIndex("types_of_pesticides_used_cans")));
            model.setTypesOfPesticidesUsedDrums(cursor.getString(cursor.getColumnIndex("types_of_pesticides_used_drums")));
            model.setTypesOfPesticidesUsedBarrels(cursor.getString(cursor.getColumnIndex("types_of_pesticides_used_barrels")));
            model.setTypesOfPesticidesUsedCoconutShells(
                    cursor.getString(cursor.getColumnIndex("types_of_pesticides_used_coconut_shells"))
            );
            model.setAmountOfPesticideUsedPonds(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_pesticide_used_ponds"))
            );
            model.setAmountOfPesticideUsedCans(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_pesticide_used_cans"))
            );
            model.setAmountOfPesticideUsedDrums(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_pesticide_used_drums"))
            );
            model.setAmountOfPesticideUsedBarrels(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_pesticide_used_barrels"))
            );
            model.setAmountOfPesticideUsedCoconutShells(
                    cursor.getDouble(cursor.getColumnIndex("amount_of_pesticide_used_coconut_shells"))
            );
            return model;
        };

        List<HpsAnnualCensusRegisterModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) return null;
        return res;
    }

    public static boolean individualClientHasAnyVisit(String baseEntityID) {
        String sql = "SELECT COUNT (*) AS count FROM " + Constants.TABLES.HPS_CLIENT_SERVICES + " cs " +
                "WHERE cs.entity_id = '" + baseEntityID + "' ";
        DataMap<Integer> countMap = cursor -> getCursorIntValue(cursor, "count");
        List<Integer> countResults = readData(sql, countMap);
        return !countResults.isEmpty() && countResults.get(0) > 0;
    }

    public static boolean houseHoldClientHasAnyVisit(String baseEntityID) {
        String sql = "SELECT COUNT (*) AS count FROM " + Constants.TABLES.HPS_HOUSEHOLD_SERVICES + " hs " +
                "WHERE hs.entity_id = '" + baseEntityID + "' ";
        DataMap<Integer> countMap = cursor -> getCursorIntValue(cursor, "count");
        List<Integer> countResults = readData(sql, countMap);
        return !countResults.isEmpty() && countResults.get(0) > 0;
    }


}

