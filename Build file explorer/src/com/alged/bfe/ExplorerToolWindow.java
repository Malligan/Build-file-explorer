package com.alged.bfe;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
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
        modulesConfigurationTable.setRowSelectionAllowed(false);
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        syncModulesButton.addActionListener(e -> syncModules());
        applyConfigurationButton.addActionListener(e -> applyConfiguration());

        presenter = new ExplorerToolWindowPresenter(project, createUiTableModel());

        modulesConfigurationTable.setModel(presenter.getUiTableModel());
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private DefaultTableModel createUiTableModel() {
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
    }

    private void applyConfiguration() {
        presenter.applyModulesConfiguration();
    }
}