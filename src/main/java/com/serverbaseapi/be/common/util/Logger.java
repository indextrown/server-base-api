package com.serverbaseapi.be.common.util;

public class Logger {

    public enum LogType {
        DEBUG("🟢"),
        WARNING("🟡"),
        ERROR("🔴");

        private final String icon;

        LogType(String icon) {
            this.icon = icon;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static String d(String message) {
        return log(LogType.DEBUG, message);
    }

    public static String w(String message) {
        return log(LogType.WARNING, message);
    }

    public static String e(String message) {
        return log(LogType.ERROR, message);
    }

    private static String log(LogType type, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        StackTraceElement element = null;

        // ✅ com.board.* 이면서 Logger 자신은 제외
        for (StackTraceElement ste : stackTrace) {
            if (ste.getClassName().startsWith("com.serverbaseapi")
                    && !ste.getClassName().contains("Logger")) {
                element = ste;
                break;
            }
        }

        String fileName = (element != null)
                ? element.getFileName().replace(".java", "")
                : "UnknownFile";

        int line = (element != null) ? element.getLineNumber() : -1;
        String function = (element != null) ? element.getMethodName() : "UnknownFunc";

        String logMessage = String.format("[%s] [%s:%d] %s — %s",
                type.getIcon(),
                fileName,
                line,
                function,
                message
        );

        System.out.println(logMessage);
        return logMessage;
    }
}