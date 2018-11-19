package com.alged.bfe;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.PlainTextASTFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ExplorerToolWindow {
    private ExplorerToolWindowPresenter presenter;

    //top level content panel
    private JPanel myToolWindowContent;
    private JButton hideToolWindowButton;

    private JTabbedPane tabbedPane1;

    //tab content: modules
    private JButton syncModulesButton;
    private JTable modulesConfigurationTable;
    private JButton applyConfigurationButton;

    public ExplorerToolWindow(ToolWindow toolWindow, Project project) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        syncModulesButton.addActionListener(e -> syncModules());
        modulesConfigurationTable.setRowSelectionAllowed(false);

        presenter = new ExplorerToolWindowPresenter(project, createTableModel());

        modulesConfigurationTable.setModel(presenter.getTableModel());
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private DefaultTableModel createTableModel() {
        DefaultTableModel model = new DefaultTableModel() {

            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column){
                //module names at 0 column with disabled editing
                return column != 0;
            }
        };

        model.addColumn("module name");
        model.addColumn("module availability");

        return model;
    }

    private void syncModules() {
        presenter.updateSettingsFilesWithSync(FilenameIndex.getFilesByName(presenter.getProject(), "settings.gradle", GlobalSearchScope.allScope(presenter.getProject())));

        //Messages.showMessageDialog(getCurrentProject(), getSelectedSettingsFile().getContainingDirectory().getName(), "test", Messages.getInformationIcon());
    }

    private void fileEditingTest(ASTNode fileASTNode) {
        PlainTextASTFactory factory = new PlainTextASTFactory();
        LeafElement editedStringPlainTextElement = factory.createLeaf(PlainTextTokenTypes.PLAIN_TEXT, fileASTNode.getLastChildNode().getText() + "\n//edited!");

        try {
            WriteCommandAction.runWriteCommandAction(presenter.getProject(), () -> {
                fileASTNode.replaceChild(fileASTNode.getLastChildNode(), editedStringPlainTextElement);
                fileASTNode.getChildren(null);
            });
        } catch (Throwable throwable) {
            //avoid
        }
    }
}