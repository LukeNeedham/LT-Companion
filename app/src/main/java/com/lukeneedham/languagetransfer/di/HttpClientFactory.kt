package com.lukeneedham.languagetransfer.di

import com.lukeneedham.common.network.NetworkLogInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

/** The one HTTP client of the app: Ktor on OkHttp, with every call recorded for the debug kit network page. */
object HttpClientFactory {
    fun create(): HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation)
            engine { addInterceptor(NetworkLogInterceptor()) }
        }
}
