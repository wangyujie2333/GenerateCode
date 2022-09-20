package com.idea.plugin.copy;

import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.CopyBuildConfigVO;
import com.idea.plugin.ui.CopyBuildUI;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.idea.plugin.utils.ZipUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CopyFileService {

    public static void copyBuildFile(Project project, VirtualFile[] virtualFiles) {
        if (virtualFiles.length <= 0) {
            NoticeUtil.warn("请选择文件！");
            return;
        }
        CopyBuildConfigVO config = ToolSettings.getCopyBuildConfig();
        try {
            String absolutePath = config.filePathCache;
            String folderName = config.folderName;
            File file = new File(absolutePath + "/" + folderName);
            if (StringUtils.isEmpty(folderName) || !file.exists()) {
                CopyBuildUI copyBuildUI = CopyBuildUI.getInstance(project);
                folderName = "默认文件名称";
                if (copyBuildUI.showAndGet()) {
                    String name = copyBuildUI.getFolderName();
                    if (StringUtils.isNotEmpty(name)) {
                        folderName = name;
                        config.folderName = name;
                    }
                }
            }
            if (StringUtils.isEmpty(absolutePath)) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int res = chooser.showSaveDialog(new JLabel());
                if (JFileChooser.APPROVE_OPTION != res) {
                    return;
                }
                absolutePath = chooser.getSelectedFile().getAbsolutePath();
                config.filePathCache = absolutePath;
            }
            absolutePath = absolutePath + "/" + folderName;
            copyFiles(project, virtualFiles, absolutePath);
            ZipUtils.fileToZip(absolutePath);
        } catch (Exception ex) {
            NoticeUtil.error("增量文件导入失败", ex);
        }
    }

    private static void copyFiles(Project project, VirtualFile[] virtualFiles, String absolutePath) throws IOException {
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                copyFiles(project, virtualFile.getChildren(), absolutePath);
            } else {
                String filePath = virtualFile.getPath();
                String fileType = virtualFile.getFileType().getName();
                String fileName = virtualFile.getName();
                FileTypeEnum fileTypeEnum = FileTypeEnum.codeToEnum(fileType);
                String filePathStr = "";
                String targetPath = "";
                if (fileTypeEnum != null) {
                    targetPath = filePath.replaceAll(fileTypeEnum.getPath(), "target/classes/");
                    filePathStr = targetPath.substring(targetPath.lastIndexOf("target/classes/") + 15);
                    if (FileTypeEnum.JAVA.equals(fileTypeEnum)) {
                        targetPath = targetPath.replaceAll("\\.java", "\\.class");
                        fileName = fileName.replaceAll("\\.java", "\\.class");
                        int subClassNum = 1;
                        boolean hasSubClass = true;
                        while (hasSubClass) {
                            int i = targetPath.lastIndexOf(".");
                            String subTargetPath = targetPath.substring(0, i) + "$" + subClassNum + targetPath.substring(i);
                            int j = fileName.lastIndexOf(".");
                            String subFileName = fileName.substring(0, j) + "$" + subClassNum + fileName.substring(j);
                            File newfile = new File(subTargetPath);
                            if (newfile.exists()) {
                                ++subClassNum;
                                String path = absolutePath + "/" + subFileName;
                                FileUtils.copyFile(subTargetPath, path);
                            } else {
                                hasSubClass = false;
                            }
                        }
                    }
                }
                VirtualFile dir = FileUtils.createDir(absolutePath);
                File file = new File(targetPath);
                String fileTime = "notclass";
                if (file.exists()) {
                    fileTime = DateUtils.DateToStr(new Date(file.lastModified()), DateUtils.YYYY_MM_DD_HH_MM_SS);
                    FileUtils.copyFile(targetPath, dir.getPath() + "/" + fileName);
                } else {
                    Module[] modules = ModuleManager.getInstance(project).getModules();
                    List<String> modulePaths = Arrays.stream(modules).map(ModuleUtil::getModuleDirPath).collect(Collectors.toList());
                    String modulePath = modulePaths.stream().filter(filePath::contains).findAny().orElse("");
                    filePathStr = filePath.replaceAll(modulePath, "");
                    File file1 = new File(targetPath);
                    if (file1.exists()) {
                        fileTime = DateUtils.DateToStr(new Date(file1.lastModified()), DateUtils.YYYY_MM_DD_HH_MM_SS);
                        FileUtils.copyFile(filePath, dir.getPath() + "/" + fileName);
                    }
                }
                String pathtxt = absolutePath + "/增量地址.txt";
                if (!FileUtils.readFile(pathtxt).contains(filePathStr)) {
                    FileUtils.writeFile(pathtxt, filePathStr + "\n");
                }
                NoticeUtil.info("增量文件导入成功-" + fileTime + "-" + filePathStr);
            }
        }
    }
}
