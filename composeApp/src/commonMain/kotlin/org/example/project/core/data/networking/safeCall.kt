package org.example.project.core.data.networking

import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import org.example.project.core.domain.util.NetworkError
import org.example.project.core.domain.util.Result
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(
    apiCall: () -> HttpResponse
): Result<T, NetworkError> {
    val response = try {
        apiCall()
    } catch (e: UnresolvedAddressException) {
        return Result.Error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        return Result.Error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Result.Error(NetworkError.UNKNOWN)
    }
    return responseToResult(response)
}