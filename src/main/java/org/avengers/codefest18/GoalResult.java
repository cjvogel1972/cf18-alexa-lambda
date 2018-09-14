package org.avengers.codefest18;

public class GoalResult {
    private String name;
    private float successRate;
    private double contributionAmount;

    public GoalResult(String name, float successRate, double contributionAmount) {
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
}
