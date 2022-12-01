package com.idea.plugin.git;

import com.intellij.openapi.project.Project;
import git4idea.branch.GitBranchUtil;
import git4idea.commands.GitCommandResult;
import git4idea.repo.GitRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GitService {

    private static Git git = new GitImpl();

    public GitCommandResult newNewBranchBaseRemoteMaster(@NotNull GitRepository repository, @Nullable String master, @NotNull String newBranchName) {
        git.fetchNewBranchByRemoteMaster(repository, master, newBranchName);
        git.checkout(repository, newBranchName);
        // 推送分支
        return git.push(repository, newBranchName, true);
    }


    public GitCommandResult newNewBranchByLocalBranch(@NotNull GitRepository repository, @Nullable String localBranchName, @NotNull String newBranchName) {
//        git.checkout(repository, localBranchName);
        git.branch(repository, newBranchName);
        return git.checkout(repository, newBranchName);
    }


    public void deleteBranch(@NotNull GitRepository repository, @Nullable String branchName, @Nullable boolean isDeleteLocalBranch) {
        if (isDeleteLocalBranch) {
            git.deleteLocalBranch(repository, branchName);
        }
        git.deleteRemoteBranch(repository, branchName);
    }


    public GitCommandResult deleteBranch(@NotNull GitRepository repository,
                                         @Nullable String checkoutBranchName,
                                         @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        git.deleteRemoteBranch(repository, branchName);
        return git.deleteLocalBranch(repository, branchName);
    }


    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository,
                                              @Nullable String checkoutBranchName,
                                              @Nullable String branchName) {

        git.checkout(repository, checkoutBranchName);
        return git.deleteLocalBranch(repository, branchName);
    }


    public String getCurrentBranch(@NotNull Project project) {
        GitRepository repository = GitBranchUtil.getCurrentRepository(project);
        return repository.getCurrentBranch().getName();
    }


    public String getRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName) {
        git.fetch(repository);
        GitCommandResult result = git.showRemoteLastCommit(repository, remoteBranchName);
        GitCommandResult lastReleaseTimeResult = git.getLastReleaseTime(repository);
        String msg = result.getOutputAsJoinedString();
        msg = msg.replaceFirst("Author:", "\r\n  Author: ");
        msg = msg.replaceFirst("-Message:", ";\r\n  Message: ");

        String lastReleaseTime = lastReleaseTimeResult.getOutputAsJoinedString();
        if (StringUtils.isNotBlank(lastReleaseTime)) {
            lastReleaseTime = lastReleaseTime.substring(lastReleaseTime.indexOf("@{") + 2, lastReleaseTime.indexOf(" +"));
            msg = msg + "\r\n  Date: " + lastReleaseTime;
        }

        return msg;
    }


    public GitCommandResult getLocalLastCommit(@NotNull GitRepository repository, @Nullable String branchName) {
        git.fetch(repository);
        return git.showLocalLastCommit(repository, branchName);
    }


}
