package com.alged.bfe

import com.alged.bfe.model.RowModelWithNode
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile

class ModulesSelectionService(private val configuration: ModulesSelectionConfiguration) {

    fun buildTableModel(file: PsiFile): MutableList<RowModelWithNode> {
        val localTableModel: MutableList<RowModelWithNode> = mutableListOf()
        val fileASTNode = file.node
        val nodes = fileASTNode.getChildren(null)

        val projectNodes: MutableList<ASTNode> = nodes.filter {
            it.text.contains("project")
        }.toMutableList()

        while (projectNodes.isNotEmpty()) {
            proceedNode(localTableModel, projectNodes.first(), projectNodes)
        }

        return localTableModel
    }

    private fun proceedNode(tableModel: MutableList<RowModelWithNode>, node: ASTNode, nodes: MutableList<ASTNode>) {
        val itemsForModel = mutableListOf<Triple<ASTNode, String, Boolean>>()
        val moduleNamePath = configuration.modulePath.toRegex().find(node.text)?.value

        if (moduleNamePath == null) {
            nodes.remove(node)
            return
        } else {
            itemsForModel.add(Triple(node, moduleNamePath, true))
        }

        for (moduleEndingVariant in configuration.moduleEndings) {
            val moduleNamePathVariant = "${moduleNamePath.subSequence(0, moduleNamePath.length - 1)}$moduleEndingVariant'"
            val nodeVariant = nodes.find { it.text.contains(moduleNamePathVariant) }

            if (nodeVariant != null) {
                itemsForModel.add(Triple(nodeVariant, moduleNamePathVariant, false))
            }
        }

        //no variants found, exclude node and continue
        if (itemsForModel.count() == 1) {
            nodes.remove(node)
            return
        }

        for (item in itemsForModel) {
            tableModel.add(RowModelWithNode(item.second, !item.first.text.contains("//"), item.first, item.third))
            nodes.remove(item.first)
        }
    }
}

data class ModulesSelectionConfiguration(val modulePath: String = """('[^:]\S+')+""",
                                         val moduleEndings: List<String> = listOf("/stub"),
                                         val dualModuleMode: Boolean = moduleEndings.count() == 1)

//multi modules mode
//loop for all module variants, if at least 2 types of same name module exists add them to editor
//for example there is will be "/stub" and "/dev" modules.

//dual module mode
//add modules with selected type, single line for module in table, disabling = enabling first module variant in list of types(endings), enabling = enabling original module
