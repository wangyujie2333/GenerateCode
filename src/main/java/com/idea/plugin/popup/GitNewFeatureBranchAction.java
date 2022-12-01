package com.idea.plugin.popup;

import com.idea.plugin.git.GitService;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.ReportConfigVO;
import com.idea.plugin.ui.GitBranchUI;
import com.idea.plugin.utils.DateUtils;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.vcs.ui.Refreshable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.vcs.commit.CommitProjectPanelAdapter;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitNewFeatureBranchAction extends BaseAction {

    private static final GitService gitService = new GitService();

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            super.actionPerformed(e);
            GitRepositoryManager instance = GitRepositoryManager.getInstance(context.getProject());
            List<GitRepository> repositories = instance.getRepositories();
            CommitProjectPanelAdapter commitProjectPanel = (CommitProjectPanelAdapter) e.getData(Refreshable.PANEL_KEY);
            if (CollectionUtils.isEmpty(repositories) || commitProjectPanel == null) {
                return;
            }
            Collection<VirtualFile> virtualFiles = commitProjectPanel.getVirtualFiles();
            if (CollectionUtils.isEmpty(virtualFiles)) {
                return;
            }
            List<String> filePaths = virtualFiles.stream().map(virtualFile -> virtualFile.getPath()).collect(Collectors.toList());
            List<GitRepository> selectedRepositorys = repositories.stream().filter(repository -> filePaths.stream().anyMatch(filePath -> filePath.contains(repository.getRoot().getPath()))).collect(Collectors.toList());
            String commitMessage = commitProjectPanel.getCommitMessage();
            Pattern idPattern = Pattern.compile("\\d{7}");
            Matcher matcher = idPattern.matcher(commitMessage);
            String idvalue = "0000000";
            if (matcher.find()) {
                idvalue = matcher.group();
            }
            ReportConfigVO config = ToolSettings.getReportConfig();
            String mfeature = config.getGitSetting().getBranchKey().getMfeature();
            String mfeatrueName = mfeature + "-" + DateUtils.nowDate("20220101") + "-" + idvalue + "-" + ToolSettings.getSettingConfig().getAuthor();
            String newBranchName = getInputString(mfeatrueName);
            if (StringUtils.isEmpty(newBranchName)) {
                return;
            }
            for (GitRepository repository : selectedRepositorys) {
                new Task.Backgroundable(context.getProject(), newBranchName, false) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        gitService.newNewBranchByLocalBranch(repository, newBranchName);
                        repository.update();
                        VirtualFileManager.getInstance().asyncRefresh(null);
                    }
                }.queue();
            }
            if (config.getOpen()) {
                String path = config.filePath + "/" + config.getDayTemplate().getDayName();
                FileUtils.writeFile(path, commitMessage.replaceAll("\n", "") + "\n");
            }
        } catch (Exception ex) {
            NoticeUtil.error(ex);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setVisible(context.getProject() != null);
    }

    private static String getInputString(String mfeatrueName) {
        GitBranchUI gitBranchUI = GitBranchUI.getInstance(context.getProject());
        gitBranchUI.setFieldText(mfeatrueName);
        if (gitBranchUI.showAndGet()) {
            return gitBranchUI.getBranchName();
        }
        return null;
    }

}
