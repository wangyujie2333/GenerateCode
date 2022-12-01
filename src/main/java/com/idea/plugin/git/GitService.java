package com.idea.plugin.git;

import com.idea.plugin.utils.NoticeUtil;
import git4idea.commands.*;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;


public class GitService {

    private static final GitImpl git = new GitImpl();

    public GitCommandResult newNewBranchByLocalBranch(@NotNull GitRepository repository, @NotNull String newBranchName) {
        branch(repository, newBranchName);
        return checkout(repository, newBranchName);
    }

    public GitCommandResult checkout(@NotNull GitRepository repository, @NotNull String reference) {
        NoticeUtil.info(String.format("git -c core.quotepath=false -c log.showSignature=false checkout %s", reference));
        return git.checkout(repository, reference, null, false, false);
    }

    public GitCommandResult branch(@NotNull GitRepository repository, @NotNull String newBranchName) {
        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
        h.setSilent(false);
        h.setStdoutSuppressed(false);
        h.addParameters(newBranchName);

        NoticeUtil.info(h.printableCommandLine());
        return git.runCommand(h);
    }

}
