package com.github.zuberol.myvim

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.CaretVisualAttributes
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.VisualPosition
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

val BLOCK =
    CaretVisualAttributes(null, CaretVisualAttributes.Weight.NORMAL, CaretVisualAttributes.Shape.BLOCK, 1.0F)
val BAR =
    CaretVisualAttributes(null, CaretVisualAttributes.Weight.NORMAL, CaretVisualAttributes.Shape.BAR, 0.25F)

class NoWriteCodeInsides : TypedHandlerDelegate() {
    private val log: Logger = thisLogger()
    override fun beforeCharTyped(c: Char, project: Project, editor: Editor, file: PsiFile, fileType: FileType): Result {
        log.debug("char typed = \"$c\", mode =${Vim.mode}")
        return when(Vim.mode) {
            Insert -> {
                editor.redrawCaret(BAR)
                Result.CONTINUE
            }
            Normal -> {
                editor.redrawCaret(BLOCK)
                when (c) {
                    'i' -> {
                        Vim.mode = Insert
                        Result.STOP
                    }
                    'd' -> {
                        Vim.mode = OpPending(c)
                        Result.STOP
                    }
                    'v' -> {
                        Vim.mode = Visual
                        editor.redrawCaret(BLOCK)
                        Result.STOP
                    }
                    'h' -> {
                        val pos = editor.caretModel.visualPosition
                        editor.caretModel.currentCaret
                            .moveToVisualPosition(VisualPosition(
                                pos.line,
                                pos.column - 1
                            ))
                        Result.STOP
                    }
                    'j' -> {
                        val pos = editor.caretModel.visualPosition
                        editor.caretModel.currentCaret
                            .moveToVisualPosition(VisualPosition(
                                pos.line + 1,
                                pos.column
                            ))
                        Result.STOP
                    }
                    'k' -> {
                        val pos = editor.caretModel.visualPosition
                        editor.caretModel.currentCaret
                            .moveToVisualPosition(VisualPosition(
                                pos.line - 1,
                                pos.column
                            ))
                        Result.STOP
                    }
                    'l' -> {
                        val pos = editor.caretModel.visualPosition
                        editor.caretModel.currentCaret
                            .moveToVisualPosition(VisualPosition(
                                pos.line,
                                pos.column + 1
                            ))
                        Result.STOP
                    }
                    else -> {
                        log.debug("no action")
                        Result.CONTINUE
                    }
                }
            }
            Visual -> {
                log.debug("not implemented")
                Result.CONTINUE
            }
            is OpPending -> {
                editor.redrawCaret(BAR)
                val prev = (Vim.mode as OpPending).prev
                if( prev == 'd') {
                    when(c) {
                        'e' -> {
                            runWriteCommandAction(project) {
                                val start = editor.caretModel.currentCaret.offset
                                val line = editor.document.getLineNumber(editor.caretModel.offset)
                                val end = editor.document.getLineEndOffset(line)
                                editor.document.deleteString(start, end)
                            }
                            Vim.mode = Normal
                            Result.STOP
                        }
                        'h' -> {
                            runWriteCommandAction(project) {
                                val start = editor.caretModel.currentCaret.offset
                                editor.document.deleteString(start - 1 , start)
                            }
                            Vim.mode = Normal
                            Result.STOP
                        }
                        'l' -> {
                            runWriteCommandAction(project) {
                                val start = editor.caretModel.currentCaret.offset
                                editor.document.deleteString(start, start + 1)
                            }
                            Vim.mode = Normal
                            Result.STOP
                        }
                        'j' -> {
                            val line = editor.document.getLineNumber(editor.caretModel.offset)
                            val col = editor.caretModel.currentCaret.offset - editor.document.getLineStartOffset(line)
                            val dest = editor.document.getLineStartOffset(line + 1)
//                            log.warn("offset: $line, col: ${col}, dest: ${dest + col}")
                            runWriteCommandAction(project) {
                                editor.document.deleteString(editor.caretModel.currentCaret.offset, dest + col)
                            }
                            Vim.mode = Normal
                            Result.STOP
                        }
                        'k' -> {
                            val line = editor.document.getLineNumber(editor.caretModel.offset)
                            val col = editor.caretModel.currentCaret.offset - editor.document.getLineStartOffset(line)
                            val dest = editor.document.getLineStartOffset(line - 1)
                            runWriteCommandAction(project) {
                                editor.document.deleteString(dest + col, editor.caretModel.currentCaret.offset)
                            }
                            Vim.mode = Normal
                            Result.STOP
                        }
                        else -> {
                            log.warn("not implemented")
                        }
                    }
                    log.warn("only delete end line")
                }
                log.warn("only delete is supported")
                Result.CONTINUE
            }
        }
    }

    private fun Editor.redrawCaret(style: CaretVisualAttributes) {
        ApplicationManager.getApplication().invokeAndWait {
            caretModel.primaryCaret.visualAttributes = style
        }
    }
}