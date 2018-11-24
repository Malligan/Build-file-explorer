package com.alged.bfe.model

import com.alged.bfe.extensions.haveOriginalName
import com.alged.bfe.extensions.moduleName
import com.intellij.lang.ASTNode

data class Module(val name: String,
                  val enabled: Boolean,
                  val node: ASTNode?,
                  val original: Boolean = false,
                  val group: List<Module> = listOf()) {
    companion object Factory {
        fun separator(): Module {
            return Module("", false, null)
        }

        fun fromASTNode(node: ASTNode, configuration: ModulesSelectionConfiguration): Module {
            return Module(node.moduleName(configuration), !node.text.contains("//"), node, node.haveOriginalName(configuration))
        }
    }
}