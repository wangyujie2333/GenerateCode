package com.idea.plugin.utils;

import com.intellij.icons.AllIcons;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class NoticeUtil {
    private static String NAME;
    private static Map<String, NotificationGroup> groupMap = new HashMap<>();
    public static NotificationGroup group;

    public static void init(final String name) {
        if (groupMap.containsKey(name)) {
            group = groupMap.get(name);
            NoticeUtil.NAME = name;
        } else {
            group = new NotificationGroup("Generate code", NotificationDisplayType.BALLOON, true, null, AllIcons.General.AddJdk);
            NoticeUtil.NAME = name;
            NotificationsConfiguration.getNotificationsConfiguration().register(NoticeUtil.NAME, NotificationDisplayType.NONE);
        }
    }

    public static Notification getInfo(String text) {
        return group.createNotification(NoticeUtil.NAME, NoticeUtil.NAME, text, NotificationType.INFORMATION, NotificationListener.URL_OPENING_LISTENER);
    }
    public static Notification getWarn(String text) {
        return group.createNotification(NoticeUtil.NAME, NoticeUtil.NAME, text, NotificationType.WARNING);
    }

    public static Notification getError(String text) {
        return group.createNotification(NoticeUtil.NAME, NoticeUtil.NAME, text, NotificationType.ERROR);
    }

    public static void info(String text) {
        start(getInfo(text));
    }

    public static void warn(String text) {
        start(getWarn(text));
    }

    public static void error(String name, Throwable throwable) {
        start(getError(name + getStackTrace(throwable)));
    }

    public static void error(Throwable throwable) {
        start(getError(getStackTrace(throwable)));
    }

    public static void start(Notification notification) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> new NotificationThread(notification).run());
    }

    public static String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder(throwable.getLocalizedMessage() + "\n");
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().startsWith("com.idea.plugin")) {
                sb.append(stackTraceElement.getClassName()).append("#").append(stackTraceElement.getMethodName()).append("[").append(stackTraceElement.getLineNumber()).append("]\n");
            }
        }
        return sb.toString();
    }

    public static class NotificationThread extends Thread {
        static Notification notification;
        static int sleepTime = 4;

        NotificationThread(Notification notification) {
            NotificationThread.notification = notification;
        }

        @Override
        public void run() {
            Notifications.Bus.notify(notification);
            try {
                TimeUnit.SECONDS.sleep(NotificationThread.sleepTime);
            } catch (InterruptedException ex) {
                NoticeUtil.error(ex);
            }
            NotificationThread.notification.expire();
        }
    }
}
