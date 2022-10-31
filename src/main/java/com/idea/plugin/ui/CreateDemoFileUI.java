package com.idea.plugin.ui;

import com.google.common.base.CaseFormat;
import com.idea.plugin.demo.DemoFileStrUtils;
import com.idea.plugin.orm.JavaGenerateDialogWrapper;
import com.idea.plugin.orm.support.enums.FileTypePathEnum;
import com.idea.plugin.setting.ToolSettings;
import com.idea.plugin.setting.support.JavaFileConfigVO;
import com.idea.plugin.setting.support.TableConfigVO;
import com.idea.plugin.sql.support.GeneralInfoVO;
import com.idea.plugin.sql.support.GeneralOrmInfoVO;
import com.idea.plugin.sql.support.GeneralSqlInfoVO;
import com.idea.plugin.sql.support.TableInfoVO;
import com.idea.plugin.sql.support.enums.DataTypeEnum;
import com.idea.plugin.sql.support.enums.ProcedureTypeEnum;
import com.idea.plugin.utils.DBUtils;
import com.idea.plugin.utils.FileUtils;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.ide.util.ChooseElementsDialog;
import com.intellij.openapi.project.Project;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateDemoFileUI {
    private JPanel panel;
    private JPanel sqlPanel;
    private JPanel javaPanel;
    private JPanel commonPanel;
    private JTextField author;
    private JTextField jdbcUrl;
    private JTextField username;
    private JTextField dbpasswd;
    private JTextField fileName;
    private JTextField filePath;
    private JButton exportSqlButton;
    private JButton exportJavaButton;
    private JCheckBox ADD_TABLE;
    private JCheckBox ADD_INDEX;
    private JCheckBox ADD_COLUMN;
    private JCheckBox MODIFY_COLUMN;
    private JCheckBox INSERT_DATA;
    private JCheckBox INSERT_SQL;
    private JCheckBox ADD_DATA;
    private JButton filepathSelect;
    private JTextField tableName;
    private JButton refreshButton;
    private Project project;
    private String selectFilePath;
    private TableConfigVO config;

    public static CreateDemoFileUI getInstance(Project project) {
        return new CreateDemoFileUI(project);
    }

    public CreateDemoFileUI(Project project) {
        config = ToolSettings.getTableConfig();
        this.author.setText(config.author);
        this.jdbcUrl.setText(config.jdbcUrl);
        this.username.setText(config.username);
        this.dbpasswd.setText(config.dbpasswd);
        this.filePath.setText(config.filePath);
        this.fileName.setText(config.fileName);
        this.tableName.setText(config.tableName);
        Stream.of(ADD_TABLE, ADD_INDEX, ADD_COLUMN, MODIFY_COLUMN, INSERT_DATA, INSERT_SQL, ADD_DATA).forEach(jCheckBox -> {
            if (config.procedureTypeList.contains(jCheckBox.getText())) {
                jCheckBox.setSelected(true);
            }
        });
        exportSqlButton.addActionListener(e -> {
            try {
                getTableConfigVOFromDB(new GeneralSqlInfoVO());
                if (exportDemoFile(config.username + "_sql.txt", DemoFileStrUtils.sqlFileStr(config))) {
                    NoticeUtil.info("Sql配置文件创建成功");
                }
            } catch (Exception ex) {
                NoticeUtil.error("Sql配置文件创建失败", ex);
            }
        });
        exportJavaButton.addActionListener(e -> {
            try {
                getTableConfigVOFromDB(new GeneralOrmInfoVO());
                JavaGenerateDialogWrapper javaGenerateDialogWrapper = new JavaGenerateDialogWrapper(project);
                JavaFileConfigVO javaFileConfig = ToolSettings.getJavaFileConfig();
                javaFileConfig.copy(config);
                javaGenerateDialogWrapper.fillData();
                javaGenerateDialogWrapper.fillData(project);
                if (javaGenerateDialogWrapper.showAndGet()) {
                    javaGenerateDialogWrapper.fillData();
                    if (exportDemoFile(config.username + "_java.txt", DemoFileStrUtils.javaFileStr(config))) {
                        NoticeUtil.info("Java配置文件创建成功");
                    }
                }
            } catch (Exception ex) {
                NoticeUtil.error("Java配置文件创建失败:", ex);
            }
        });
        filepathSelect.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setCurrentDirectory(new File(project.getBasePath()));
            int res = chooser.showSaveDialog(new JLabel());
            if (JFileChooser.APPROVE_OPTION != res) {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                return;
            }
            this.filePath.setText(file.getAbsolutePath());
            config.filePath = this.filePath.getText();
        });
        tableName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Connection connection = null;
                try {
                    List<String> tableList;
                    String schema = username.getText().toUpperCase();
                    connection = DBUtils.getConnection(jdbcUrl.getText(), username.getText(), dbpasswd.getText());
                    if (jdbcUrl.getText().contains(DataTypeEnum.MYSQL.getCode())) {
                        schema = connection.getCatalog();
                    }
                    if (config.tabNameCacheMap.containsKey(schema)) {
                        tableList = config.tabNameCacheMap.get(schema);
                    } else {
                        tableList = DBUtils.getAllTableName(connection, schema);
                        config.tabNameCacheMap.put(schema, tableList);
                    }
                    String exitsTableNames = tableName.getText().trim();
                    List<String> exitsTableNameList = new ArrayList<>();
                    String searchName = "";
                    if (exitsTableNames.contains(";")) {
                        String[] exitsTableNameArr = exitsTableNames.split(";");
                        if (exitsTableNames.lastIndexOf(";") < exitsTableNames.length() - 1) {
                            searchName = exitsTableNameArr[exitsTableNameArr.length - 1];
                        }
                        String finalSearchName = searchName;
                        exitsTableNameList = Arrays.stream(exitsTableNameArr).filter(tableName -> StringUtils.isNotEmpty(tableName) && !tableName.equals(finalSearchName)).collect(Collectors.toList());
                    } else {
                        searchName = exitsTableNames;
                    }
                    if (StringUtils.isNotEmpty(searchName) || CollectionUtils.isNotEmpty(exitsTableNameList)) {
                        String searchNameIgnore = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, searchName).toLowerCase();
                        List<String> finalExitsTableNameList = exitsTableNameList;
                        tableList = tableList.stream().filter(tableName -> {
                            String tableNameIgnore = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName).toLowerCase();
                            return tableNameIgnore.contains(searchNameIgnore) && !finalExitsTableNameList.contains(tableName);
                        }).collect(Collectors.toList());
                    }
                    ChooseStringDialog dialog = new ChooseStringDialog(project, tableList, "Choose Module", "Choose Single Module");
                    dialog.setMutilSelectionMode();
                    dialog.setSize(400, 400);
                    dialog.show();
                    StringBuilder tableNameStr = new StringBuilder();
                    for (String tableName : exitsTableNameList) {
                        tableNameStr.append(tableName).append(";");
                    }
                    for (String tableName : dialog.getChosenElements()) {
                        tableNameStr.append(tableName).append(";");
                    }
                    tableName.setText(tableNameStr.toString());
                    config.tableName = tableName.getText();
                } catch (Exception ex) {
                    NoticeUtil.error("选择表失败", ex);
                } finally {
                    DBUtils.close(connection);
                }
            }
        });
        tableName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });

        refreshButton.addActionListener(e -> {
            config.tabNameCacheMap = new ConcurrentHashMap<>();
            config.tableInfoCacheMap = new ConcurrentHashMap<>();
        });
    }

    private void getTableConfigVOFromDB(GeneralInfoVO<?> generalInfoVO) {
        fillData(project, selectFilePath);
        generalInfoVO.copy(config);
        List<String> procedureTypes = Arrays.stream(ProcedureTypeEnum.values()).filter(procedureTypeEnum -> {
                    if (generalInfoVO instanceof GeneralSqlInfoVO) {
                        return procedureTypeEnum.getFileType() != null;
                    } else if (generalInfoVO instanceof GeneralOrmInfoVO) {
                        return procedureTypeEnum.getFileCreateType() != null;
                    }
                    return false;
                })
                .map(Enum::name).collect(Collectors.toList());
        String procedureType = config.procedureTypeList.stream().filter(procedureTypes::contains).collect(Collectors.joining(","));
        List<String> tableNameList = config.getTableNameList();
        List<TableInfoVO> tableInfoVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(tableNameList)) {
            TableInfoVO tableInfoVO = DBUtils.initTableInfoVO(config);
            tableInfoVOS.add(tableInfoVO);
        } else {
            for (String tableName : tableNameList) {
                TableInfoVO tableInfoVO = new TableInfoVO();
                tableInfoVO.setTableName(tableName);
                tableInfoVO.setProcedureType(procedureType);
                TableInfoVO tableInfoVOFromDB = DBUtils.getTableInfo(generalInfoVO, tableInfoVO, config);
                tableInfoVO.setProcedureType(procedureType);
                tableInfoVO.fieldInfos = new ArrayList<>();
                DBUtils.getFieldInfo(generalInfoVO, tableInfoVOFromDB, config);
                DBUtils.getTableDataInfo(generalInfoVO, tableInfoVOFromDB);
                DBUtils.addTableInfoAttri(tableInfoVOFromDB);
                tableInfoVOS.add(tableInfoVOFromDB);
            }
        }
        config.tableInfoVOS = tableInfoVOS;
    }

    public boolean exportDemoFile(String demoFileName, String demoFileString) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File(selectFilePath));
        int res = chooser.showSaveDialog(new JLabel());
        if (JFileChooser.APPROVE_OPTION != res) {
            return false;
        }
        File file = chooser.getSelectedFile();
        if (file == null) {
            return false;
        }
        FileUtils.writeFileDelete(file.getAbsolutePath() + "/" + demoFileName, demoFileString);
        return true;
    }

    public JComponent getMianPanel() {
        return panel;
    }

    public void fillData(Project project, String selectFilePath) {
        this.project = project;
        this.selectFilePath = selectFilePath;
        config.author = this.author.getText();
        config.jdbcUrl = this.jdbcUrl.getText();
        config.username = this.username.getText();
        config.dbpasswd = this.dbpasswd.getText();
        config.filePath = this.filePath.getText();
        config.fileName = this.fileName.getText();
        config.tableName = this.tableName.getText();
        config.procedureTypeList = new ArrayList<>();
        fillData();
        Stream.of(ADD_TABLE, ADD_INDEX, MODIFY_COLUMN, ADD_COLUMN, INSERT_DATA, INSERT_SQL, ADD_DATA).forEach(jCheckBox -> {
            if (jCheckBox.isSelected()) {
                config.procedureTypeList.add(jCheckBox.getText());
            }
        });
    }

    public void fillData() {
        JavaFileConfigVO javaFileConfigVO = ToolSettings.getJavaFileConfig();
        config.procedureTypeList = new ArrayList<>();
        if (javaFileConfigVO.getDO()) {
            config.procedureTypeList.add(FileTypePathEnum.DO.name());
        }
        if (javaFileConfigVO.getDAO()) {
            config.procedureTypeList.add(FileTypePathEnum.DAO.name());
        }
        if (javaFileConfigVO.getSERVICE()) {
            config.procedureTypeList.add(FileTypePathEnum.SERVICE.name());
        }
        if (javaFileConfigVO.getCONTROLLER()) {
            config.procedureTypeList.add(FileTypePathEnum.CONTROLLER.name());
        }
    }


    public static class ChooseStringDialog extends ChooseElementsDialog<String> {
        public ChooseStringDialog(Project project, List<String> names, String title, String description) {
            super(project, names, title, description, false);
        }

        public void setMutilSelectionMode() {
            JTable jTable = (JTable) myChooser.getComponent();
            jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }

        @Override
        protected String getItemText(String item) {
            return item;
        }

        @Override
        protected Icon getItemIcon(String item) {
            return null;
        }
    }
}
