package com.github.zuberol.myvim

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.ActionPlan
import com.intellij.openapi.editor.actionSystem.TypedActionHandlerEx
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.application.ApplicationManager

class NoWritePre : TypedActionHandlerEx {

    private val log = logger<NoWritePre>()

    override fun execute(editor: Editor, charTyped: Char, dataContext: DataContext) {
        log.debug("zeauberg: execute start")

        when(Vim.mode) {
            Insert -> {
                if(charTyped != 'e') {
                    Vim.mode = Normal
                    return
                }
                return
            }
            Normal -> {
                when (charTyped) {
                    'i' -> {
                        Vim.mode = Insert
                        return
                    }
                    'd' -> {
                        Vim.mode = OpPending(charTyped)
                        return
                    }
                    'v' -> {
                        Vim.mode = Visual
                        ApplicationManager.getApplication().invokeAndWait {
                            editor.caretModel.primaryCaret.visualAttributes = BLOCK
                        }
                        return
                    }
                    else -> TODO("you can only go to insert mode right now")
                }
            }
            Visual -> TODO()
            is OpPending -> {
                val prev = (Vim.mode as OpPending).prev
                if( prev == 'd') {
                    if(charTyped == 'l') {
//                        runWriteCommandAction(project) {
//                            val start = editor.caretModel.currentCaret.offset
//                            val line = editor.document.getLineNumber(editor.caretModel.offset)
//                            val end = editor.document.getLineEndOffset(line)
//                            editor.document.deleteString(start, end)
//                        }
                        return
                    }
                    TODO("only delete end line")
                }
                TODO("only delete is supported")
            }
        }



        TODO("zeauberg typed $charTyped")
    }

    override fun beforeExecute(editor: Editor, c: Char, context: DataContext, plan: ActionPlan) {
        log.debug("zeauberg: calling execute")
        execute(editor, c, context)
    }
}