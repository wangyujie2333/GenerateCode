package com.idea.plugin.popup;

import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.utils.ActionUtils;
import com.idea.plugin.utils.CreateFileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class SqlFilePopupAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            GeneralSqlInfoVO generalSqlInfoVO = ActionUtils.readGeneralInfoByText(context, GeneralSqlInfoVO.class);
            CreateFileUtils.generatorSqlFile(generalSqlInfoVO);
            NoticeUtil.info("文件创建成功, 路径: " + generalSqlInfoVO.fileName);
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix("txt"));
    }


}
