package com.idea.plugin.git;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import git4idea.util.GitFileUtils;

import java.util.Collections;
import java.util.List;

public class GitUtils {

    public void addFile(GitRepository repository) throws VcsException {
        FilePath path = VcsUtil.getFilePath("filePath");
        GitFileUtils.addPaths(repository.getProject(), repository.getRoot(), Collections.singleton(path));
    }

}
