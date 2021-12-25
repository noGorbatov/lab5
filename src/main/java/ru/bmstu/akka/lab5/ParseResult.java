package ru.bmstu.akka.lab5;

public class ParseResult {
    private final int count;
    private final String testUrl;
    private final boolean success;
    private final String msg;
    public ParseResult(boolean success, int count, String testUrl, String msg) {
        this.success = success;
        this.count = count;
        this.testUrl = testUrl;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
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
