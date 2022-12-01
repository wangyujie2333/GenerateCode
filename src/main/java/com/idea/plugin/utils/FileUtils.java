package com.idea.plugin.utils;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static VirtualFile createDir(String packageName) {
        String path = FileUtil.toSystemIndependentName(packageName);
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    public static String readFileStr(String path) {
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                br = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                String s;
                while ((s = br.readLine()) != null) {
                    result.append(s).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    public static List<String> readFile(String path) {
        List<String> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                br = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
                String s;
                while ((s = br.readLine()) != null) {
                    result.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void writeFile(String path, String fileStr) {
        writeFile(path, fileStr, true);
    }

    public static void writeFile(String path, String fileStr, boolean append) {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(fileStr)) {
            return;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), StandardCharsets.UTF_8));
            bw.write(fileStr);
            LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeFileDelete(String path, String fileStr) {
        writeFile(path, fileStr, false);
    }

    public static void delete(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] sourceFiles = file.listFiles();
            if (sourceFiles == null) {
                return;
            }
            for (File sourceFile : sourceFiles) {
                delete(sourceFile.getPath());
            }
        }
        if (file.exists()) {
            file.delete();
        }
    }


    public static void copyFile(String path, String newPath) throws IOException {
        File newfile = new File(newPath);
        if (newfile.exists()) {
            newfile.delete();
        }
        File file = new File(path);
        org.apache.commons.io.FileUtils.copyFile(file, newfile);
    }

    public static List<String> listFiles(String path) {
        File newfile = new File(path);
        if (!newfile.exists()) {
            return Collections.emptyList();
        }
        ArrayList<String> list = new ArrayList<>();
        for (File file : newfile.listFiles()) {
            if (file.isDirectory()) {
                list.add(file.getPath());
                list.addAll(listFiles(file.getPath()));
            }
        }
        return list;
    }
}
