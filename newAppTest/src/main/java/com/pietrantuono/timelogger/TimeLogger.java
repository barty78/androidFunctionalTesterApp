package com.pietrantuono.timelogger;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TimeLogger {
    public static List<LogEntry> entries;
    public static AtomicLong startTime = null;
    private static BehaviorSubject<LogEntry> subject= BehaviorSubject.create();

    /**
     * Call TimeLogger.start() to start the timer
     * the first entry is the start time
     */
    public synchronized static void start() {
        if (startTime != null) {
            Log.e("TimeLogger", "Restarting an already started timer, are you sure?");
        }
        startTime = new AtomicLong(System.nanoTime());
        LogEntry logEntry = new LogEntry("Start (absolute time)", "" + startTime);
        if (entries == null) {
            entries = Collections.synchronizedList(new ArrayList<LogEntry>());
        }
        addEntry(logEntry);
    }

    /**
     * Call TimeLogger.stop to stop the timer
     * returns a string containing all logs logged until now
     * the last entry is the time elapsed since start
     */
    public synchronized static String stop() {
        LogEntry logEntry = new LogEntry("End (delta)", getTimeDelta());
        if (entries == null) {
            entries = Collections.synchronizedList(new ArrayList<LogEntry>());
        }
        addEntry(logEntry);
        String out = getEntries();
        entries.clear();
        startTime = null;
        return out;
    }

    /**
     * TimeLogger.getAllLogs()
     * returns a string containing all logs logged until now
     */
    public synchronized static String getAllLogs() {
        return getEntries();
    }

    /**
     * TimeLogger.log(String message)
     * adds a log containing the delta time since started and a message
     */
    public synchronized static void log(String message) {
        LogEntry logEntry = new LogEntry(message, getTimeDelta());
        if (entries == null) {
            entries = Collections.synchronizedList(new ArrayList<LogEntry>());
        }
        addEntry(logEntry);
    }

    /**
     * TimeLogger.logStackTrace()
     * logs the stack trace at the point of execution
     */
    public synchronized static void logStackTrace() {
        Exception exception = new Exception();
        StackTraceElement[] trace = exception.getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement a : trace) {
            builder.append(a.toString());
            builder.append("\n");
        }
        LogEntry logEntry = new LogEntry(builder.toString(), getTimeDelta());
        if (entries == null) {
            entries = Collections.synchronizedList(new ArrayList<LogEntry>());
        }
        addEntry(logEntry);
    }


    private synchronized static String getEntries() {
        if (entries == null || entries.size() <= 0) {
            return "No entries logged";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (LogEntry a : entries) {
           stringBuilder.append(a.toString());
        }
        return stringBuilder.toString();
    }

    private synchronized static String getTimeDelta() {
        long now = System.nanoTime();
        if (startTime != null) {
            return "" + (now - startTime.get());
        } else return "TIMER WAS NOT STARTED";
    }

    private synchronized static void addEntry(LogEntry entry){
        subject.onNext(entry);
        entries.add(entry);
    }

    /**
     * Get your observable here
     */
    public static synchronized Observable<LogEntry> getObservable(){
        return subject.asObservable();
    }
}


