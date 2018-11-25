package com.alged.bfe

import com.alged.bfe.extensions.getModules
import com.alged.bfe.model.Module
import com.alged.bfe.model.ModulesSelectionConfiguration
import com.alged.bfe.extensions.getVisibleModules
import com.alged.bfe.extensions.inverseNodeCommentingInFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.DummyHolderElement
import javax.swing.table.DefaultTableModel

class ExplorerToolWindowPresenter(val project: Project, val uiTableModel: DefaultTableModel) {
    private var configuration = ModulesSelectionConfiguration()
    private var allModules = listOf<Module>()
    private var settingsFiles = arrayOf<PsiFile>()

    fun updateSettingsFilesWithSync(newSettingsFiles: Array<PsiFile>) {
        settingsFiles = newSettingsFiles
        syncModules()
    }

    fun applyModulesConfiguration() {
        val indexedModules = allModules.getVisibleModules(configuration).withIndex()
        val editableModules = indexedModules.filter { it.value.node !is DummyHolderElement }
        val editedOriginalModules = editableModules
                .filter { uiTableModel.getValueAt(it.index, 1) as Boolean != it.value.enabled } //0 - name, 1 - enabled
                .map { it.value }
        val allModulesForInversing = editedOriginalModules.flatMap(Module::group)
        allModulesForInversing.forEach { it.file?.node?.inverseNodeCommentingInFile(it.node, project) }

        syncModules()
    }

    private fun syncModules() {
        uiTableModel.rowCount = 0 //remove all ui rows
        allModules = settingsFiles.flatMap { it.getModules(configuration) } //remove all readed modules and set new
        allModules.getVisibleModules(configuration)
                .map { arrayOf(it.name, it.enabled) }
                .forEach(uiTableModel::addRow)
        uiTableModel.fireTableDataChanged()
    }
}

//Messages.showMessageDialog(project, extractedModules.map { it.toString() }.reduce { acc, module -> acc + module + "\n" }, "test", Messages.getInformationIcon())