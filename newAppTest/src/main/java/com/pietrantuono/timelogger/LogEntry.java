package com.pietrantuono.timelogger;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class LogEntry {
    public String time;
    public String message;

    public LogEntry(String message, String time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MESSAGE: " + (getMessage() != null ? getMessage() : ""));
        stringBuilder.append(" - TIME: " + getTime());
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
