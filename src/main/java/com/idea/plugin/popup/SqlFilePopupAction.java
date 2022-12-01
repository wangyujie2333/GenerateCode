package com.idea.plugin.popup;

import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.utils.ActionUtils;
import com.idea.plugin.utils.CreateFileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SqlFilePopupAction extends BaseAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            Editor editor = context.getEditor();
            if (editor == null) {
                VirtualFile[] virtualFiles = e.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
                String names = renameFile(virtualFiles);
                NoticeUtil.info("文件重命名成功, 路径: " + names);

            } else {
                GeneralSqlInfoVO generalSqlInfoVO = ActionUtils.readGeneralInfoByText(context, GeneralSqlInfoVO.class);
                CreateFileUtils.generatorSqlFile(generalSqlInfoVO);
                NoticeUtil.info("文件创建成功, 路径: " + generalSqlInfoVO.fileName);
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.isPsiFilePathSuffix("txt") || (context.getProject() != null && context.getEditor() == null));
    }

    public String renameFile(VirtualFile[] virtualFiles) {
        if (virtualFiles == null || virtualFiles.length == 0) {
            NoticeUtil.warn("请选择文件！");
            return null;
        }
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm");
        String num;
        String format = dateFormat.format(LocalDateTime.now());
        List<String> names = new ArrayList<>();
        for (int i = 0; i < virtualFiles.length; i++) {
            try {
                if (i < 10) {
                    num = "0" + (i + 1);
                } else {
                    num = "" + (i + 1);
                }
                VirtualFile virtualFile = virtualFiles[i];
                String path = virtualFile.getPath();
                if (path.endsWith(".sql")) {
                    String mysql = path.replaceAll("(mysql|oracle)", "mysql");
                    String oracle = path.replaceAll("(mysql|oracle)", "oracle");
                    rename(num, format, names, mysql);
                    rename(num, format, names, oracle);
                }
            } catch (Exception e) {
                NoticeUtil.error(e);
            }
        }
        return String.join(";", names);
    }

    private void rename(String num, String format, List<String> names, String typePath) throws IOException {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(typePath);
        if (virtualFile != null) {
            String newName = virtualFile.getName().replaceAll("([\\d]{4}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2}\\.[\\d]{2})", format + "." + num);
            names.add(typePath);
            virtualFile.rename(virtualFile, newName);
            LocalFileSystem.getInstance().refreshFiles(Collections.singleton(virtualFile));
        }
    }


}
