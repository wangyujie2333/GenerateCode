package com.idea.plugin.report.service;

import com.idea.plugin.api.WanNianLiService;
import com.idea.plugin.api.WanNianLiVO;
import com.idea.plugin.orm.service.GeneratorConfig;
import com.idea.plugin.report.support.ReportContext;
import com.idea.plugin.report.support.ReportModuleFactory;
import com.idea.plugin.report.support.enums.ReportTypeEnum;
import com.idea.plugin.report.support.module.ReportFileInfo;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.setting.template.MdTemplateVO;
import com.idea.plugin.setting.template.TemplateTaskPathEnum;
import com.idea.plugin.setting.template.TemplateTaskVO;
import com.idea.plugin.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ReportGenerator extends GeneratorConfig {

    private static final Timer timer = new Timer();
    private static final Pattern pattern = Pattern.compile("^$\\{\\w+\\}");
    private static Boolean taskRun = false;

    public ReportGenerator() {
    }

    public void generationFile(ReportContext context, ReportConfigVO config) {
        try {
            ReportModuleFactory.createModule(context, config);
            String reportStr = FileUtils.readFileStr(context.templatePath);
            FileUtils.writeFileDelete(context.absulotePath, context.getReportModeule().getTemplate(reportStr));
        } catch (Exception igore) {
        }
    }


    public void weekReportFile(ReportConfigVO config) {
        ReportContext reportContext = new ReportContext(ReportTypeEnum.WEEK, config);
        File sourceFile = new File(config.filePath + "/" + config.getDayTemplate().getDayName());
        File targetFile = new File(reportContext.absulotePath);
        if (!sourceFile.exists() || (targetFile.exists() && sourceFile.lastModified() < targetFile.lastModified())) {
            return;
        }
        ReportFileInfo reportFileInfo = new ReportFileInfo();
        reportContext.setFileInfo(reportFileInfo);
        generationFile(reportContext, config);
    }

    public void dayReportFile(ReportConfigVO config) {
        ReportContext reportContext = new ReportContext(ReportTypeEnum.DAY, config);
        File sourceFile = new File(config.filePath + "/" + config.getDayTemplate().getDayName());
        File targetFile = new File(reportContext.absulotePath);
        if (!sourceFile.exists() || (targetFile.exists() && sourceFile.lastModified() < targetFile.lastModified())) {
            return;
        }
        List<String> dayStrlist = FileUtils.readFile(config.filePath + "/" + config.getDayTemplate().getDayName());
        ReportFileInfo reportFileInfo = JsonUtil.fromJson(JsonUtil.getJsonStrByFileStr(dayStrlist), ReportFileInfo.class);
        if (reportFileInfo == null) {
            return;
        }
        reportContext.setFileInfo(reportFileInfo);
        generationFile(reportContext, config);
    }

    public void runSchedule() {
        try {
            ReportConfigVO config = ToolSettings.getReportConfig();
            if (taskRun || config.filePath == null) {
                return;
            }
            WanNianLiService wanNianLiService = new WanNianLiService();
//            config.setWanNianLiVO(wanNianLiService.getApiResult());
            taskRun = true;
            long dayS = config.period == null ? 1000 : config.period;
            Date daytime = DateUtils.StrToDate("2022-05-20 09:00:00", DateUtils.YYYY_MM_DD_HH_MM_SS);
            timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        dayReportFile(config);
                        weekReportFile(config);
                        TemplateFile(config);
                        backupFile(config.backupfromPath, config.backuptoPath);
                        mdFileConversion(config.getArchive().getConversionPath());
                    } catch (Exception ex) {
                        NoticeUtil.error(ex);
                    }
                }
            }, daytime, dayS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void mdFileConversion(List<String> conversionPath) {
        if (CollectionUtils.isEmpty(conversionPath)) {
            return;
        }
        for (String filePath : conversionPath) {
            try {
                File sourceDirFile = new File(filePath);
                File[] sourceFiles = sourceDirFile.listFiles();
                if (sourceFiles == null) {
                    return;
                }
                for (File sourceFile : sourceFiles) {
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
                    if (basicFileAttributes.isRegularFile()) {
                        String sourcePath = sourceDirFile.getPath() + "/" + sourceFile.getName();
                        String targetPath = sourceDirFile.getPath() + "/" + sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf(".")) + ".docx";
                        File targetFile = new File(targetPath);
                        if (!targetFile.exists() || sourceFile.lastModified() > targetFile.lastModified()) {
                            mdFileConversion(new File(sourcePath), new File(targetPath));
                        }
                    } else if (basicFileAttributes.isDirectory()) {
                        mdFileConversion(Collections.singletonList(sourceFile.getPath()));
                    }
                }
            } catch (IOException ex) {
                NoticeUtil.error(ex);
            }
        }
    }

    public void TemplateFile(ReportConfigVO config) {
        MdTemplateVO mdTemplate = config.mdTemplate;
        if (mdTemplate == null || CollectionUtils.isEmpty(mdTemplate.getTemplateTask())) {
            return;
        }
        List<TemplateTaskVO> templateTaskVOS = mdTemplate.getTemplateTask().stream().filter(template -> StringUtils.isNotEmpty(template.getTaskName())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(templateTaskVOS)) {
            return;
        }

        for (TemplateTaskVO templateTaskVO : templateTaskVOS) {
            String filePath = config.filePath + templateTaskVO.getFilePath();
            File sourceFile = new File(config.filePath + "/" + templateTaskVO.getTaskName());
            if (!sourceFile.exists()) {
                continue;
            }
            List<String> designStrlist = FileUtils.readFile(config.filePath + "/" + templateTaskVO.getTaskName());
            designStrlist = designStrlist.stream().map(value -> StringUtil.getResultByTemplate(value, new HashMap<>())).collect(Collectors.toList());
            Map<String, Object> infoJsonMap = JsonUtil.getJsonMapByFileStr(designStrlist);
            String fileName = templateTaskVO.getTaskName() + StringUtil.now(DateUtils.YYYYMMDDHHMMSSS);
            if (infoJsonMap.containsKey("fileName")) {
                fileName = infoJsonMap.get("fileName").toString();
            }
            fileName = filePath + "/" + fileName + ".md";
            File targetFile = new File(fileName);
            if ((targetFile.exists() && sourceFile.lastModified() < targetFile.lastModified())) {
                continue;
            }
            List<Map<String, Object>> bodyInfoMapList = new ArrayList<>();
            Object bodyObj = infoJsonMap.get(TemplateTaskPathEnum.BODY.getCode());
            if (bodyObj instanceof Map) {
                bodyInfoMapList.add((Map<String, Object>) bodyObj);
            } else if (bodyObj instanceof List) {
                for (Object mapObj : ((List<?>) bodyObj)) {
                    if (mapObj instanceof Map) {
                        bodyInfoMapList.add((Map<String, Object>) mapObj);
                    }
                }
            }
            FileUtils.createDir(filePath);
            Integer number = 1;
            boolean isFileWrite = false;
            if (templateTaskVO.getTemplatePath().containsKey(TemplateTaskPathEnum.HEAD.getCode())) {
                if (infoJsonMap.containsKey("number")) {
                    number = Integer.valueOf(infoJsonMap.get("number").toString());
                }
                infoJsonMap.put("number", number);
                String templatePath = templateTaskVO.getTemplatePath().get(TemplateTaskPathEnum.HEAD.getCode());
                String designheadStr = FileUtils.readFileStr(config.filePath + templatePath);
                String resultByTemplate = StringUtil.getResultByTemplate(designheadStr, infoJsonMap);
                FileUtils.writeFileDelete(fileName, resultByTemplate);
                isFileWrite = true;
                ++number;
            }
            if (templateTaskVO.getTemplatePath().containsKey(TemplateTaskPathEnum.BODY.getCode())) {
                String templatePath = templateTaskVO.getTemplatePath().get(TemplateTaskPathEnum.BODY.getCode());
                String designheadStr = FileUtils.readFileStr(config.filePath + templatePath);
                for (Map<String, Object> bodyInfoMap : bodyInfoMapList) {
                    if (bodyInfoMap.containsKey("number")) {
                        number = Integer.valueOf(infoJsonMap.get("number").toString());
                    }
                    bodyInfoMap.putAll(infoJsonMap);
                    bodyInfoMap.put("number", number);
                    String resultByTemplate = StringUtil.getResultByTemplate(designheadStr, bodyInfoMap);
                    if (!isFileWrite) {
                        FileUtils.writeFileDelete(fileName, resultByTemplate);
                        isFileWrite = true;
                    } else {
                        FileUtils.writeFile(fileName, resultByTemplate);
                    }
                    ++number;
                }

            }
            if (templateTaskVO.getTemplatePath().containsKey(TemplateTaskPathEnum.TAIL.getCode())) {
                infoJsonMap.put("number", number);
                String templatePath = templateTaskVO.getTemplatePath().get(TemplateTaskPathEnum.TAIL.getCode());
                String designTailStr = FileUtils.readFileStr(config.filePath + templatePath);
                String resultByTemplate = StringUtil.getResultByTemplate(designTailStr, infoJsonMap);
                if (!isFileWrite) {
                    FileUtils.writeFileDelete(fileName, resultByTemplate);
                } else {
                    FileUtils.writeFile(fileName, resultByTemplate);
                }
            }
        }
    }

    public void backupFile(String frompath, String topath) {
        try {
            if (frompath != null && topath != null) {
                File sourceDirFile = new File(frompath);
                File targetDirFile = new File(topath);
                File[] sourceFiles = sourceDirFile.listFiles();
                if (sourceFiles == null) {
                    return;
                }
                for (File sourceFile : sourceFiles) {
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
                    if (basicFileAttributes.isRegularFile()) {
                        String sourcePath = sourceDirFile.getPath() + "/" + sourceFile.getName();
                        String targetPath = targetDirFile.getPath() + "/" + sourceFile.getName();
                        File targetFile = new File(targetPath);
                        if (!targetFile.exists() || sourceFile.lastModified() > targetFile.lastModified()) {
                            FileUtils.copyFile(sourcePath, targetPath);
                        }
                    } else if (basicFileAttributes.isDirectory()) {
                        String targetPath = topath + "/" + sourceFile.getName();
                        FileUtils.createDir(targetPath);
                        backupFile(sourceFile.getPath(), targetPath);
                    }
                }
            }
        } catch (IOException ex) {
            NoticeUtil.error(ex);
        }
    }
}
