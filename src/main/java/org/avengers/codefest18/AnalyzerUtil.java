package org.avengers.codefest18;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class AnalyzerUtil {

    public Optional<ContributionAnalysisSummary> callAnalysisService(double amount) {
        String urlStr = "http://goal-backend.istio.toddelewis.net/calc";

        String response = callRestService(urlStr, amount);
        if (response != null && !response.isEmpty()) {
            JSONObject obj = new JSONObject(response);
            ContributionScenarioOutput before = createContributionScenarioOutput(obj, "before");
            ContributionScenarioOutput after = createContributionScenarioOutput(obj, "after");
            ContributionAnalysisSummary summary = new ContributionAnalysisSummary(before, after);
            return Optional.of(summary);
        }

        return Optional.empty();
    }

    private String callRestService(String urlStr, double amount) {
        String result = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            String body = "{ \"amount\": \"" + amount + "\" }";
            byte[] outputInBytes = body.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                                                   + conn.getResponseCode());
            }

            StringWriter sw = new StringWriter();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
//            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                sw.write(output);
//                System.out.println(output);
            }
            result = sw.getBuffer()
                    .toString();

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContributionScenarioOutput createContributionScenarioOutput(JSONObject obj, String scenarioName) {
        JSONObject before = obj.getJSONObject(scenarioName);
        Double overallSuccessRateDbl = before.getDouble("overallSuccessRate");
        float overallSuccessRate = overallSuccessRateDbl.floatValue();
        ContributionScenarioOutput scenarioOutput = new ContributionScenarioOutput(overallSuccessRate);
        JSONArray goalResults = before.getJSONArray("goalResults");
        int size = goalResults.length();
        for (int i = 0; i < size; i++) {
            JSONObject goal = goalResults.getJSONObject(i);
            String goalName = goal.getString("goalName");
            Double successRateDbl = goal.getDouble("successRate");
            float successRate = successRateDbl.floatValue();
            double contribAmt = goal.getDouble("contribAmt");
            int yearsTillNeed = goal.getInt("yearsTillNeed");
            GoalResult goalResult = new GoalResult(goalName, successRate, contribAmt, yearsTillNeed);
            scenarioOutput.addGoalResult(goalResult);
        }
        return scenarioOutput;
    }
}
