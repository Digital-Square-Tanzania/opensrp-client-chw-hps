package org.smartregister.chw.hps.actionhelper;

import static com.vijay.jsonwizard.constants.JsonFormConstants.FIELDS;
import static com.vijay.jsonwizard.constants.JsonFormConstants.KEY;
import static org.smartregister.AllConstants.OPTIONS;
import static org.smartregister.client.utils.constants.JsonFormConstants.STEP1;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hps.domain.MemberObject;
import org.smartregister.chw.hps.domain.VisitDetail;
import org.smartregister.chw.hps.model.BaseHpsVisitAction;
import org.smartregister.chw.hps.util.Constants;
import org.smartregister.chw.hps.util.JsonFormUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class HpsCurativeServicesActionHelper implements BaseHpsVisitAction.HpsVisitActionHelper {
    public interface DiseaseSignsPayloadProvider {
        String getDiseaseSignsPayload();
    }

    private final String diseaseSignsPayload;
    private final DiseaseSignsPayloadProvider diseaseSignsPayloadProvider;

    protected String jsonPayload;

    protected String wereCurativeServicesProvided;

    protected String baseEntityId;

    protected Context context;

    protected MemberObject memberObject;

    protected Map<String, List<VisitDetail>> details;



    public HpsCurativeServicesActionHelper(Context context,
                                           MemberObject memberObject,
                                           DiseaseSignsPayloadProvider diseaseSignsPayloadProvider) {
        this(context, memberObject, null, diseaseSignsPayloadProvider);
    }

    private HpsCurativeServicesActionHelper(Context context,
                                            MemberObject memberObject,
                                            String diseaseSignsPayload,
                                            DiseaseSignsPayloadProvider diseaseSignsPayloadProvider) {
        this.context = context;
        this.memberObject = memberObject;
        this.diseaseSignsPayload = diseaseSignsPayload;
        this.diseaseSignsPayloadProvider = diseaseSignsPayloadProvider;
    }

    @Override
    public void onJsonFormLoaded(String jsonPayload, Context context, Map<String, List<VisitDetail>> map) {
        this.jsonPayload = jsonPayload;
        this.details = map;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fieldsArray = jsonObject.getJSONObject(STEP1).getJSONArray(FIELDS);

            JSONObject global = jsonObject.optJSONObject(Constants.JSON_FORM_KEYS.GLOBAL);
            if (global == null) {
                global = new JSONObject();
                jsonObject.put(Constants.JSON_FORM_KEYS.GLOBAL, global);
            }

            global.put("gender", memberObject.getGender());

            // memberObject.getAge() already returns age in years.
            int ageYears = memberObject.getAge();
            global.put("age", ageYears);
            //Get key field and options
            JSONObject testConducted = JsonFormUtils.getFieldJSONObject(fieldsArray, Constants.HPS_CURATIVE_SERVICES_FIELDS.DISEASE_TESTS_CONDUCTED);
            JSONArray testConductedOptions = testConducted.getJSONArray(OPTIONS);

            // MUAC should be displayed for clients aged 0-5 years only.
            if (ageYears > 5) {
                for (int i = 0; i < testConductedOptions.length(); i++) {
                    JSONObject option = testConductedOptions.getJSONObject(i);
                    if (option.getString(KEY).equals(Constants.HPS_DISEASE_SIGNS_FIELDS.ARM_CIRCUMFERENCE)) {
                        testConductedOptions.remove(i);
                        break;
                    }
                }
            }

            JSONObject treatmentProvided = JsonFormUtils.getFieldJSONObject(fieldsArray, Constants.HPS_CURATIVE_SERVICES_FIELDS.TREATMENT_PROVIDED);
            JSONArray treatmentProvidedOptions = treatmentProvided.getJSONArray(OPTIONS);
            DiseaseSignsContext diseaseSignsContext = resolveDiseaseSignsContext();
            if (diseaseSignsContext != null) {
                JSONArray filteredTreatmentOptions = new JSONArray();
                for (int i = 0; i < treatmentProvidedOptions.length(); i++) {
                    JSONObject option = treatmentProvidedOptions.optJSONObject(i);
                    if (option == null) {
                        continue;
                    }

                    if (shouldDisplayTreatmentOption(option.optString(KEY), diseaseSignsContext)) {
                        filteredTreatmentOptions.put(option);
                    }
                }
                treatmentProvided.put(OPTIONS, filteredTreatmentOptions);
            }

            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }

        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            this.jsonPayload = jsonPayload;
            JSONObject jsonObject = new JSONObject(jsonPayload);
            wereCurativeServicesProvided = JsonFormUtils.getValue(jsonObject, Constants.HPS_CURATIVE_SERVICES_FIELDS.PROVISION_OF_CURATIVE_SERVICES);

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public BaseHpsVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public String getPreProcessedSubTitle() {
        return null;
    }

    @Override
    public String postProcess(String jsonPayload) {
        try {
            JSONObject form = new JSONObject(jsonPayload);
            String malariaResult = StringUtils.trimToNull(
                    JsonFormUtils.getValue(form, Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT_RESULT)
            );

            if (isMalariaMrdtPositive(malariaResult)) {
                return null;
            }

            JSONArray fieldsArray = form.getJSONObject(STEP1).getJSONArray(FIELDS);
            JSONObject treatmentProvided = JsonFormUtils.getFieldJSONObject(
                    fieldsArray,
                    Constants.HPS_CURATIVE_SERVICES_FIELDS.TREATMENT_PROVIDED
            );
            if (treatmentProvided == null) {
                return null;
            }

            boolean updated = false;

            JSONArray options = treatmentProvided.optJSONArray(OPTIONS);
            if (options != null) {
                for (int i = 0; i < options.length(); i++) {
                    JSONObject option = options.optJSONObject(i);
                    if (option != null
                            && Constants.HPS_TREATMENT_OPTION_KEYS.MALARIA_DRUGS.equals(option.optString(KEY))
                            && option.optBoolean("value", false)) {
                        option.put("value", false);
                        updated = true;
                    }
                }
            }

            Object valueObject = treatmentProvided.opt("value");
            if (valueObject instanceof JSONArray) {
                JSONArray filteredValues = new JSONArray();
                JSONArray values = (JSONArray) valueObject;
                for (int i = 0; i < values.length(); i++) {
                    String value = values.optString(i);
                    if (!Constants.HPS_TREATMENT_OPTION_KEYS.MALARIA_DRUGS.equals(value)) {
                        filteredValues.put(value);
                    } else {
                        updated = true;
                    }
                }
                if (updated) {
                    treatmentProvided.put("value", filteredValues);
                }
            } else if (valueObject instanceof String) {
                String value = (String) valueObject;
                if (StringUtils.contains(value, Constants.HPS_TREATMENT_OPTION_KEYS.MALARIA_DRUGS)) {
                    String filtered = Arrays.stream(value.split(","))
                            .map(String::trim)
                            .filter(StringUtils::isNotBlank)
                            .filter(v -> !Constants.HPS_TREATMENT_OPTION_KEYS.MALARIA_DRUGS.equals(v))
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    treatmentProvided.put("value", filtered);
                    updated = true;
                }
            }

            return updated ? form.toString() : null;
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseHpsVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isNotBlank(wereCurativeServicesProvided)) {
            return BaseHpsVisitAction.Status.COMPLETED;
        }
        return BaseHpsVisitAction.Status.PENDING;
    }

    @Override
    public void onPayloadReceived(BaseHpsVisitAction baseHpsVisitAction) {
        Timber.v("onPayloadReceived");
    }

    private boolean shouldDisplayTreatmentOption(String treatmentKey, DiseaseSignsContext sourceContext) {
        if (StringUtils.isBlank(treatmentKey)) {
            return true;
        }

        switch (treatmentKey) {
            case Constants.HPS_TREATMENT_OPTION_KEYS.ORAL_REHYDRATION_SOLUTIONS:
                return sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.NAUSEA_AND_VOMITING)
                        || sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.DIARRHOEA);
            case Constants.HPS_TREATMENT_OPTION_KEYS.PED_ZINC:
                return sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.DIARRHOEA);
            case Constants.HPS_TREATMENT_OPTION_KEYS.AMOXICILLIN_DT:
                return sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.DIFFICULT_IN_BREATHING)
                        || sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.COUGH);
            case Constants.HPS_TREATMENT_OPTION_KEYS.MALARIA_DRUGS:
                // Option-level visibility cannot react in-form; keep ALu available
                // and enforce correctness in postProcess using malaria_mrdt_result.
                return true;
            case Constants.HPS_TREATMENT_OPTION_KEYS.ANTI_PAIN:
                return sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.HEADACHE)
                        || sourceContext.symptoms.contains(Constants.HPS_SYMPTOM_KEYS.FEVER);
            case Constants.HPS_TREATMENT_OPTION_KEYS.FOOD_SUPPLEMENTS:
                return sourceContext.rutfEligible;
            default:
                return true;
        }
    }

    private DiseaseSignsContext resolveDiseaseSignsContext() {
        String sourcePayload = StringUtils.trimToNull(resolveDiseaseSignsPayload());
        if (sourcePayload != null) {
            DiseaseSignsContext contextFromPayload = parseDiseaseSignsPayload(sourcePayload);
            if (contextFromPayload != null && contextFromPayload.hasData) {
                return contextFromPayload;
            }
        }

        DiseaseSignsContext contextFromDetails = parseDiseaseSignsFromDetails();
        if (contextFromDetails != null && contextFromDetails.hasData) {
            return contextFromDetails;
        }
        return null;
    }

    private String resolveDiseaseSignsPayload() {
        if (diseaseSignsPayloadProvider != null) {
            try {
                return diseaseSignsPayloadProvider.getDiseaseSignsPayload();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return diseaseSignsPayload;
    }

    private DiseaseSignsContext parseDiseaseSignsPayload(String payload) {
        try {
            JSONObject jsonObject = new JSONObject(payload);
            String hasDiseaseSigns = JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.HAS_DISEASE_SIGNS_AND_SYMPTOMS);
            if (StringUtils.isBlank(hasDiseaseSigns)) {
                return null;
            }

            String symptoms = firstNonBlank(
                    JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.SYMPTOMS),
                    JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.DISEASE_SIGNS_AND_SYMPTOMS),
                    JsonFormUtils.getValue(jsonObject, Constants.OPTIONS_FIELDS.DISEASE_SIGNS_MALE),
                    JsonFormUtils.getValue(jsonObject, Constants.OPTIONS_FIELDS.DISEASE_SIGNS_FEMALE)
            );

            String malariaMrdt = firstNonBlank(
                    JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT),
                    JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT_RESULT)
            );

            String muacStatus = JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.MUAC_STATUS);
            String muacMm = JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.MUAC_MM);
            String armCircumference = JsonFormUtils.getValue(jsonObject, Constants.HPS_DISEASE_SIGNS_FIELDS.ARM_CIRCUMFERENCE);

            return buildDiseaseSignsContext(hasDiseaseSigns, symptoms, malariaMrdt, muacStatus, muacMm, armCircumference);
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private DiseaseSignsContext parseDiseaseSignsFromDetails() {
        String hasDiseaseSigns = getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.HAS_DISEASE_SIGNS_AND_SYMPTOMS);
        if (StringUtils.isBlank(hasDiseaseSigns)) {
            return null;
        }

        String symptoms = firstNonBlank(
                getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.SYMPTOMS),
                getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.DISEASE_SIGNS_AND_SYMPTOMS),
                getValueFromDetails(Constants.OPTIONS_FIELDS.DISEASE_SIGNS_MALE),
                getValueFromDetails(Constants.OPTIONS_FIELDS.DISEASE_SIGNS_FEMALE)
        );

        String malariaMrdt = firstNonBlank(
                getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT),
                getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT_RESULT)
        );

        String muacStatus = getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.MUAC_STATUS);
        String muacMm = getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.MUAC_MM);
        String armCircumference = getValueFromDetails(Constants.HPS_DISEASE_SIGNS_FIELDS.ARM_CIRCUMFERENCE);

        return buildDiseaseSignsContext(hasDiseaseSigns, symptoms, malariaMrdt, muacStatus, muacMm, armCircumference);
    }

    private DiseaseSignsContext buildDiseaseSignsContext(String hasDiseaseSigns,
                                                         String symptoms,
                                                         String malariaMrdt,
                                                         String muacStatus,
                                                         String muacMm,
                                                         String armCircumference) {
        DiseaseSignsContext sourceContext = new DiseaseSignsContext();
        String currentCurativeMalariaResult = getCurrentCurativeMalariaResult();
        String effectiveMalariaResult = firstNonBlank(currentCurativeMalariaResult, malariaMrdt);

        sourceContext.symptoms = splitToSet(symptoms);
        sourceContext.malariaPositive = isMalariaMrdtPositive(effectiveMalariaResult);
        // Treat only the current curative form selection as "known" for UI gating.
        // Historical/source values can be stale and should not lock out ALu selection.
        sourceContext.hasKnownMalariaResult = StringUtils.isNotBlank(currentCurativeMalariaResult);
        sourceContext.rutfEligible = isRutfEligible(muacStatus, muacMm, armCircumference);
        sourceContext.hasData = StringUtils.isNotBlank(hasDiseaseSigns);
        return sourceContext;
    }

    private String getCurrentCurativeMalariaResult() {
        try {
            if (StringUtils.isBlank(this.jsonPayload)) {
                return null;
            }

            JSONObject currentPayload = new JSONObject(this.jsonPayload);
            return StringUtils.trimToNull(JsonFormUtils.getValue(currentPayload, Constants.HPS_DISEASE_SIGNS_FIELDS.MALARIA_MRDT_RESULT));
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    private boolean isMalariaMrdtPositive(String malariaMrdt) {
        String normalized = normalizeToken(malariaMrdt);
        return normalized.contains("positive")
                || "positive_mrdt".equals(normalized);
    }

    private boolean isRutfEligible(String muacStatus, String muacMm, String armCircumference) {
        String normalizedStatus = normalizeToken(muacStatus);
        if ("sam".equals(normalizedStatus) || "mam".equals(normalizedStatus)) {
            return true;
        }
        if ("normal".equals(normalizedStatus)) {
            return false;
        }

        Double muacInMm = parseMuacMm(muacMm);
        if (muacInMm == null) {
            muacInMm = parseArmCircumferenceToMm(armCircumference);
        }
        return muacInMm != null && muacInMm < Constants.HPS_THRESHOLDS.MUAC_MALNOURISHED_THRESHOLD_MM;
    }

    private Double parseMuacMm(String value) {
        try {
            String trimmed = StringUtils.trimToNull(value);
            if (trimmed == null) {
                return null;
            }
            return Double.parseDouble(trimmed);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private Double parseArmCircumferenceToMm(String value) {
        try {
            String trimmed = StringUtils.trimToNull(value);
            if (trimmed == null) {
                return null;
            }

            double parsed = Double.parseDouble(trimmed);
            if (parsed <= 50d) {
                return parsed * 10d;
            }
            return parsed;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private String getValueFromDetails(String detailKey) {
        if (details == null || details.isEmpty() || StringUtils.isBlank(detailKey)) {
            return null;
        }

        List<VisitDetail> exactVisitDetails = details.get(detailKey);
        String exactValue = getVisitDetailValue(exactVisitDetails);
        if (StringUtils.isNotBlank(exactValue)) {
            return exactValue;
        }

        for (Map.Entry<String, List<VisitDetail>> entry : details.entrySet()) {
            if (entry.getKey() != null && entry.getKey().contains(detailKey)) {
                String value = getVisitDetailValue(entry.getValue());
                if (StringUtils.isNotBlank(value)) {
                    return value;
                }
            }
        }
        return null;
    }

    private String getVisitDetailValue(List<VisitDetail> visitDetails) {
        if (visitDetails == null || visitDetails.isEmpty()) {
            return null;
        }

        for (VisitDetail visitDetail : visitDetails) {
            String value = JsonFormUtils.getValue(visitDetail);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private Set<String> splitToSet(String csvValue) {
        Set<String> values = new HashSet<>();
        String normalizedCsv = StringUtils.trimToNull(csvValue);
        if (normalizedCsv == null) {
            return values;
        }

        if (normalizedCsv.startsWith("[") && normalizedCsv.endsWith("]")) {
            try {
                JSONArray jsonArray = new JSONArray(normalizedCsv);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String item = normalizeToken(jsonArray.optString(i));
                    if (StringUtils.isNotBlank(item)) {
                        values.add(item);
                    }
                }
                return values;
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        Arrays.stream(normalizedCsv.split(","))
                .map(this::normalizeToken)
                .filter(StringUtils::isNotBlank)
                .forEach(values::add);
        return values;
    }

    private String normalizeToken(String value) {
        return StringUtils.defaultString(value).trim().toLowerCase();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }

        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private static class DiseaseSignsContext {
        private boolean hasData;
        private Set<String> symptoms = new HashSet<>();
        private boolean malariaPositive;
        private boolean hasKnownMalariaResult;
        private boolean rutfEligible;
    }
}
