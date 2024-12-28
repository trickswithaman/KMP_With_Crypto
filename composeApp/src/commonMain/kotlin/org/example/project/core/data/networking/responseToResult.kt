package org.example.project.core.data.networking

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import org.example.project.core.domain.util.NetworkError
import org.example.project.core.domain.util.Result

suspend inline fun  <reified T> responseToResult(
    response: HttpResponse
): org.example.project.core.domain.util.Result<T, NetworkError>{
    return when(response.status.value){
        in 200..299 -> {
            try {
                org.example.project.core.domain.util.Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                org.example.project.core.domain.util.Result.Error(NetworkError.SERIALIZATION)
            }
        }
        408 -> org.example.project.core.domain.util.Result.Error(NetworkError.REQUEST_TIMEOUT)
        429 -> org.example.project.core.domain.util.Result.Error(NetworkError.TOO_MANY_REQUESTS)
        in 500..599 -> org.example.project.core.domain.util.Result.Error(NetworkError.SERVER_ERROR)
        else -> Result.Error(NetworkError.UNKNOWN)

    }
}