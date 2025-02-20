package org.smartregister.chw.hps.dao;

import android.annotation.SuppressLint;

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
                "WHERE p.base_entity_id = '" + baseEntityID + "' AND p.is_closed = 0 ";

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
                "inner join "+Constants.TABLES.HPS_HOUSEHOLD_REGISTER+" mr on mr.base_entity_id = m.base_entity_id " +
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
            model.setDateOfGathering(cursor.getString(cursor.getColumnIndex("mobilization_date")));
            model.setTheMethodOfEducationAndAwarenessUsed(
                    cursor.getString(cursor.getColumnIndex("the_method_of_education_and_awareness_used"))
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
        String sql = "SELECT * FROM " + Constants.TABLES.HPS_DEATH_REGISTER;

        @SuppressLint("Range")
        DataMap<HpsDeathRegisterModel> dataMap = cursor -> {
            HpsDeathRegisterModel model = new HpsDeathRegisterModel();
            model.setDeathId(cursor.getString(cursor.getColumnIndex("death_id")));
            model.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
            model.setMiddleName(cursor.getString(cursor.getColumnIndex("middle_name")));
            model.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
            model.setDob(cursor.getString(cursor.getColumnIndex("dob")));
            model.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            model.setCauseOfDeath(cursor.getString(cursor.getColumnIndex("cause_of_death")));
            return model;
        };

        List<HpsDeathRegisterModel> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) return null;
        return res;
    }

}

