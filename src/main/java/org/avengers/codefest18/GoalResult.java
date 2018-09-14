package org.avengers.codefest18;

public class GoalResult {
    private String name;
    private float successRate;
    private double contributionAmount;
    private int yearsTillNeed;

    public GoalResult(String name, float successRate, double contributionAmount, int yearsTillNeed) {
        this.name = name;
        this.successRate = successRate;
        this.contributionAmount = contributionAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    public double getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(double contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public int getYearsTillNeed() {
        return yearsTillNeed;
    }

    public void setYearsTillNeed(int yearsTillNeed) {
        this.yearsTillNeed = yearsTillNeed;
    }

    @Override
    public String toString() {
        return "GoalResult{" +
                "name='" + name + '\'' +
                ", successRate=" + successRate +
                ", contributionAmount=" + contributionAmount +
                ", yearsTillNeed=" + yearsTillNeed +
                '}';
    }
}
