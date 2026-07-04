package com.github.mobdev778.aiadventchallenge.presentation.app

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.koin.java.KoinJavaComponent.inject

@Service(Service.Level.APP)
class PluginShutdownHandlerService : Disposable {

    override fun dispose() {
        val scope: CoroutineScope by inject(CoroutineScope::class.java)
        scope.cancel()
    }
}
