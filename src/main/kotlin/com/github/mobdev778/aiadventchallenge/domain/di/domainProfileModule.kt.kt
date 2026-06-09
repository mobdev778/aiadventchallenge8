package com.github.mobdev778.aiadventchallenge.domain.di

import com.github.mobdev778.aiadventchallenge.domain.profile.AppProfile
import com.github.mobdev778.aiadventchallenge.domain.profile.FreeAppProfile
import org.koin.dsl.module

val domainProfileModule = module {
    single<AppProfile> {
        FreeAppProfile()
    }
}