package com.github.zuberol.myvim

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler

class OnEscape : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
//        super.doExecute(editor, caret, dataContext)
        val log = logger<OnEscape>()

        log.warn("current mode: ${Vim.mode}")
        log.warn("dynamic handler")

        if(Vim.mode == Insert) {
            Vim.mode = Normal
            log.warn("mode changed to $Normal")
        }
    }
}