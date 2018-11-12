package com.alged.bfe;

import com.intellij.ide.util.TreeFileChooser;
import com.intellij.ide.util.TreeFileChooserFactory;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.PlainTextASTFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ExplorerToolWindow {
    //top level content panel
    private JPanel myToolWindowContent;
    private JButton hideToolWindowButton;

    private JTabbedPane tabbedPane1;

    //tab content: modules
    private JButton readModulesButton;
    private JTable modulesConfigurationTable;


    private Project currentProject;
    private PsiFile selectedSettingsFile;
    private DefaultTableModel model;

    //primitives for editing original document //not working
    private ASTNode whiteSpaceNode;
    private ASTNode newLineNode;

    private List<ASTNode> commentNodes = new ArrayList<>();
    private List<ASTNode> assignmentExpressionNodes = new ArrayList<>();


    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public PsiFile getSelectedSettingsFile() {
        return selectedSettingsFile;
    }

    public void setSelectedSettingsFile(PsiFile selectedSettingsFile) {
        this.selectedSettingsFile = selectedSettingsFile;
    }

    public ExplorerToolWindow(ToolWindow toolWindow) {
        hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        readModulesButton.addActionListener(e -> readModules());
        modulesConfigurationTable.setRowSelectionAllowed(false);

        model = new DefaultTableModel() {

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

        modulesConfigurationTable.setModel(model);
    }


    public void readModules() {
        TreeFileChooser.PsiFileFilter fileFilter = file -> file.getName().contains("settings.gradle");
        TreeFileChooser fileChooser = TreeFileChooserFactory.getInstance(getCurrentProject()).createFileChooser("Select settings.gradle file", null, null, fileFilter);
        fileChooser.showDialog();

        //GroovyFileImpl
        setSelectedSettingsFile(fileChooser.getSelectedFile());

        if (getSelectedSettingsFile() == null) {
            return;
        }

        PsiFile file = getSelectedSettingsFile();
        ASTNode fileASTNode = file.getNode();

        ASTNode[] children = fileASTNode.getChildren(null);


        //read all nodes and fill node fields
        //String childrenLog = "";
        //toStringChildrenLogTest(children, childrenLog);

        ModulesSelectionService tableModelCreator = new ModulesSelectionService();
        List<Object[]> rowModel = tableModelCreator.updateTableModel(children);

        for (Object[] row : rowModel) {
            model.addRow(row);
        }

        model.fireTableDataChanged();

        //fileEditingTest(fileASTNode);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private void fileEditingTest(ASTNode fileASTNode) {
        PlainTextASTFactory factory = new PlainTextASTFactory();
        LeafElement editedStringPlainTextElement = factory.createLeaf(PlainTextTokenTypes.PLAIN_TEXT, fileASTNode.getLastChildNode().getText() + "\n//edited!");

        try {
            WriteCommandAction.runWriteCommandAction(getCurrentProject(), () -> {
                fileASTNode.replaceChild(fileASTNode.getLastChildNode(), editedStringPlainTextElement);
                fileASTNode.getChildren(null);
            });
        } catch (Throwable throwable) {
            //avoid
        }
    }

    private void toStringChildrenLogTest(ASTNode[] children, String childrenLog) {
        for (ASTNode node : children) {
            childrenLog  = childrenLog + "node type: " + node.getElementType().toString() + "\n" +
                    "node start offset: " + node.getStartOffset() + "\n" +
                    "node text: " + node.getText() + "\n";

            //newline after comment
            if (node.getElementType().toString().equals("WHITE_SPACE")) {
                whiteSpaceNode = (ASTNode) node.clone();
            }

            //newline after assignment
            if (node.getElementType().toString().equals("new line")) {
                newLineNode = (ASTNode) node.clone();
            }

            if (node.getElementType().toString().equals("line comment")) {
                commentNodes.add((ASTNode) node.clone());
            }

            if (node.getElementType().toString().equals("Assignment expression")) {
                assignmentExpressionNodes.add((ASTNode) node.clone());
            }
        }

        Messages.showMessageDialog(getCurrentProject(), childrenLog, "lcn content", Messages.getInformationIcon());
    }
}