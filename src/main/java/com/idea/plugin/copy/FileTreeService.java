package com.idea.plugin.copy;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileTreeService {

    public static final String prePatternStr = "[ |│─└├]+";
    public static final String leafPatternStr = "└───|├───";
    public static final Pattern prePattern = Pattern.compile(prePatternStr);
    public static final Pattern leafPattern = Pattern.compile(leafPatternStr);

    public static List<String> getFilePaths(List<String> fileNameList) {
        List<String> fileNames = new ArrayList<>();
        for (int i = 1; i < fileNameList.size(); i++) {
            String fileName = fileNameList.get(i);
            String name = fileName.replaceAll(prePatternStr, "");
            if (getLast(fileName)) {
                int index = getIndex(fileName);
                int preindex = index - 1;
                String result = name;
                for (int j = i - 1; j >= 0; j--) {
                    String preFileName = fileNameList.get(j);
                    int indx = getIndex(preFileName);
                    if (preindex == indx || indx == 0) {
                        String preName = preFileName.replaceAll(prePatternStr, "");
                        result = preName + "/" + result;
                        preindex--;
                    }
                    if (indx == 0) {
                        break;
                    }
                }
                fileNames.add(result);
            }
        }
        return fileNames;
    }

    private static int getIndex(String fileName) {
        Matcher matcher = prePattern.matcher(fileName);
        int index = 0;
        if (matcher.find()) {
            String preStr = matcher.group();
            index = preStr.length() / 3;
        }
        return index;
    }

    private static boolean getLast(String fileName) {
        Matcher matcher = leafPattern.matcher(fileName);
        return matcher.find();
    }

    public static List<FileTree> getFileTrees(List<String> filePaths) {
        filePaths.sort(String.CASE_INSENSITIVE_ORDER);
        filePaths = filePaths.stream().distinct().collect(Collectors.toList());
        List<FileTree> fileTrees = new ArrayList<>();
        for (String filePath : filePaths) {
            String[] split = filePath.split("/|\\\\");
            for (int i = 1; i < split.length; i++) {
                FileTree pfileTree = getFileTree(fileTrees, split[i - 1], i - 1);
                if (pfileTree == null) {
                    pfileTree = new FileTree(split[i - 1], i - 1);
                }
                addRootFileTree(fileTrees, pfileTree, i - 1);
                FileTree fileTree = getFileTree(fileTrees, split[i], i);
                if (fileTree == null) {
                    fileTree = new FileTree(split[i], i);
                }
                fileTree.setParent(pfileTree);
                pfileTree.addChildren(fileTree);
                if (i == split.length - 1) {
                    fileTree.setLeaf(true);
                }
            }
        }
        init(fileTrees);
        return fileTrees;
    }

    private static void init(List<FileTree> fileTrees) {
        for (int i = 0; i < fileTrees.size(); i++) {
            FileTree fileTree = fileTrees.get(i);
            boolean last = i == fileTrees.size() - 1;
            fileTree.setLast(last);
            if (fileTree.getChildren() != null) {
                boolean multi = fileTree.getChildren() != null && fileTree.getChildren().size() > 1;
                fileTree.getChildren().forEach(fileTreeChild -> fileTreeChild.setMulti(multi));
                init(fileTree.getChildren());
            }
        }
    }

    public static void printFileTree(List<FileTree> fileTrees, List<String> fileNameList) {
        for (int i = 0; i < fileTrees.size(); i++) {
            FileTree fileTree = fileTrees.get(i);
            fileNameList.add(preStr(fileTree) + fileTree.getName());
            if (!fileTree.getLeaf()) {
                printFileTree(fileTree.getChildren(), fileNameList);
            }
            if (fileTree.getParent() == null) {
                fileNameList.add("\n");
            }
        }
    }


    public static String preStr(FileTree fileTree) {
        StringBuilder preStr = new StringBuilder();
        directoryPreStr(fileTree, preStr);
        preStr.reverse();
        filePreStr(fileTree, preStr);
        return preStr.toString();
    }

    private static void filePreStr(FileTree fileTree, StringBuilder preStr) {
        if (fileTree.getIndex() <= 0) {
            return;
        }
        if (fileTree.getLast()) {
            preStr.append("└──");
        } else {
            preStr.append("├──");
        }
        if (fileTree.getLeaf()) {
            preStr.append("─");
        }
    }

    private static void directoryPreStr(FileTree fileTree, StringBuilder preStr) {
        if (fileTree.getParent() == null) {
            return;
        }
        if (fileTree.getParent().getMulti() && !fileTree.getParent().getLast()) {
            preStr.append("  │");
        } else {
            preStr.append("   ");
        }
        directoryPreStr(fileTree.getParent(), preStr);
    }


    private static FileTree getFileTree(List<FileTree> fileTrees, String name, int index) {
        if (CollectionUtils.isEmpty(fileTrees)) {
            return null;
        }
        FileTree result = null;
        for (FileTree fileTree : fileTrees) {
            if (fileTree.getIndex() == index && fileTree.getName().equals(name)) {
                return fileTree;
            } else {
                result = getFileTree(fileTree.getChildren(), name, index);
            }
        }
        return result;
    }

    private static void addRootFileTree(List<FileTree> fileTrees, FileTree fileTree, int index) {
        if (index > 0) {
            return;
        }
        if (fileTrees.stream().noneMatch(fileTree1 -> fileTree1.getName().equals(fileTree.getName()))) {
            fileTrees.add(fileTree);
        }
    }
}
