package com.alged.bfe.model

import com.intellij.lang.ASTNode

data class RowModelWithNode(val moduleName: String,
                            val moduleEnabled: Boolean,
                            val moduleNode: ASTNode?,
                            val moduleOriginal: Boolean = false,
                            val header: Boolean = false)