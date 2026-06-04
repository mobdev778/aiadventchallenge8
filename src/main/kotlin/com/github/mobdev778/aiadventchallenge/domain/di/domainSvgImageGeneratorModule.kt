package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.svgimagegenerator.SvgImageGenerator
import org.koin.dsl.module

val domainSvgImageGeneratorModule = module {
    single<SvgImageGenerator> {
        SvgImageGenerator(client = get())
    }
}
