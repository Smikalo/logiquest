package com.example.demo1

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform