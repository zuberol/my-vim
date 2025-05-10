package com.github.zuberol.myvim

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.ActionPlan
import com.intellij.openapi.editor.actionSystem.TypedActionHandlerEx

class NoWritePre : TypedActionHandlerEx {

    private val log = logger<NoWritePre>()

    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        log.debug("zeauberg: execute start")

        TODO("zeauberg typed $charTyped")
    }

    override fun beforeExecute(editor: Editor, c: Char, context: DataContext, plan: ActionPlan) {
        log.debug("zeauberg: calling execute")
        execute(editor, c, context)
    }
}