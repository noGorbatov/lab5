package ru.bmstu.akka.lab5;

public class TestResult {
    private final boolean success;
    private final String url;
    private final double averageTime;

    public TestResult(boolean success, String url, double averageTime) {
        this.success = success;
        this.url = url;
        this.averageTime = averageTime;
    }

    public String getUrl() {
        return url;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public boolean isSuccess() {
        return success;
    }
}