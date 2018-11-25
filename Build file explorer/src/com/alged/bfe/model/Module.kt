package com.alged.bfe.model

import com.alged.bfe.extensions.haveOriginalName
import com.alged.bfe.extensions.moduleName
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.DummyHolderElement

data class Module(val name: String,
                  val enabled: Boolean,
                  val node: ASTNode = DummyHolderElement(""),
                  val original: Boolean = false,
                  val group: List<Module> = listOf(),
                  val file: PsiFile? = null) {
    companion object Factory {
        fun separator(): Module {
            return Module("", false)
        }

        fun fromASTNode(node: ASTNode, configuration: ModulesSelectionConfiguration, psiFile: PsiFile): Module {
            return Module(node.moduleName(configuration), !node.text.contains("//"), node, node.haveOriginalName(configuration), file = psiFile)
        }
    }
}