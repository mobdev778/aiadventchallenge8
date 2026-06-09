package com.github.mobdev778.aiadventchallenge.domain.profile

class PaidAppProfile : AppProfile{
    override val apiKey = System.getenv("AI_PROXY_API_KEY")
    override val baseUrl = System.getenv("AI_PROXY_BASE_URL")
    override val baseModel = System.getenv("AI_PROXY_MODEL")
}