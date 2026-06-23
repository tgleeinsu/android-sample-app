package com.tglee.tgaccount.core.common.asset

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * assets 디렉토리의 JSON 파일을 읽어 역직렬화한다.
 * mock API(FakeService)가 실제 통신 대신 이 로더로 로컬 JSON 을 반환하는 데 사용한다.
 */
@Singleton
class AssetJsonLoader @Inject constructor(
    @param:ApplicationContext @PublishedApi internal val context: Context,
    @PublishedApi internal val json: Json,
) {
    suspend inline fun <reified T> load(fileName: String): T = withContext(Dispatchers.IO) {
        val text = context.assets.open(fileName).bufferedReader().use { it.readText() }
        json.decodeFromString<T>(text)
    }
}
