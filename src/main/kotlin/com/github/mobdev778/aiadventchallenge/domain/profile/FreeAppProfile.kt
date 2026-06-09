package com.github.mobdev778.aiadventchallenge.domain.profile

class FreeAppProfile : AppProfile {
    override val apiKey = ""
    override val baseUrl = "http://localhost:1234/v1/"
    override val baseModel = "qwen/qwen3-14b"
}