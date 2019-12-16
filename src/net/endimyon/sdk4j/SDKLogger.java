package net.endimyon.sdk4j;

import net.endimyon.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

public class SDKLogger {
    private PrintStream printStream;
    public static SDKLogger log = new SDKLogger();
    private File logFile = new File("output-log.log");

    private SDKLogger() {
        File originalFile;
        File orig;
        orig = new File("output-old.log");
        if (orig.exists()) {
            orig.delete();
        }
        originalFile = new File("output-log.log");
        originalFile.renameTo(orig);
        try {
            printStream = new PrintStream(new FileOutputStream(logFile, true));
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTime() {
        Calendar cd = Calendar.getInstance();
        int hour = cd.get(Calendar.HOUR_OF_DAY);
        int minute = cd.get(Calendar.MINUTE);
        int second = cd.get(Calendar.SECOND);
        return "[" + Tools.timeToString(hour) + ":" + Tools.timeToString(minute) + ":" + Tools.timeToString(second) + "]";
    }

    private void log(String level, String content) {
        printStream.println(getTime() + level + content);
        System.out.println(getTime() + level + content);
    }

    public void error(Throwable cause, String content) {
        final String level = " [ERROR] ";
        printStream.println(getTime() + level + content);
        cause.printStackTrace(printStream);
        System.out.println(getTime() + level + content);
        cause.printStackTrace();
    }

    public void error(String content) {
        final String level = " [ERROR] ";
        this.log(level, content);
    }

    public void info(String content) {
        final String level = " [INFO] ";
        this.log(level, content);
    }

    public void debug(String content) {
        final String level = " [DEBUG] ";
        this.log(level, content);
    }

    public void trace(String content) {
        final String level = " [TRACE] ";
        this.log(level, content);
    }
}