package ru.bmstu.akka.lab5;

public class ParseResult {
    private final int count;
    private final String testUrl;
    private final boolean success;
    public ParseResult(boolean success, int count, String testUrl) {
        this.success = success;
        this.count = count;
        this.testUrl = testUrl;
    }

    public int getCount() {
        return count;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public boolean isSuccess() {
        return success;
    }
}
