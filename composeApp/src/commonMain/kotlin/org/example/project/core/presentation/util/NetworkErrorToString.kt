package org.example.project.core.presentation.util

import kmp_with_crypto.composeapp.generated.resources.Res
import kmp_with_crypto.composeapp.generated.resources.no_internet
import kmp_with_crypto.composeapp.generated.resources.request_timeout
import kmp_with_crypto.composeapp.generated.resources.serialization_error
import kmp_with_crypto.composeapp.generated.resources.server_error
import kmp_with_crypto.composeapp.generated.resources.too_many_requests
import kmp_with_crypto.composeapp.generated.resources.unknown_error
import org.example.project.core.domain.util.NetworkError


fun NetworkError.toUiString(): UiText {
    val redId = when (this) {
        NetworkError.REQUEST_TIMEOUT -> Res.string.request_timeout
        NetworkError.TOO_MANY_REQUESTS -> Res.string.too_many_requests
        NetworkError.NO_INTERNET -> Res.string.no_internet
        NetworkError.SERVER_ERROR -> Res.string.server_error
        NetworkError.SERIALIZATION -> Res.string.serialization_error
        NetworkError.UNKNOWN -> Res.string.unknown_error
    }
    return UiText.StringResourceId(redId)
}

