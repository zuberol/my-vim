package com.github.zuberol.myvim

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile


object Vim {
    var mode: Mode = Normal
}

sealed interface Mode

data object Normal : Mode
data object Insert : Mode
data object Visual : Mode
data class OpPending(val prev: Char) : Mode


class NoWriteCodeInsides : TypedHandlerDelegate() {
    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        thisLogger().debug("char typed: $c")
        thisLogger().debug("current mode: ${Vim.mode}")


        when(Vim.mode) {
            Insert -> {
                if(c != 'e') {
                    Vim.mode = Normal
                    return Result.STOP
                }
                return Result.CONTINUE
            }
            Normal -> {
                if(c == 'i') {
                    Vim.mode = Insert
                    return Result.STOP
                } else if ( c == 'd') {
                    Vim.mode = OpPending(c)
                    return Result.STOP
                }
                TODO("you can only go to insert mode right now")
            }
            Visual -> TODO()
            is OpPending -> {
                val prev = (Vim.mode as OpPending).prev
                if( prev == 'd') {
                    if(c == 'l') {
                        runWriteCommandAction(project) {
                            val start = editor.caretModel.currentCaret.offset
                            val line = editor.document.getLineNumber(editor.caretModel.offset)
                            val end = editor.document.getLineEndOffset(line)
                            editor.document.deleteString(start, end)
                            //editor.document.replaceString(start, end, "XXXX_XXXX")
                            //editor.document.immutableCharSequence
                            //editor.document.charsSequence
                        }
                        return Result.STOP
                    }
                    TODO("only delete end line")
                }
                TODO("only delete is supported")
            }
        }


//        val res =  when(c) {
//            'h', 'k', 'j', 'l' -> {
//                val document = editor.document
//                val runnable = Runnable { document.insertString(0, c.toString()) }
//                runWriteCommandAction(project, runnable)
//                Result.STOP
//            }
//            'i' -> {
//                thisLogger().debug("insert mode active")
//                Vim.mode = Insert
//                Result.STOP
//            }
//            'e' -> {
//                Vim.mode = Normal
//                Result.STOP
//            }
//            else -> {
//                Result.DEFAULT
//            }
//        }
//
//        thisLogger().debug("new mode: ${Vim.mode}")

        TODO()
    }
}