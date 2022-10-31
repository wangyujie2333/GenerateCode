package com.idea.plugin.orm.service;

import com.idea.plugin.orm.support.TableModule;
import com.idea.plugin.utils.NoticeUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class GeneratorConfig {

    private static final String ENCODING = "UTF-8";

    private static FreemarkerConfiguration freemarker = new FreemarkerConfiguration("/template");

    protected static Template getTemplate(String ftl) throws IOException {
        return freemarker.getTemplate(ftl, ENCODING);
    }

    static class FreemarkerConfiguration extends Configuration {

        public FreemarkerConfiguration(String basePackagePath) {
            super(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            setDefaultEncoding(ENCODING);
            setClassForTemplateLoading(getClass(), basePackagePath);
        }

    }

    public static <T extends TableModule> String getTemplate(Object module, String ftlpath) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Template template = getTemplate(ftlpath);
        template.process(module, stringWriter);
        return stringWriter.toString();
    }

    public void mdFileConversion(File inputFile, File outputFile) {
        List<String> commandSegments = new ArrayList();
        commandSegments.add("pandoc");
        commandSegments.add(inputFile.getAbsolutePath());
        commandSegments.add("--output=" + outputFile.getAbsolutePath());
        String[] result = new String[commandSegments.size()];
        String[] command = commandSegments.toArray(result);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            NoticeUtil.error(e);
        }
    }

}
