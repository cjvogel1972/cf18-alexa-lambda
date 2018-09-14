package org.avengers.codefest18;

import java.util.ArrayList;
import java.util.List;

public class ContributionScenarioOutput {

    private float overallSuccessRate;
    private List<GoalResult> goalResults;

    public ContributionScenarioOutput(float overallSuccessRate) {
        this.overallSuccessRate = overallSuccessRate;
        goalResults = new ArrayList<>();
    }

    public void addGoalResult(GoalResult result) {
        goalResults.add(result);
    }

    public float getOverallSuccessRate() {
        return overallSuccessRate;
    }

    public List<GoalResult> getGoalResults() {
        return goalResults;
    }

    @Override
    public String toString() {
        return "ContributionScenarioOutput{" +
                "overallSuccessRate=" + overallSuccessRate +
                ", goalResults=" + goalResults +
                '}';
    }
}
