//package com.idea.plugin.git;
//
//import com.idea.plugin.utils.NoticeUtil;
//import com.intellij.openapi.progress.ProgressManager;
//import com.intellij.openapi.util.Key;
//import git4idea.commands.GitCommand;
//import git4idea.commands.GitCommandResult;
//import git4idea.commands.GitLineHandler;
//import git4idea.commands.GitLineHandlerListener;
//import git4idea.i18n.GitBundle;
//import git4idea.repo.GitRemote;
//import git4idea.repo.GitRepository;
//import org.apache.commons.collections.CollectionUtils;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class GitImpl {
//    private git4idea.commands.Git git = git4idea.commands.Git.getInstance();
//
//    public GitCommandResult checkout(@NotNull GitRepository repository, @NotNull String reference) {
//        // 切换分支
//        NoticeUtil.info(String.format("git -c core.quotepath=false -c log.showSignature=false checkout %s", reference));
//        return git.checkout(repository, reference, null, false, false);
//    }
//
//    public GitCommandResult checkoutNewBranch(GitRepository repository, String branchName) {
//        // git checkout -b 本地分支名x origin/远程分支名x
//        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.CHECKOUT);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.addParameters("-b");
//        h.addParameters(branchName);
//        h.addParameters("origin/" + branchName);
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult fetchNewBranchByRemoteMaster(GitRepository repository, String master, String newBranchName) {
//        //git fetch origin 远程分支名x:本地分支名x
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.FETCH);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("origin");
//        // 远程分支名x:本地分支名x
//        h.addParameters(master + ":" + newBranchName);
//        h.addParameters("-f");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult branch(@NotNull GitRepository repository, @NotNull String newBranchName) {
//        //git branch 本地分支名x
////        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
////        h.setUrls(remote.getUrls());
//        // 远程分支名x:本地分支名x
//        h.addParameters(newBranchName);
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult renameBranch(@NotNull GitRepository repository,
//                                         @Nullable String oldBranch,
//                                         @NotNull String newBranchName) {
//
//        return git.renameBranch(repository, oldBranch, newBranchName);
//    }
//
//    public GitCommandResult push(GitRepository repository, String localBranchName, boolean isNewBranch) {
//        return push(repository, localBranchName, localBranchName, isNewBranch);
//    }
//
//    public GitCommandResult push(GitRepository repository, String localBranchName, String remoteBranchName, boolean isNewBranch) {
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PUSH);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("origin");
//        h.addParameters(localBranchName + ":" + remoteBranchName);
//        h.addParameters("--tag");
//        if (isNewBranch) {
//            h.addParameters("--set-upstream");
//        }
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult deleteRemoteBranch(@NotNull GitRepository repository, @Nullable String branchName) {
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PUSH);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("origin");
//        h.addParameters("--delete");
//        h.addParameters(branchName);
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult deleteLocalBranch(@NotNull GitRepository repository, @Nullable String branchName) {
//        // 删除本地分支
//        NoticeUtil.info(String.format("git -c core.quotepath=false -c log.showSignature=false branch -D %s", branchName));
//        return git.branchDelete(repository, branchName, true);
//    }
//
//    public GitCommandResult showRemoteLastCommit(@NotNull GitRepository repository, @Nullable String remoteBranchName) {
//        //git show origin/master -s --format=Author:%ae-Date:%ad-Message:%s --date=format:%Y-%m-%d_%H:%M:%S
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.SHOW);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("origin/" + remoteBranchName);
//        h.addParameters("-s");
//        h.addParameters("--format=Author:%ae-Message:%s");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult showLocalLastCommit(@NotNull GitRepository repository, @Nullable String localBranchName) {
//        //git show master  -s --format=%s-body:%b
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.SHOW);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters(localBranchName);
//        h.addParameters("-s");
//        h.addParameters("--format=%s-body:%b");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult getLastReleaseTime(@NotNull GitRepository repository) {
//        //git reflog show --date=iso <branch name>
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), getReadGitCommand("reflog"));
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("show");
//        h.addParameters("--date=iso");
//        h.addParameters("origin/" + "GFP_LOCK_BRANCH_NAME");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult createNewTag(@NotNull GitRepository repository, @Nullable String tagName, @Nullable String message) {
//        final GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.TAG);
//        h.setSilent(false);
//        h.addParameters("-a");
//        h.addParameters("-f");
//        h.addParameters("-m");
//        h.addParameters(message);
////        h.addParameters(ConfigUtil.getConfig(repository.getProject()).get().getTagPrefix() + tagName);
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult tagList(@NotNull GitRepository repository) {
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.TAG);
//        h.setSilent(true);
//        NoticeUtil.info(h.printableCommandLine());
//
//        return ProgressManager.getInstance()
//                .runProcessWithProgressSynchronously(() -> git.runCommand(h),
//                        GitBundle.message("tag.getting.existing.tags"),
//                        false,
//                        repository.getProject());
//    }
//
//    public GitCommandResult fetch(@NotNull GitRepository repository) {
//        NoticeUtil.info(String.format("git -c core.quotepath=false -c log.showSignature=false fetch origin"));
//        return git.fetch(repository, getDefaultRemote(repository), Collections.singletonList(new GitFetchPruneDetector()), new String[0]);
//    }
//
//    public GitCommandResult pull(GitRepository repository, @Nullable String branchName) {
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.PULL);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//        h.addParameters("origin");
//        h.addParameters(branchName + ":" + branchName);
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult merge(@NotNull GitRepository repository, @NotNull String sourceBranch, @Nullable String targetBranch, @NotNull GitLineHandlerListener... listeners) {
//        NoticeUtil.info(String.format("git -c core.quotepath=false -c log.showSignature=false merge %s -m \"Merge branch '%s' into %s\"", sourceBranch, sourceBranch, targetBranch));
//        List<String> params = new ArrayList<>();
//        params.add("-m");
//        params.add(String.format("Merge branch '%s' into %s", sourceBranch, targetBranch));
//        return git.merge(repository, sourceBranch, params, listeners);
//    }
//
//    public GitCommandResult getUserEmail(GitRepository repository) {
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.CONFIG);
//        h.setSilent(true);
//        h.addParameters("--null", "--get", "user.email");
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult getAllBranchList(GitRepository repository) {
//        //git show master  -s --format=%s-body:%b
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.BRANCH);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//
//        h.addParameters("-a");
//        h.addParameters("--sort", "committerdate");
//        h.addParameters("--format", "%(committerdate:short)@@@%(authorname)@@@%(refname:short)");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    public GitCommandResult getMergedBranchList(GitRepository repository, String data) {
//        //git log origin/master --after="2020-10-11" --grep="Merge branch 'feature" -i --format=%s-body:%b
//        GitRemote remote = getDefaultRemote(repository);
//        GitLineHandler h = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.LOG);
//        h.setSilent(false);
//        h.setStdoutSuppressed(false);
//        h.setUrls(remote.getUrls());
//
//        h.addParameters("origin/master", "-i", "-10000");
//        h.addParameters(String.format("--after=%s", data));
//        h.addParameters("--grep=Merge");
//        h.addParameters("--format=%s-body:%b");
//
//        NoticeUtil.info(h.printableCommandLine());
//        return git.runCommand(h);
//    }
//
//    private GitRemote getDefaultRemote(@NotNull GitRepository repository) {
//        Collection<GitRemote> remotes = repository.getRemotes();
//        if (CollectionUtils.isEmpty(remotes)) {
//            return null;
//        }
//        return remotes.iterator().next();
//    }
//
//    private GitCommand getReadGitCommand(String name) {
//        try {
//            Method m = GitCommand.class.getDeclaredMethod("read", String.class);
//            m.setAccessible(true);
//            return (GitCommand) m.invoke(null, name);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private class GitFetchPruneDetector implements GitLineHandlerListener {
//
//        private final Pattern PRUNE_PATTERN = Pattern.compile("\\s*x\\s*\\[deleted\\].*->\\s*(\\S*)");
//
//        @NotNull
//        private final Collection<String> myPrunedRefs = new ArrayList<>();
//
//        @Override
//        public void onLineAvailable(String line, Key outputType) {
//            //  x [deleted]         (none)     -> origin/frmari
//            Matcher matcher = PRUNE_PATTERN.matcher(line);
//            if (matcher.matches()) {
//                myPrunedRefs.add(matcher.group(1));
//            }
//        }
//
//        @NotNull
//        public Collection<String> getPrunedRefs() {
//            return myPrunedRefs;
//        }
//    }
//}
