package com.idea.plugin.text;

import com.idea.plugin.text.json.ToolMenu;
import com.idea.plugin.utils.NoticeUtil;
import com.intellij.openapi.editor.colors.EditorColorsUtil;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.TextTransferable;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextFormatView extends JRootPane {
    JFrame jFrame;
    Project project;
    /**
     * 菜单
     */
    public ToolMenu menu;
    /**
     * 输入文本域
     */
    public RSyntaxTextArea inputTextArea = new RSyntaxTextArea(19, 0);
    /**
     * 输出文本域
     */
    RSyntaxTextArea outputTextArea = new RSyntaxTextArea(34, 0);
    /**
     * cache instance by project
     */
    private static final Map<Project, TextFormatView> INSTANCES = new HashMap<>();

    private TextFormatView(Project project) {
        jFrame = new JFrame();
        jFrame.add(this);
        jFrame.setSize(1200, 750);
        jFrame.setLocationRelativeTo(null);
        this.project = project;
        super.getContentPane().setLayout(new BoxLayout(super.getContentPane(), BoxLayout.Y_AXIS));
        // 初始化输入文本域
        this.doInitInputTextArea();
        // 初始化菜单
        this.doInitMenu();
        // 初始输出化文本域
        this.doInitOutputTextArea();
        String themexml = EditorColorsUtil.getGlobalOrDefaultColorScheme().getDefaultBackground().getRed() > 125
                ? "/org/fife/ui/rsyntaxtextarea/themes/idea.xml" : "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";
        // 设置高亮主题
        try (InputStream inputStream = this.getClass().getResourceAsStream(themexml)) {
            Theme theme = Theme.load(inputStream);
            theme.apply(this.inputTextArea);
            theme.apply(this.outputTextArea);
        } catch (IOException ex) {
            NoticeUtil.error(ex);
        }
    }

    private void doInitInputTextArea() {
        this.inputTextArea.getInputMap(WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "format");
        this.inputTextArea.getActionMap()
                .put("format", new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                TextFormatView.this.doFormat();
                            }
                        }
                );
        super.getContentPane().add(new RTextScrollPane(this.inputTextArea));
    }


    private void doInitOutputTextArea() {
        this.outputTextArea.setCodeFoldingEnabled(true);
        this.outputTextArea.setAutoIndentEnabled(true);
        super.getContentPane().add(new RTextScrollPane(this.outputTextArea));
    }

    JComboBox<ToolMenu> mainMenu;
    JComboBox<ToolMenu> jsonMenu;
    JComboBox<ToolMenu> javaBeanMenu;
    JComboBox<ToolMenu> sqlMenu;
    JComboBox<ToolMenu> textMenu;

    private void doInitMenu() {
        ItemListener itemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                switchMenu((ToolMenu) e.getItem());
            }
        };
        mainMenu = new ComboBox<>(new ToolMenu[]{
                ToolMenu.JSON,
                ToolMenu.JAVA_BEAN,
                ToolMenu.SQL,
                ToolMenu.TEXT
        });
        mainMenu.addItemListener(itemListener);
        mainMenu.setSelectedItem(0);
        jsonMenu = new ComboBox<>(new ToolMenu[]{
                ToolMenu.JSON_FORMAT,
                ToolMenu.JSON_COMPRESSION,
                ToolMenu.JSON_DOC,
                ToolMenu.JSON_KEY_DOC,
                ToolMenu.KEY_DOC
        });
        jsonMenu.addItemListener(itemListener);
        jsonMenu.setVisible(false);
        javaBeanMenu = new ComboBox<>(new ToolMenu[]{
                ToolMenu.JAVA_BEAN_NULL,
                ToolMenu.JAVA_BEAN_SETGET,
                ToolMenu.JAVA_BEAN_DOC,
                ToolMenu.JAVA_BEAN_ALL
        });
        javaBeanMenu.addItemListener(itemListener);
        javaBeanMenu.setVisible(false);
        sqlMenu = new ComboBox<>(new ToolMenu[]{
                ToolMenu.SQL_MYSQL,
                ToolMenu.SQL_ORACLE,
                ToolMenu.SQL_LOG
        });
        sqlMenu.addItemListener(itemListener);
        sqlMenu.setVisible(false);
        textMenu = new ComboBox<>(new ToolMenu[]{
                ToolMenu.TEXT_CAMELCASE,
                ToolMenu.TEXT_CONSTANT,
                ToolMenu.TEXT_KEBABCASE,
                ToolMenu.TEXT_UNDERSCORECASE,
                ToolMenu.TEXT_WORD,
                ToolMenu.TEXT_TRANSLATOR
        });
        textMenu.addItemListener(itemListener);
        textMenu.setVisible(false);
        this.switchMenu((ToolMenu) mainMenu.getSelectedItem());
        JButton format = new JButton("convert");
        format.addActionListener(e -> doFormat());
        JButton copy = new JButton("copy");
        copy.addActionListener(e -> CopyPasteManager.getInstance().setContents(new TextTransferable(this.outputTextArea.getText())));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(mainMenu);
        panel.add(jsonMenu);
        panel.add(javaBeanMenu);
        panel.add(sqlMenu);
        panel.add(textMenu);
        panel.add(format);
        panel.add(copy);
        super.getContentPane().add(panel);
    }

    public void switchMenu(ToolMenu menu) {
        this.menu = menu;
        if (isMenu(mainMenu, menu)) {
            mainMenu.setSelectedItem(menu);
            if (menu.equals(ToolMenu.JSON)) {
                this.switchMenu((ToolMenu) jsonMenu.getSelectedItem());
                jsonMenu.setVisible(true);
            } else {
                jsonMenu.setVisible(false);
            }
            if (menu.equals(ToolMenu.JAVA_BEAN)) {
                this.switchMenu((ToolMenu) javaBeanMenu.getSelectedItem());
                javaBeanMenu.setVisible(true);
            } else {
                javaBeanMenu.setVisible(false);
            }
            if (menu.equals(ToolMenu.SQL)) {
                this.switchMenu((ToolMenu) sqlMenu.getSelectedItem());
                sqlMenu.setVisible(true);
            } else {
                sqlMenu.setVisible(false);
            }
            if (menu.equals(ToolMenu.TEXT)) {
                this.switchMenu((ToolMenu) textMenu.getSelectedItem());
                textMenu.setVisible(true);
            } else {
                textMenu.setVisible(false);
            }
        } else if (isMenu(jsonMenu, menu)) {
            jsonMenu.setSelectedItem(menu);
            if (menu.equals(ToolMenu.JSON_COMPRESSION)) {
                this.outputTextArea.setLineWrap(true);
            }
        } else if (isMenu(javaBeanMenu, menu)) {
            javaBeanMenu.setSelectedItem(menu);
        } else if (isMenu(sqlMenu, menu)) {
            sqlMenu.setSelectedItem(menu);
        } else if (isMenu(textMenu, menu)) {
            textMenu.setSelectedItem(menu);
        }
        if (Objects.nonNull(this.inputTextArea)) {
            this.inputTextArea.setToolTipSupplier(null);
            this.inputTextArea.removeAllLineHighlights();
            this.inputTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
            this.inputTextArea.setCodeFoldingEnabled(true);
            this.inputTextArea.setAntiAliasingEnabled(true);
            this.inputTextArea.setAutoscrolls(true);
            if (Objects.nonNull(menu.getType())) {
                this.outputTextArea.setSyntaxEditingStyle(menu.getType().getStyle());
                this.outputTextArea.setCodeFoldingEnabled(true);
                this.outputTextArea.setAntiAliasingEnabled(true);
                this.outputTextArea.setAutoscrolls(true);
            }
        }
    }

    private boolean isMenu(JComboBox<ToolMenu> mainMenu, ToolMenu menu) {
        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            if (mainMenu.getItemAt(i).getName().equals(menu.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化动作
     */
    private void doFormat() {
        if (StringUtils.isEmpty(this.inputTextArea.getText())) {
            return;
        }
        try {
            this.inputTextArea.setToolTipSupplier(null);
            this.inputTextArea.removeAllLineHighlights();
            this.menu.handle(this.inputTextArea, this.outputTextArea);
        } finally {
            this.getContentPane().repaint();
        }
    }

    /**
     * 按照{@link Project}单例
     *
     * @param project idea 项目
     * @return {@link TextFormatView}
     */
    public static TextFormatView getInstance(Project project) {
        if (!INSTANCES.containsKey(project)) {
            synchronized (TextFormatView.class) {
                if (!INSTANCES.containsKey(project)) {
                    INSTANCES.putIfAbsent(project, new TextFormatView(project));
                }
            }
        }
        return INSTANCES.get(project);
    }

    public void showframe() {
        this.jFrame.show();
    }
}
