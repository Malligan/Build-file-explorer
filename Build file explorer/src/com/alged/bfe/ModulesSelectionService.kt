package com.alged.bfe

import com.intellij.lang.ASTNode

class ModulesSelectionService {

    companion object {
        val modulePathRegex = """('[^:]\S+')+""".toRegex()
        val moduleNameRegex = """('[:]\S+')+""".toRegex()
    }

    var tableModel : MutableList<RowModelWithNode> = mutableListOf()

    fun updateTableModel(nodes : Array<ASTNode>): List<Array<Any>> {
        val projectNodes : MutableList<ASTNode>  = (nodes.filter { it.text.contains("project") }).toMutableList()

        while (projectNodes.isNotEmpty()) {
            proceedNode(projectNodes.first(), projectNodes)
        }

        return tableModel.map { it -> arrayOf(it.moduleName, it.moduleEnabled) }
    }

    private fun proceedNode(node : ASTNode, nodes : MutableList<ASTNode>) {
        val extractedPath = modulePathRegex.find(node.text)?.value

        if (extractedPath == null) {
            nodes.remove(node)
            return
        }

        val pairNode = nodes.find { it.text.contains("${extractedPath.subSequence(0, extractedPath.length - 1)}/stub'") }

        if (pairNode == null) {
            nodes.remove(node)
            return
        }

        val moduleName = moduleNameRegex.find(node.text)?.value
        val moduleEnabled = !node.text.contains("//")

        val stubModuleName = moduleNameRegex.find(node.text)?.value
        val stubModuleEnabled = !pairNode.text.contains("//")

        if (moduleName == null || stubModuleName == null) {
            nodes.remove(node)
            nodes.remove(pairNode)
            return
        }

        tableModel.add(RowModelWithNode(moduleName, moduleEnabled, node))
        tableModel.add(RowModelWithNode("$stubModuleName-stub", stubModuleEnabled, node))

        nodes.remove(node)
        nodes.remove(pairNode)
    }
}

data class RowModelWithNode(val moduleName: String, val moduleEnabled: Boolean, val moduleNode: ASTNode)