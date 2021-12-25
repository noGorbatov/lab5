package ru.bmstu.akka.lab5;

public class TestResult {
    private final boolean success;
    private final String url;
    private final int count;
    private final double averageTime;
    private final String msg;
    private long sum;

    public TestResult(boolean success, String url, double averageTime, int count, String msg) {
        this.success = success;
        this.url = url;
        this.averageTime = averageTime;
        this.count = count;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
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

    public int getCount() {
        return count;
    }

    static public TestResult add(TestResult agg, TestResult test) {
        agg.sum += test.
    }
}