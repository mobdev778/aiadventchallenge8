package com.github.mobdev778.aiadventchallenge.presentation.app

import com.github.mobdev778.aiadventchallenge.data.di.dataChatHistoryModule
import com.github.mobdev778.aiadventchallenge.data.di.dataChatModule
import com.github.mobdev778.aiadventchallenge.data.di.dataImageModule
import com.github.mobdev778.aiadventchallenge.data.di.networkModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainChatModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageGeneratorModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainProfileModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainReasoningStrategyModule
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class PluginInitializer : ProjectActivity {

    override suspend fun execute(project: Project) {
        ensureKoinStarted()
    }

    companion object {
        /**
         * Toolwindows can be created before `postStartupActivity` runs.
         * This method makes DI initialization idempotent and safe to call from anywhere.
         */
        fun ensureKoinStarted() {
            if (GlobalContext.getOrNull() != null) return

            startKoin {
                modules(
                    domainProfileModule,
                    networkModule,
                    dataChatModule,
                    dataImageModule,
                    dataChatHistoryModule,
                    domainChatModule,
                    domainImageModule,
                    domainReasoningStrategyModule,
                    domainImageGeneratorModule,
                )
            }
        }
    }
}
