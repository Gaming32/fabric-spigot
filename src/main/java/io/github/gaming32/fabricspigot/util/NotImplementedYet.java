package io.github.gaming32.fabricspigot.util;

public class NotImplementedYet extends UnsupportedOperationException {
    public NotImplementedYet() {
        super(getCallerDescription());
    }

    public NotImplementedYet(String detail) {
        super(getCallerDescription() + ": " + detail);
    }

    private static String getCallerDescription() {
        final StackTraceElement frame = new Throwable().getStackTrace()[2];
        return frame.getClassName().substring(frame.getClassName().lastIndexOf('.') + 1) + '.' + frame.getMethodName();
    }
}
