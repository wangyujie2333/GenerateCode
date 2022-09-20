package com.idea.plugin.utils;


import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtils {

    public static void fileToZip(String sourceFilePath) {
        String fileNameExit = sourceFilePath + ".zip";
        File zipFile = new File(fileNameExit);
        if (zipFile.exists()) {
            zipFile.delete();
        }
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis;
        BufferedInputStream bis = null;
        FileOutputStream fos;
        ZipOutputStream zos = null;
        if (sourceFile.exists()) {
            try {
                zipFile = new File(fileNameExit);
                File[] sourceFiles = sourceFile.listFiles();

                fos = new FileOutputStream(zipFile);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                byte[] bufs = new byte[1024 * 10];
                for (int i = 0; i < sourceFiles.length; i++) {
                    // 创建ZIP实体,并添加进压缩包
                    ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                    zos.putNextEntry(zipEntry);
                    sourceFiles[i].setExecutable(true);
                    sourceFiles[i].setReadable(true);
                    sourceFiles[i].setWritable(true);
                    // 读取待压缩的文件并写进压缩包里
                    fis = new FileInputStream(sourceFiles[i]);
                    bis = new BufferedInputStream(fis, 1024 * 10);
                    int read = 0;
                    while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                        zos.write(bufs, 0, read);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // 关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        String sourceFilePath = "C:\\Users\\jimmy\\OneDrive\\文档\\201202控制期间增量";
        String zipFilePath = "C:\\Users\\jimmy\\OneDrive\\文档";
        String fileName = "201202控制期间增量";
        ZipUtils.fileToZip(sourceFilePath);
    }

}
