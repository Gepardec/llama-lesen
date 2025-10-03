package com.gepardec.lernelamalesen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.LevenshteinDistance;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FormEvaluator {

    private static final double TEXT_SIMILARITY_THRESHOLD = 0.85;
    private static final LevenshteinDistance LEVENSHTEIN = LevenshteinDistance.getDefaultInstance();

    private static final Map<String, Integer> FRAGE_WEIGHTS = new HashMap<>();
    static {
        FRAGE_WEIGHTS.put("frage1", 10);  // Datum, Uhrzeit, Ort - KRITISCH
        FRAGE_WEIGHTS.put("frage2", 4);   // Frühere Verletzung
        FRAGE_WEIGHTS.put("frage3", 7);   // Vorfallsort/-art - WICHTIG
        FRAGE_WEIGHTS.put("frage4", 10);  // Fremdes Verschulden - KRITISCH
        FRAGE_WEIGHTS.put("frage5", 7);   // Ereignisschilderung - WICHTIG
        FRAGE_WEIGHTS.put("frage6", 4);   // Raufhandel
        FRAGE_WEIGHTS.put("frage7", 10);  // Alkohol/Drogen - KRITISCH
        FRAGE_WEIGHTS.put("frage8", 4);   // Polizei
        FRAGE_WEIGHTS.put("frage9", 4);   // Gericht
    }

    //Feld-Typen Definition
    private static final Map<String, String> FIELD_TYPES = new HashMap<>();
    static {
        // Checkbox-Felder (Vorfallsort und Vorfallsart)
        FIELD_TYPES.put("frage1_keinKonkretesEreignis", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_beruflicheTaetigkeit", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_arbeitsweg", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_berufskrankheit", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_schulveranstaltung", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_freizeit", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsort_sonstige", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_verkehrsunfall", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_raufhandel", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_arztlicherBehandlungsfehler", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_sportunfall", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_verletzungDurchEinTier", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_glatteissturz", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_gebrauchEinesFehlerhaftenProdukts", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_stromBlitz", "checkbox");
        FIELD_TYPES.put("frage3_vorfallsart_sonstigerVorfall", "checkbox");

        // Ja/Nein-Felder
        FIELD_TYPES.put("frage4_fremdesVerschulden", "ja_nein");
        FIELD_TYPES.put("frage6_raufhandelOderAuseinandersetzung", "ja_nein");
        FIELD_TYPES.put("frage7_konsumVonAlkoholSuchtgiftMedikamenten", "ja_nein");
        FIELD_TYPES.put("frage8_polizeiAufnahme", "ja_nein");
        FIELD_TYPES.put("frage9_gerichtlichesVerfahrenAnhaengig", "ja_nein");

        // Alle anderen sind Text-Felder
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java FormEvaluator <groundTruthDir> <llm1Dir> [llm2Dir] [llm3Dir]");
            return;
        }

        String groundTruthDir = args[0];
        List<String> llmDirs = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            llmDirs.add(args[i]);
        }

        try {
            evaluateAllLLMs(groundTruthDir, llmDirs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void evaluateAllLLMs(String groundTruthDir, List<String> llmDirs) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Ground Truth laden
        File gtDir = new File(groundTruthDir);
        File[] gtFiles = gtDir.listFiles((dir, name) -> name.endsWith(".json"));

        if (gtFiles == null || gtFiles.length == 0) {
            System.out.println("Keine Ground Truth Dateien gefunden!");
            return;
        }

        Map<String, JsonNode> groundTruthData = new HashMap<>();
        for (File file : gtFiles) {
            groundTruthData.put(file.getName(), mapper.readTree(file));
        }

        System.out.println("=".repeat(120));
        System.out.println("EVALUATION RESULTS");
        System.out.println("=".repeat(120));
        System.out.println();

        // Für jedes LLM evaluieren
        for (int llmIdx = 0; llmIdx < llmDirs.size(); llmIdx++) {
            String llmDir = llmDirs.get(llmIdx);
            String llmName = "LLM" + (llmIdx + 1);

            System.out.println("### " + llmName + " (" + llmDir + ")");
            System.out.println("-".repeat(120));

            Map<String, JsonNode> llmData = new HashMap<>();
            File llmDirFile = new File(llmDir);
            File[] llmFiles = llmDirFile.listFiles((dir, name) -> name.endsWith(".json"));

            if (llmFiles != null) {
                for (File file : llmFiles) {
                    llmData.put(file.getName(), mapper.readTree(file));
                }
            }

            EvaluationResult result = evaluateLLM(groundTruthData, llmData);
            printResults(result, llmName);
            System.out.println();
        }
    }

    public static EvaluationResult evaluateLLM(Map<String, JsonNode> groundTruth, Map<String, JsonNode> predicted) {
        EvaluationResult result = new EvaluationResult();

        for (String filename : groundTruth.keySet()) {
            if (!predicted.containsKey(filename)) {
                System.out.println("  WARNING: Datei " + filename + " fehlt in predicted data!");
                continue;
            }

            JsonNode gt = groundTruth.get(filename);
            JsonNode pred = predicted.get(filename);

            boolean perfectForm = true;
            Map<String, Boolean> frageResults = new HashMap<>();

            // Für jede Frage evaluieren
            for (int i = 1; i <= 9; i++) {
                String frageKey = "frage" + i;
                boolean frageCorrect = evaluateFrage(gt.get(frageKey), pred.get(frageKey), result);
                frageResults.put(frageKey, frageCorrect);
                if (!frageCorrect) {
                    perfectForm = false;
                }

                // Gewichtete Accuracy berechnen
                int weight = FRAGE_WEIGHTS.get(frageKey);
                result.totalWeightedScore += weight;
                if (frageCorrect) {
                    result.correctWeightedScore += weight;
                }
            }

            result.frageAccuracies.add(frageResults);
            if (perfectForm) {
                result.perfectForms++;
            }
            result.totalForms++;
        }

        return result;
    }

    private static boolean evaluateFrage(JsonNode gtFrage, JsonNode predFrage, EvaluationResult result) {
        if (gtFrage == null || predFrage == null) {
            return false;
        }

        boolean allCorrect = true;
        Iterator<Map.Entry<String, JsonNode>> fields = gtFrage.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            String gtValue = entry.getValue().asText("");
            String predValue = predFrage.has(fieldName) ? predFrage.get(fieldName).asText("") : "";

            String fieldType = FIELD_TYPES.getOrDefault(fieldName, "text");
            boolean isCorrect = evaluateField(gtValue, predValue, fieldType);

            result.totalFields++;
            if (isCorrect) {
                result.correctFields++;
            } else {
                allCorrect = false;
            }

            // Nach Typ kategorisieren
            switch (fieldType) {
                case "checkbox":
                    result.totalCheckboxes++;
                    if (isCorrect) result.correctCheckboxes++;

                    // Confusion Matrix
                    if (gtValue.equals("true") && predValue.equals("true")) {
                        result.checkboxTruePositive++;
                    } else if (gtValue.equals("") && predValue.equals("")) {
                        result.checkboxTrueNegative++;
                    } else if (gtValue.equals("true") && !predValue.equals("true")) {
                        result.checkboxFalseNegative++;
                    } else if (!gtValue.equals("true") && predValue.equals("true")) {
                        result.checkboxFalsePositive++;
                    }
                    break;

                case "ja_nein":
                    result.totalJaNein++;
                    if (isCorrect) result.correctJaNein++;
                    break;

                case "text":
                    result.totalTexts++;
                    if (isCorrect) result.correctTexts++;

                    // Zusätzlich: Similarity berechnen auch wenn nicht exact match
                    if (!gtValue.isEmpty() || !predValue.isEmpty()) {
                        // Normalisieren für faire Similarity-Berechnung
                        String gtNorm = normalize(gtValue);
                        String predNorm = normalize(predValue);
                        double similarity = levenshteinSimilarity(gtNorm, predNorm);
                        result.textSimilarities.add(similarity);
                    }
                    break;
            }
        }

        return allCorrect;
    }

    private static boolean evaluateField(String groundTruth, String predicted, String fieldType) {
        switch (fieldType) {
            case "checkbox":
            case "ja_nein":
                return groundTruth.equals(predicted);

            case "text":
                // Leere Felder
                if (groundTruth.isEmpty() && predicted.isEmpty()) {
                    return true;
                }
                if (groundTruth.isEmpty() || predicted.isEmpty()) {
                    return false;
                }

                // WICHTIG: Normalisierung für beide Vergleiche
                String gtNorm = normalize(groundTruth);
                String predNorm = normalize(predicted);

                // Erst normalisiert vergleichen (exakt)
                if (gtNorm.equals(predNorm)) {
                    return true;
                }

                // Dann Levenshtein auf NORMALISIERTEN Strings
                double similarity = levenshteinSimilarity(gtNorm, predNorm);
                return similarity >= TEXT_SIMILARITY_THRESHOLD;

            default:
                return groundTruth.equals(predicted);
        }
    }

    private static String normalize(String text) {
        return text.toLowerCase()
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[.,;:!?\\-]", "")
                // Umlaute normalisieren (wichtig für österreichisches Deutsch)
                .replaceAll("ä", "a")
                .replaceAll("ö", "o")
                .replaceAll("ü", "u")
                .replaceAll("ß", "ss");
    }

    private static double levenshteinSimilarity(String s1, String s2) {
        int distance = LEVENSHTEIN.apply(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) distance / maxLen);
    }

    private static void printResults(EvaluationResult result, String llmName) {
        System.out.println("Overall Metrics:");
        System.out.printf("  Total Fields:              %d%n", result.totalFields);
        System.out.printf("  Correct Fields:            %d%n", result.correctFields);
        System.out.printf("  Overall Accuracy:          %.2f%%%n", result.getOverallAccuracy());
        System.out.printf("  WEIGHTED Accuracy:         %.2f%%%n%n", result.getWeightedAccuracy());

        System.out.println("Checkbox Metrics:");
        System.out.printf("  Checkbox Accuracy:         %.2f%% (%d/%d)%n",
                result.getCheckboxAccuracy(), result.correctCheckboxes, result.totalCheckboxes);
        System.out.printf("  True Positives:            %d%n", result.checkboxTruePositive);
        System.out.printf("  True Negatives:            %d%n", result.checkboxTrueNegative);
        System.out.printf("  False Positives:           %d%n", result.checkboxFalsePositive);
        System.out.printf("  False Negatives:           %d%n%n", result.checkboxFalseNegative);

        System.out.println("Ja/Nein Metrics:");
        System.out.printf("  Ja/Nein Accuracy:          %.2f%% (%d/%d)%n%n",
                result.getJaNeinAccuracy(), result.correctJaNein, result.totalJaNein);

        System.out.println("Text Metrics:");
        System.out.printf("  Text Accuracy:             %.2f%% (%d/%d)%n",
                result.getTextAccuracy(), result.correctTexts, result.totalTexts);
        System.out.printf("  Avg Text Similarity:       %.2f%%%n%n", result.getAverageTextSimilarity());

        System.out.println("Per-Question Accuracy (Weight in brackets):");
        for (int i = 1; i <= 9; i++) {
            String frageKey = "frage" + i;
            int weight = FRAGE_WEIGHTS.get(frageKey);
            double acc = result.getFrageAccuracy(frageKey);
            String marker = weight == 10 ? " ⭐⭐⭐" : (weight == 7 ? " ⭐⭐" : " ⭐");
            System.out.printf("  %s (w=%d):            %.2f%%%s%n", frageKey, weight, acc, marker);
        }
        System.out.println();

        System.out.println("Perfect Forms:");
        System.out.printf("  Perfect Forms:             %d/%d (%.2f%%)%n",
                result.perfectForms, result.totalForms, result.getPerfectFormsPercentage());
    }

    static class EvaluationResult {
        int totalFields = 0;
        int correctFields = 0;

        int totalCheckboxes = 0;
        int correctCheckboxes = 0;
        int checkboxTruePositive = 0;
        int checkboxTrueNegative = 0;
        int checkboxFalsePositive = 0;
        int checkboxFalseNegative = 0;

        int totalJaNein = 0;
        int correctJaNein = 0;

        int totalTexts = 0;
        int correctTexts = 0;
        List<Double> textSimilarities = new ArrayList<>();

        int totalForms = 0;
        int perfectForms = 0;

        // Gewichtete Scores
        int totalWeightedScore = 0;
        int correctWeightedScore = 0;

        List<Map<String, Boolean>> frageAccuracies = new ArrayList<>();

        double getOverallAccuracy() {
            return totalFields == 0 ? 0 : (correctFields * 100.0 / totalFields);
        }

        double getWeightedAccuracy() {
            return totalWeightedScore == 0 ? 0 : (correctWeightedScore * 100.0 / totalWeightedScore);
        }

        double getCheckboxAccuracy() {
            return totalCheckboxes == 0 ? 0 : (correctCheckboxes * 100.0 / totalCheckboxes);
        }

        double getJaNeinAccuracy() {
            return totalJaNein == 0 ? 0 : (correctJaNein * 100.0 / totalJaNein);
        }

        double getTextAccuracy() {
            return totalTexts == 0 ? 0 : (correctTexts * 100.0 / totalTexts);
        }

        double getAverageTextSimilarity() {
            if (textSimilarities.isEmpty()) return 0;
            return textSimilarities.stream().mapToDouble(Double::doubleValue).average().orElse(0) * 100;
        }

        double getPerfectFormsPercentage() {
            return totalForms == 0 ? 0 : (perfectForms * 100.0 / totalForms);
        }

        double getFrageAccuracy(String frageKey) {
            if (frageAccuracies.isEmpty()) return 0;
            long correct = frageAccuracies.stream()
                    .filter(map -> map.getOrDefault(frageKey, false))
                    .count();
            return (correct * 100.0 / frageAccuracies.size());
        }
    }
}
