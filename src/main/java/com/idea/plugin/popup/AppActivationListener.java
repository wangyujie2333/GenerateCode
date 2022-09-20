package com.idea.plugin.popup;

import com.idea.plugin.utils.NoticeUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;


public class AppActivationListener implements ApplicationActivationListener {
    private static final Logger LOGGER = Logger.getInstance(AppActivationListener.class);

    private volatile long lastNoticeTime = 0L;

    private static final long INTERVAL = 7 * 24 * 60 * 60 * 1000L;

    @Override
    public synchronized void applicationActivated(@NotNull IdeFrame ideFrame) {
        if (System.currentTimeMillis() - lastNoticeTime < INTERVAL) {
            return;
        }
        NoticeUtil.init("欢迎使用GenerateCode");
        Notification notification = NoticeUtil.getInfo("期望这款小插件为您节约了不少时间\n");
        notification.addAction(new NotificationAction("✨ 去点star") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                try {
                    Desktop dp = Desktop.getDesktop();
                    if (dp.isSupported(Desktop.Action.BROWSE)) {
                        dp.browse(URI.create("https://github.com/wangyujie2333/GenerateCode"));
                    }
                } catch (Exception ex) {
                    NoticeUtil.error("打开链接失败:https://github.com/wangyujie2333/GenerateCode",ex);
                }
            }
        });
//
//        notification.addAction(new NotificationAction("☕ 请喝咖啡") {
//            @Override
//            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
//               /* SupportView supportView = new SupportView();
//                supportView.show();*/
//            }
//        });
        NoticeUtil.start(notification);
        lastNoticeTime = System.currentTimeMillis();
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {
        applicationActivated(ideFrame);
    }
}