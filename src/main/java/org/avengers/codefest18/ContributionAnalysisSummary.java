package org.avengers.codefest18;

public class ContributionAnalysisSummary {
    private ContributionScenarioOutput before;
    private ContributionScenarioOutput after;

    public ContributionAnalysisSummary(ContributionScenarioOutput before,
                                       ContributionScenarioOutput after) {
        this.before = before;
        this.after = after;
    }

    public ContributionScenarioOutput getBefore() {
        return before;
    }

    public ContributionScenarioOutput getAfter() {
        return after;
    }

    @Override
    public String toString() {
        return "ContributionAnalysisSummary{" +
                "before=" + before +
                ", after=" + after +
                '}';
    }
}
