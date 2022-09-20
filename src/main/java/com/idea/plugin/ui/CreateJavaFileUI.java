package com.idea.plugin.ui;

import com.idea.plugin.orm.support.enums.FileTypeEnum;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.orm.support.enums.MethodEnum;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.JavaFileConfigVO;
import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.utils.FileUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateJavaFileUI {
    private JCheckBox DOCheckBox;
    private JCheckBox DAOCheckBox;
    private JCheckBox SERVICECheckBox;
    private JCheckBox CONTROLLERCheckBox;
    private JTextField modulePath;
    private JTextField DOPath;
    private JTextField VOPath;
    private JTextField DAOPath;
    private JTextField ISERVICEPath;
    private JTextField SERVICEPath;
    private JTextField CONTROLLERPath;
    private JTextField controllerReturn;
    private JLabel doLabel;
    private JLabel daoLabel;
    private JLabel iserviceLabel;
    private JLabel controllerLabel;
    private JPanel panel;
    private JLabel voLabel;
    private JLabel serviceLabel;
    private JLabel controlReturnLabel;
    private JLabel methods;
    private JTextField methodListText;

    private Project project;
    private boolean fileType;

    public static CreateJavaFileUI getInstance(Project project) {
        return new CreateJavaFileUI(project);
    }

    public CreateJavaFileUI(Project project) {
        JavaFileConfigVO config = ToolSettings.getJavaFileConfig();
        setProcedureSelected(config);
        this.modulePath.setText(config.modulePath);
        this.methodListText.setText(String.join(";", config.getMethods()));
        this.DOPath.setText(config.doPath);
        this.VOPath.setText(config.voPath);
        this.DAOPath.setText(config.daoPath);
        this.ISERVICEPath.setText(config.iservicePath);
        this.SERVICEPath.setText(config.servicePath);
        this.CONTROLLERPath.setText(config.controllerPath);
        this.controllerReturn.setText(config.controllerReturn);
        DOPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(DOPath, Arrays.asList("model", "entity"));
            }
        });
        VOPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(VOPath, Arrays.asList("model", "vo"));
            }
        });
        DAOPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(DAOPath, Arrays.asList("dao"));
            }

        });
        ISERVICEPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(ISERVICEPath, Arrays.asList("service"));
            }
        });
        SERVICEPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(SERVICEPath, Arrays.asList("service", "impl"));
            }
        });
        CONTROLLERPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getFileLists(CONTROLLERPath, Arrays.asList("controller"));
            }
        });
        modulePath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Module[] modules = ModuleManager.getInstance(project).getModules();
                List<String> modulePaths = Arrays.stream(modules).map(ModuleUtil::getModuleDirPath).collect(Collectors.toList());
                CreateDemoFileUI.ChooseStringDialog dialog = new CreateDemoFileUI.ChooseStringDialog(project, modulePaths, "Choose Module", "Choose Single Module");
                dialog.setSize(400, 400);
                dialog.show();
                List<String> chosenElements = dialog.getChosenElements();
                if (chosenElements.size() > 0) {
                    modulePath.setText(chosenElements.get(0));
                    config.modulePath = modulePath.getText();
                    config.setDoPath(null);
                    config.setVoPath(null);
                    config.setDaoPath(null);
                    config.setIservicePath(null);
                    config.setServicePath(null);
                    config.setControllerPath(null);
                    DOPath.setText(config.doPath);
                    VOPath.setText(config.voPath);
                    DAOPath.setText(config.daoPath);
                    ISERVICEPath.setText(config.iservicePath);
                    SERVICEPath.setText(config.servicePath);
                    CONTROLLERPath.setText(config.controllerPath);
                }
            }
        });
        DOCheckBox.addActionListener(e -> {
            DOPath.setVisible(DOCheckBox.isSelected());
            VOPath.setVisible(DOCheckBox.isSelected());
            doLabel.setVisible(DOCheckBox.isSelected());
            voLabel.setVisible(DOCheckBox.isSelected());
        });
        DAOCheckBox.addActionListener(e -> {
            DAOPath.setVisible(DAOCheckBox.isSelected());
            daoLabel.setVisible(DAOCheckBox.isSelected());
        });
        SERVICECheckBox.addActionListener(e -> {
            ISERVICEPath.setVisible(SERVICECheckBox.isSelected());
            SERVICEPath.setVisible(SERVICECheckBox.isSelected());
            iserviceLabel.setVisible(SERVICECheckBox.isSelected());
            serviceLabel.setVisible(SERVICECheckBox.isSelected());
        });
        CONTROLLERCheckBox.addActionListener(e -> {
            CONTROLLERPath.setVisible(CONTROLLERCheckBox.isSelected());
            controllerReturn.setVisible(CONTROLLERCheckBox.isSelected());
            controllerLabel.setVisible(CONTROLLERCheckBox.isSelected());
            controlReturnLabel.setVisible(CONTROLLERCheckBox.isSelected());
        });
        methodListText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<String> methods = Arrays.stream(MethodEnum.values()).map(MethodEnum::getCode).collect(Collectors.toList());
                CreateDemoFileUI.ChooseStringDialog dialog = new CreateDemoFileUI.ChooseStringDialog(project, methods, "Choose Method", "Choose Generate Method");
                dialog.setSize(400, 400);
                dialog.show();
                List<String> chosenElements = dialog.getChosenElements();
                if (CollectionUtils.isNotEmpty(chosenElements)) {
                    methodListText.setText(String.join(";", chosenElements));
                    config.methods = chosenElements;
                }
            }
        });
    }

    private void getFileLists(JTextField path, List<String> fileNames) {
        String moudule = modulePath.getText() + "/" + FileTypeEnum.JAVA.getPath();
        List<String> filePaths = FileUtils.listFiles(moudule);
        List<String> filterfilePaths = filePaths.stream().filter(s -> fileNames.stream().anyMatch(fileName -> s.contains(fileName))).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(filterfilePaths)) {
            filePaths = filterfilePaths;
        }
        if (CollectionUtils.isEmpty(filePaths)) {
            return;
        }
        moudule = new File(moudule).getPath();
        String finalMoudule = moudule;
        filePaths = filePaths.stream().map(filePath -> filePath.replace(finalMoudule, "").replaceAll("\\\\", "/")).collect(Collectors.toList());
        CreateDemoFileUI.ChooseStringDialog dialog = new CreateDemoFileUI.ChooseStringDialog(project, filePaths, "Choose path", "Choose Single Path");
        dialog.setSize(400, 400);
        dialog.show();
        List<String> chosenElements = dialog.getChosenElements();
        if (chosenElements.size() > 0) {
            path.setText(chosenElements.get(0));
        }
    }

    private void setProcedureSelected(JavaFileConfigVO config) {
        this.DOCheckBox.setSelected(config.DO);
        this.DAOCheckBox.setSelected(config.DAO);
        this.SERVICECheckBox.setSelected(config.SERVICE);
        this.CONTROLLERCheckBox.setSelected(config.CONTROLLER);
        methods.setVisible(!this.fileType);
        methodListText.setVisible(!this.fileType);
        DOPath.setVisible(DOCheckBox.isSelected());
        doLabel.setVisible(DOCheckBox.isSelected());
        VOPath.setVisible(DOCheckBox.isSelected());
        voLabel.setVisible(DOCheckBox.isSelected());
        DAOPath.setVisible(DAOCheckBox.isSelected());
        daoLabel.setVisible(DAOCheckBox.isSelected());
        ISERVICEPath.setVisible(SERVICECheckBox.isSelected());
        SERVICEPath.setVisible(SERVICECheckBox.isSelected());
        iserviceLabel.setVisible(SERVICECheckBox.isSelected());
        serviceLabel.setVisible(SERVICECheckBox.isSelected());
        CONTROLLERPath.setVisible(CONTROLLERCheckBox.isSelected());
        controllerReturn.setVisible(CONTROLLERCheckBox.isSelected());
        controllerLabel.setVisible(CONTROLLERCheckBox.isSelected());
        controlReturnLabel.setVisible(CONTROLLERCheckBox.isSelected());
    }


    public JComponent getMianPanel() {
        return panel;
    }

    public void fillData(Project project, boolean fileType) {
        this.fileType = fileType;
        methods.setVisible(!this.fileType);
        methodListText.setVisible(!this.fileType);
        fillData(project);
    }

    public void fillData(Project project) {
        JavaFileConfigVO config = ToolSettings.getJavaFileConfig();
        this.project = project;
        config.DO = this.DOCheckBox.isSelected();
        config.DAO = this.DAOCheckBox.isSelected();
        config.SERVICE = this.SERVICECheckBox.isSelected();
        config.CONTROLLER = this.CONTROLLERCheckBox.isSelected();
        config.modulePath = this.modulePath.getText();
        config.methods = Arrays.stream(this.methodListText.getText().split(";")).collect(Collectors.toList());
        config.doPath = this.DOPath.getText();
        config.voPath = this.VOPath.getText();
        config.daoPath = this.DAOPath.getText();
        config.iservicePath = this.ISERVICEPath.getText();
        config.servicePath = this.SERVICEPath.getText();
        config.controllerPath = this.CONTROLLERPath.getText();
        config.controllerReturn = this.controllerReturn.getText();
    }

    public void fillData() {
        JavaFileConfigVO config = ToolSettings.getJavaFileConfig();
        TableConfigVO tableConfig = ToolSettings.getTableConfig();
        setProcedureSelected(config);
        tableConfig.procedureTypeList = new ArrayList<>();
        if (config.isDO()) {
            tableConfig.procedureTypeList.add(FileTypePathEnum.DO.name());
        }
        if (config.isDAO()) {
            tableConfig.procedureTypeList.add(FileTypePathEnum.DAO.name());
        }
        if (config.isSERVICE()) {
            tableConfig.procedureTypeList.add(FileTypePathEnum.SERVICE.name());
        }
        if (config.isCONTROLLER()) {
            tableConfig.procedureTypeList.add(FileTypePathEnum.CONTROLLER.name());
        }
    }

    public String getType() {
        return Stream.of(DOCheckBox, DAOCheckBox, SERVICECheckBox, CONTROLLERCheckBox)
                .filter(AbstractButton::isSelected).map(AbstractButton::getText).collect(Collectors.joining(","));
    }
}
