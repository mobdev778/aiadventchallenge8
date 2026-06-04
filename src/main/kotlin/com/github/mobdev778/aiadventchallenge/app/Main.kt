package com.github.mobdev778.aiadventchallenge.app

import com.github.mobdev778.aiadventchallenge.data.di.networkModule
import com.github.mobdev778.aiadventchallenge.data.di.dataOpenApiModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainOpenaiModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainReasoningStrategyModule
import com.github.mobdev778.aiadventchallenge.domain.di.domainSvgImageGeneratorModule
import com.github.mobdev778.aiadventchallenge.domain.svgimagegenerator.SvgImageGenerator
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    startKoin {
        modules(networkModule)
        modules(dataOpenApiModule)
        modules(domainOpenaiModule)
        modules(domainReasoningStrategyModule)
        modules(domainSvgImageGeneratorModule)
    }

    val imageGenerator: SvgImageGenerator by inject(SvgImageGenerator::class.java)

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