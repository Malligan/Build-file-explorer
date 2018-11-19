package com.alged.bfe

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

        for (settingsFile in settingsFiles) {
            processSettingsFile(settingsFile)
        }
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

    private fun createTableModel(): DefaultTableModel {
        val defaultTableModel = object : DefaultTableModel() {
            override fun getColumnClass(column: Int): Class<*> {
                return when (column) {
                    0 -> String::class.java
                    1 -> Boolean::class.java
                    else -> String::class.java
                }
            }

            override fun isCellEditable(row: Int, column: Int): Boolean {
                return column != 0 //module names at 0 column with disabled editing
            }
        }

        defaultTableModel.addColumn("module name")
        defaultTableModel.addColumn("module availability")

        return defaultTableModel
    }
}