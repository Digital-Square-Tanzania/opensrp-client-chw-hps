package org.smartregister.chw.hps.actionhelper;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.util.JsonFormUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HpsPreventiveServicesActionHelperTest {

    @Test
    public void getPreProcessedShouldOnlyKeepUnderFiveOptions() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(4, "male");

        assertEquals(4, processedJson.getJSONObject("global").getInt("age"));
        assertEquals("male", processedJson.getJSONObject("global").getString("gender"));
        assertEquals(Arrays.asList(
                "vitamin_a_supplements",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    @Test
    public void getPreProcessedShouldOnlyKeepFiveToSeventeenOptions() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(17, "female");

        assertEquals(Arrays.asList(
                "counselling",
                "family_planning_pills",
                "iron_folic_tablets",
                "deworming_medication",
                "male_condoms",
                "female_condoms",
                "post_exposure_prophylaxis",
                "pre_exposure_prophylaxis",
                "fortified_foods",
                "distribution_mosquito_nets",
                "distribution_water_purification_tablets",
                "psychological_counselling",
                "preliminary_screening_tb",
                "gender_based_violence_screening",
                "collection_sputum_stool_samples",
                "dispensing_arv_medication",
                "preventive_medication_ntd",
                "first_aid_services",
                "environment_hygiene_sanitation",
                "food_hygiene_safety",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    @Test
    public void getPreProcessedShouldHideFemaleOnlyOptionsForMaleAdolescentClients() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(17, "male");

        assertEquals(Arrays.asList(
                "counselling",
                "deworming_medication",
                "male_condoms",
                "female_condoms",
                "post_exposure_prophylaxis",
                "pre_exposure_prophylaxis",
                "fortified_foods",
                "distribution_mosquito_nets",
                "distribution_water_purification_tablets",
                "psychological_counselling",
                "preliminary_screening_tb",
                "gender_based_violence_screening",
                "collection_sputum_stool_samples",
                "dispensing_arv_medication",
                "preventive_medication_ntd",
                "first_aid_services",
                "environment_hygiene_sanitation",
                "food_hygiene_safety",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    @Test
    public void getPreProcessedShouldHideFemaleOnlyOptionsForGirlsBelowTwelve() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(11, "female");

        assertEquals(Arrays.asList(
                "counselling",
                "deworming_medication",
                "male_condoms",
                "female_condoms",
                "post_exposure_prophylaxis",
                "pre_exposure_prophylaxis",
                "fortified_foods",
                "distribution_mosquito_nets",
                "distribution_water_purification_tablets",
                "psychological_counselling",
                "preliminary_screening_tb",
                "gender_based_violence_screening",
                "collection_sputum_stool_samples",
                "dispensing_arv_medication",
                "preventive_medication_ntd",
                "first_aid_services",
                "environment_hygiene_sanitation",
                "food_hygiene_safety",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    @Test
    public void getPreProcessedShouldOnlyKeepAdultOptions() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(18, "female");

        assertEquals(Arrays.asList(
                "counselling",
                "family_planning_pills",
                "iron_folic_tablets",
                "deworming_medication",
                "male_condoms",
                "female_condoms",
                "post_exposure_prophylaxis",
                "pre_exposure_prophylaxis",
                "fortified_foods",
                "distribution_mosquito_nets",
                "hiv_self_test_kits",
                "distribution_water_purification_tablets",
                "psychological_counselling",
                "preliminary_screening_tb",
                "gender_based_violence_screening",
                "collection_sputum_stool_samples",
                "dispensing_arv_medication",
                "preventive_medication_ntd",
                "first_aid_services",
                "environment_hygiene_sanitation",
                "food_hygiene_safety",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    @Test
    public void getPreProcessedShouldHideFemaleOnlyOptionsForAdultMaleClients() throws Exception {
        JSONObject processedJson = getPreProcessedJsonForAge(18, "male");

        assertEquals(Arrays.asList(
                "counselling",
                "deworming_medication",
                "male_condoms",
                "female_condoms",
                "post_exposure_prophylaxis",
                "pre_exposure_prophylaxis",
                "fortified_foods",
                "distribution_mosquito_nets",
                "hiv_self_test_kits",
                "distribution_water_purification_tablets",
                "psychological_counselling",
                "preliminary_screening_tb",
                "gender_based_violence_screening",
                "collection_sputum_stool_samples",
                "dispensing_arv_medication",
                "preventive_medication_ntd",
                "first_aid_services",
                "environment_hygiene_sanitation",
                "food_hygiene_safety",
                "other_services"
        ), getPreventiveServiceOptionKeys(processedJson));
    }

    private JSONObject getPreProcessedJsonForAge(int age, String gender) throws Exception {
        HpsPreventiveServicesActionHelper helper = new HpsPreventiveServicesActionHelper(null, createMember(age, gender));
        helper.onJsonFormLoaded(readPreventiveServicesForm(), null, null);
        return new JSONObject(helper.getPreProcessed());
    }

    private MemberObject createMember(int age, String gender) {
        MemberObject memberObject = new MemberObject();
        memberObject.setDob(DateTime.now().minusYears(age).minusDays(1).toString());
        memberObject.setGender(gender);
        return memberObject;
    }

    private String readPreventiveServicesForm() throws Exception {
        Path assetPath = Paths.get("sample", "src", "main", "assets", "json.form", "hps_preventive_services.json");
        if (!Files.exists(assetPath)) {
            assetPath = Paths.get("..", "sample", "src", "main", "assets", "json.form", "hps_preventive_services.json");
        }
        return new String(Files.readAllBytes(assetPath), StandardCharsets.UTF_8);
    }

    private List<String> getPreventiveServiceOptionKeys(JSONObject jsonObject) throws Exception {
        JSONArray fields = jsonObject.getJSONObject("step1").getJSONArray("fields");
        JSONObject preventiveServices = JsonFormUtils.getFieldJSONObject(fields, "preventive_services");
        JSONArray options = preventiveServices.getJSONArray("options");

        List<String> optionKeys = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            optionKeys.add(options.getJSONObject(i).getString("key"));
        }
        return optionKeys;
    }
}
