package com.github.zuberol.myvim.startup

import com.github.zuberol.myvim.NoWritePre
import com.github.zuberol.myvim.OnEscape
import com.intellij.ide.util.RunOnceUtil.runOnceForApp
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class InitPlugin : ProjectActivity {
    override suspend fun execute(project: Project) {
        val log = logger<InitPlugin>()
        log.warn("init plugin")
        val ret = runOnceForApp("task-id") {
            log.warn("init plugin 2")
//            TypedAction.getInstance() TODO
//                .setupRawHandler(NoWritePre())
                //.setupHandler()

        }
        TypedAction.getInstance()
            .setupRawHandler(NoWritePre())
        log.warn("end init")

        val am = EditorActionManager.getInstance()

        am.setActionHandler("EditorEscape", OnEscape())

    }
}

private fun registerTypedAction() {
    /*
        EditorActionManager actionManager = EditorActionManager.getInstance();
    TypedAction typedAction = actionManager.getTypedAction();
    typedAction.setupHandler(new MyTypedHandler());
     */
    // EditorActionManager.getInstance().typedAction

    // @Deprecated
  //  TypedAction.getInstance().setupHandler()
/*]
use <typedHandler> extension point to register
  private static final ExtensionPointName<EditorTypedHandlerBean> EP_NAME = new ExtensionPointName<>("com.intellij.editorTypedHandler");
  private static final ExtensionPointName<EditorTypedHandlerBean> RAW_EP_NAME = new ExtensionPointName<>("com.intellij.rawEditorTypedHandler");

summary:
        <typedHandler id="some-arbitrary-id-one" implementation="com.github.zuberol.myvim.NoWrite" />

 */

}