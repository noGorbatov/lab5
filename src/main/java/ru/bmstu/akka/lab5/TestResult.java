package ru.bmstu.akka.lab5;

public class TestResult {
    private final boolean success;
    private final String url;
    private final int count;
    private final double averageTime;
    private final String msg;
    private long sum;
    private boolean added = false;

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
        if (added) {
            return (double) sum / count;
        } else {
            return averageTime;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCount() {
        return count;
    }

    public double getResult() {
        return averageTime;
    }

    static public TestResult add(TestResult agg, Long time) {
        agg.sum += time;
        agg.added = true;
        return agg;
    }
}