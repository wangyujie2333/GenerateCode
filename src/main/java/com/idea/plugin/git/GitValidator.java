package com.idea.plugin.git;

import com.intellij.openapi.ui.InputValidatorEx;
import git4idea.GitBranch;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBranchesCollection;
import git4idea.repo.GitRepository;
import git4idea.validators.GitRefNameValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public final class GitValidator implements InputValidatorEx {

    private final Collection<GitRepository> myRepositories;
    private String myErrorText;

    private GitValidator(@NotNull Collection<GitRepository> repositories) {
        this.myRepositories = repositories;
    }

    public static GitValidator newInstance(@NotNull Collection<GitRepository> repositories) {
        return new GitValidator(repositories);
    }

    @Override
    public boolean checkInput(@NotNull String inputString) {
        if (!GitRefNameValidator.getInstance().checkInput(inputString)) {
            myErrorText = "无效的分支名称";
            return false;
        }
        return checkBranchConflict(inputString);
    }

    private boolean checkBranchConflict(@NotNull String inputString) {
        if (conflictsWithLocalBranch(inputString) || conflictsWithRemoteBranch(inputString)) {
            return false;
        }
        myErrorText = null;
        return true;
    }

    private boolean conflictsWithLocalBranch(@NotNull String inputString) {
        return conflictsWithLocalOrRemote(inputString, true, " 在本地仓库中已经存在");
    }

    private boolean conflictsWithRemoteBranch(@NotNull String inputString) {
        return conflictsWithLocalOrRemote(inputString, false, " 在远程仓库中已存在");
    }

    private boolean conflictsWithLocalOrRemote(@NotNull String inputString, boolean local, @NotNull String message) {
        int conflictsWithCurrentName = 0;
        for (GitRepository repository : myRepositories) {
            if (inputString.equals(repository.getCurrentBranchName())) {
                conflictsWithCurrentName++;
            } else {
                GitBranchesCollection branchesCollection = repository.getBranches();
                Collection<? extends GitBranch> branches = local ? branchesCollection.getLocalBranches() : branchesCollection.getRemoteBranches();
                for (GitBranch branch : branches) {
                    if (branch.getName().equals(inputString)) {
                        myErrorText = "Branch name " + inputString + message;
                        if (myRepositories.size() > 1 && !allReposHaveBranch(inputString, local)) {
                            myErrorText += " in repository " + repository.getPresentableUrl();
                        }
                        return true;
                    }
                }
            }
        }
        if (conflictsWithCurrentName == myRepositories.size()) {
            myErrorText = String.format("你当前正处于 '%s' 分支", inputString);
            ;
            return true;
        }
        return false;
    }

    private boolean allReposHaveBranch(String inputString, boolean local) {
        for (GitRepository repository : myRepositories) {
            GitBranchesCollection branchesCollection = repository.getBranches();
            Collection<? extends GitBranch> branches = local ? branchesCollection.getLocalBranches() : branchesCollection.getRemoteBranches();
            if (!GitBranchUtil.convertBranchesToNames(branches).contains(inputString)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canClose(String inputString) {
        return checkInput(inputString);
    }

    @Override
    public String getErrorText(String inputString) {
        return myErrorText;
    }
}
