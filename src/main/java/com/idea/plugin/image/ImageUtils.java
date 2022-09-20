package com.idea.plugin.image;

import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImageUtils {

    private static final String defaultImagePath = "assets";
    private static Pattern urlPattern = Pattern.compile("((http|https)://)([\\w-]+\\.)+[\\w$]+(\\/[\\w-?=&./]*)?$");
    private static String picUrl = "![](%s)\n";
    private static String httpUrl = "[%s](%s)\n";

    public static List<String> getImageFromClipboard() throws Exception {
        List<String> imageNames = new ArrayList<>();
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable == null) {
            return imageNames;
        }
        String absoluteImagePath = ToolSettings.getReportConfig().filePath + "/" + defaultImagePath;
        VirtualFile virtualFile = FileUtils.createDir(absoluteImagePath);
        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            for (File file : fileList) {
                String name = file.getName().toLowerCase();
                int dotIndex = name.lastIndexOf(".");
                if (dotIndex <= 0) {
                    continue;
                }
                String suffix = name.substring(dotIndex);
                if (!(suffix.equalsIgnoreCase(".png") || suffix.equalsIgnoreCase(".jpg") ||
                        suffix.equalsIgnoreCase(".jpeg") || suffix.equalsIgnoreCase(".gif") ||
                        suffix.equalsIgnoreCase(".bmp"))) {
                    continue;
                }
                String imageName = DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYYMMDDHHMMSSS) + ".png";
                FileUtils.copyFile(file.getPath(), virtualFile.getPath() + "/" + imageName);
                imageNames.add(String.format(picUrl, virtualFile.getPath() + "/" + imageName));
            }
        }
        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage image = (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
            String imageName = DateUtils.LocalDateTimeToStr(LocalDateTime.now(), DateUtils.YYYYMMDDHHMMSSS) + ".png";
            File targetFile = new File(virtualFile.getPath() + "/" + imageName);
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            ImageIO.write(image, "PNG", outputStream);
            outputStream.close();
            imageNames.add(String.format(picUrl, virtualFile.getPath() + "/" + imageName));
        }
        // 检查内容是否是文本类型
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                Matcher matcher = urlPattern.matcher(text);
                if (matcher.find()) {
                    String url = matcher.group();
                    String title = text.replaceAll("\n", "").replaceAll(url, "");
                    title = StringUtils.isNotEmpty(title) ? title : url;
                    imageNames.add(String.format(httpUrl, title, url));
                } else {
                    if (ToolSettings.getReportConfig().wordTemplate != null && ToolSettings.getReportConfig().wordTemplate.containsKey(text)) {
                        imageNames.add(ToolSettings.getReportConfig().wordTemplate.get(text));
                    }
                }
            } catch (Exception e) {
                NoticeUtil.error(e);
            }
        }
        return imageNames;
    }

}
