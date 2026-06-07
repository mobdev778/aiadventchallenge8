package com.github.mobdev778.aiadventchallenge.app

import com.github.mobdev778.aiadventchallenge.data.di.networkModule
import com.github.mobdev778.aiadventchallenge.data.di.dataChatModule
import com.github.mobdev778.aiadventchallenge.data.di.dataImageModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainChatModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageGeneratorModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainImageModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainReasoningStrategyModule
import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.ImageGenerator
import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.gpt.GptImageGenerator
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    startKoin {
        modules(networkModule)
        modules(dataChatModule)
        modules(dataImageModule)
        modules(domainChatModule)
        modules(domainImageModule)
        modules(domainReasoningStrategyModule)
        modules(domainImageGeneratorModule)
    }

    val imageGenerator: ImageGenerator by inject(GptImageGenerator::class.java)

    runBlocking {
        for (temperature in listOf(0.0, 0.7, 1.2)) {
            val prompt = "Red rose in a glass"
            val image = imageGenerator.generateImage(temperature, prompt)
            try {
                ImageIO.write(
                    image,
                    "png",
                    File("${System.getProperty("user.dir")}/generated_svg_${temperature}.png")
                )
            } catch (e: Exception) {
            }
        }
    }
}