package com.alged.bfe

import com.alged.bfe.extensions.getModules
import com.alged.bfe.extensions.inverseNodeCommenting
import com.alged.bfe.model.Module
import com.alged.bfe.model.ModulesSelectionConfiguration
import com.alged.bfe.extensions.getVisibleModules
import com.intellij.lang.ASTNode
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import javax.swing.table.DefaultTableModel

class ExplorerToolWindowPresenter(var project: Project, var uiTableModel: DefaultTableModel) {
    private var configuration: ModulesSelectionConfiguration = ModulesSelectionConfiguration()
    private var allModules = listOf<Module>()

    var settingsFiles: Array<PsiFile> = arrayOf()

    fun updateSettingsFilesWithSync(newSettingsFiles: Array<PsiFile>) {
        settingsFiles = newSettingsFiles
        syncModules()
    }

    fun applyModulesConfiguration() {
        /*
        uiTableModel.dataVector

        for ((index, row) in array.withIndex()) {

        }*/

        allModules.getVisibleModules(configuration).withIndex()

        fileEditingTest(settingsFiles[0].node, settingsFiles[0].node.lastChildNode)
    }

    private fun fileEditingTest(fileNode: ASTNode, nodeForInverting: ASTNode) {
        try {
            WriteCommandAction.runWriteCommandAction(project) {
                fileNode.replaceChild(nodeForInverting, nodeForInverting.inverseNodeCommenting())
            }
        } catch (throwable: Throwable) {
            //avoid
        }
    }

    private fun syncModules() {
        uiTableModel.rowCount = 0 //remove all ui rows
        allModules = settingsFiles.flatMap { it.getModules(configuration) } //remove all readed modules and set new
        allModules.getVisibleModules(configuration).map { arrayOf(it.name, it.enabled) }.forEach(uiTableModel::addRow)
        uiTableModel.fireTableDataChanged()
    }
}

//Messages.showMessageDialog(project, extractedModules.map { it.toString() }.reduce { acc, module -> acc + module + "\n" }, "test", Messages.getInformationIcon())