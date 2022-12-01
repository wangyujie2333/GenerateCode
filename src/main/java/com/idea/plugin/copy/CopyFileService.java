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
import java.util.*;

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
            List<String> filePaths = new ArrayList<>();
            copyFiles(project, virtualFiles, absolutePath, config.getSourceCode(), filePaths);
            String pathtxt = absolutePath + "/增量地址.txt";
            filePaths.addAll(FileTreeService.getFilePaths(FileUtils.readFile(pathtxt)));
            List<FileTree> fileTrees = FileTreeService.getFileTrees(filePaths);
            List<String> fileNameList = new ArrayList<>();
            FileTreeService.printFileTree(fileTrees, fileNameList);
            FileUtils.writeFile(pathtxt, String.join("\n", fileNameList), false);
            ZipUtils.fileToZip(absolutePath);
        } catch (Exception ex) {
            NoticeUtil.error("增量文件导入失败", ex);
        }
    }

    private static void copyFiles(Project project, VirtualFile[] virtualFiles, String absolutePath, Boolean sourceCode, List<String> filePaths) throws IOException {
        for (VirtualFile virtualFile : virtualFiles) {
            if (virtualFile.isDirectory()) {
                copyFiles(project, virtualFile.getChildren(), absolutePath, sourceCode, filePaths);
            } else {
                String filePath = virtualFile.getPath();
                String fileType = virtualFile.getFileType().getName();
                String fileName = virtualFile.getName();
                FileTypeEnum fileTypeEnum = FileTypeEnum.codeToEnum(fileType);
                String targetPath = "";
                String targetFileName = "";
                if (!Boolean.TRUE.equals(sourceCode) && fileTypeEnum != null && filePath.contains("src/main/")) {
                    targetPath = filePath.replaceAll(fileTypeEnum.getPath(), "target/classes/");
                    if (FileTypeEnum.JAVA.equals(fileTypeEnum)) {
                        targetPath = targetPath.replaceAll("\\.java", "\\.class");
                        targetFileName = fileName.replaceAll("\\.java", "\\.class");
                        int subClassNum = 1;
                        boolean hasSubClass = true;
                        while (hasSubClass) {
                            int i = targetPath.lastIndexOf(".");
                            String subTargetPath = targetPath.substring(0, i) + "$" + subClassNum + targetPath.substring(i);
                            int j = targetFileName.lastIndexOf(".");
                            String subFileName = targetFileName.substring(0, j) + "$" + subClassNum + targetFileName.substring(j);
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
                String fileTime = copyIncrementalFile(absolutePath, filePath, fileName, targetPath, targetFileName);
                if (targetPath.endsWith(".class")) {
                    filePath = filePath.replaceAll("\\.java", "\\.class");
                }
                String filePathStr = writeIncrementalAddress(project, filePath, filePaths);
                NoticeUtil.info("增量文件导入成功-" + fileTime + "-" + filePathStr);
            }

        }
    }

    /**
     * 复制增量文件
     *
     * @param absolutePath   绝对路径
     * @param filePath       文件路径
     * @param fileName       文件的名字
     * @param targetPath     目标路径
     * @param targetFileName 目标文件的名字
     * @return {@link String}
     * @throws IOException io异常
     */
    private static String copyIncrementalFile(String absolutePath, String filePath, String fileName, String targetPath, String targetFileName) throws IOException {
        VirtualFile dir = FileUtils.createDir(absolutePath);
        File file = new File(targetPath);
        String fileTime = "notclass";
        if (file.exists()) {
            fileTime = DateUtils.DateToStr(new Date(file.lastModified()), DateUtils.YYYY_MM_DD_HH_MM_SS);
            FileUtils.copyFile(targetPath, dir.getPath() + "/" + targetFileName);
        } else {
            File file1 = new File(filePath);
            if (file1.exists()) {
                fileTime = DateUtils.DateToStr(new Date(file1.lastModified()), DateUtils.YYYY_MM_DD_HH_MM_SS);
                FileUtils.copyFile(filePath, dir.getPath() + "/" + fileName);
            }
        }
        return fileTime;
    }

    /**
     * 写增量地址
     *
     * @param project   项目
     * @param filePath  文件路径
     * @param filePaths
     * @return {@link String}
     */
    private static String writeIncrementalAddress(Project project, String filePath, List<String> filePaths) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Optional<Module> optionalModule = Arrays.stream(modules).filter(module -> filePath.contains(ModuleUtil.getModuleDirPath(module))).findAny();
        String filePathStr = optionalModule.map(module -> filePath.substring(filePath.indexOf(module.getName())))
                .orElseGet(() -> filePath.substring(filePath.indexOf(project.getBasePath()) + project.getBasePath().length()))
                .replaceAll("target/classes/|src/main/java/|src/main/resources/","");
        filePaths.add(filePathStr);
        return filePathStr;
    }
}
