package io.paku.climblog.core

sealed interface CommonError {
    data object Unknown: CommonError
    data object PoorNetwork: CommonError
    data object NoNetwork: CommonError
    data object UnAuthorized: CommonError
    data object NoLoginId: CommonError
    data object NoMemberId: CommonError
}