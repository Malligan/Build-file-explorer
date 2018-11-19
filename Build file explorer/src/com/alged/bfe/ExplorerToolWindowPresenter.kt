package com.alged.bfe

import com.alged.bfe.model.RowModelWithNode
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import javax.swing.table.DefaultTableModel

class ExplorerToolWindowPresenter(var project: Project, var tableModel: DefaultTableModel) {
    var configuration: ModulesSelectionConfiguration = ModulesSelectionConfiguration()
    var tableModelWithNodes = mutableListOf<RowModelWithNode>()

    var settingsFiles: Array<PsiFile> = arrayOf()

    fun updateSettingsFilesWithSync(newSettingsFiles: Array<PsiFile>) {
        settingsFiles = newSettingsFiles

        syncModules()
    }

    private fun syncModules() {
        tableModel.rowCount = 0 //remove all rows
        settingsFiles.forEach { processSettingsFile(it) }
    }

    private fun processSettingsFile(settingsFile: PsiFile) {
        val modulesSelectionService = ModulesSelectionService(configuration)
        val rowModel = modulesSelectionService.buildTableModel(settingsFile)

        val rowModelWithHeader = mutableListOf(RowModelWithNode("", false, null, header = true))
        rowModelWithHeader.addAll(rowModel) //TODO continue with rowModel and rowModel adding to tableModel

        if (configuration.dualModuleMode) {
            for (row in rowModel.filter { it.moduleOriginal }) {
                tableModel.addRow(arrayOf(row.moduleName, row.moduleEnabled))
            }
        } else {
            for (row in rowModel) {
                tableModel.addRow(arrayOf(row.moduleName, row.moduleEnabled))
            }
        }

        tableModel.fireTableDataChanged()
    }
}