package com.adventistportal.user.api.grpc

import com.adventistportal.contracts.user.v1.GetUserRequest
import com.adventistportal.contracts.user.v1.GetUserResponse
import com.adventistportal.contracts.user.v1.User
import com.adventistportal.contracts.user.v1.UsersGrpc
import com.adventistportal.user.infrastructure.database.repositories.UserRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * The one thing another service may ask this one directly.
 *
 * It exists so a projection built from events has a way back when an event never arrived.
 * Answering "no such user" is a real answer and distinct from failing: the first tells the
 * caller its empty projection was correct, the second tells it to try again.
 */
@Component
class UsersGrpcService(private val users: UserRepository) : UsersGrpc.UsersImplBase() {

    @Transactional(readOnly = true)
    override fun getUser(request: GetUserRequest, observer: StreamObserver<GetUserResponse>) {
        val userId = runCatching { UUID.fromString(request.userId) }.getOrNull()
        if (userId == null) {
            observer.onError(Status.INVALID_ARGUMENT.withDescription("user_id is not a UUID").asException())
            return
        }

        val found = users.findById(userId).orElse(null)
        val response = GetUserResponse.newBuilder()
            .apply { found?.let { user = it.asProto() } }
            .build()

        observer.onNext(response)
        observer.onCompleted()
    }

    private fun com.adventistportal.user.infrastructure.database.entities.UserEntity.asProto(): User =
        User.newBuilder()
            .setUserId(requireNotNull(id).toString())
            .setEmail(email)
            .setUsername(username)
            .apply { profilePictureUrl?.let { profilePictureUrl = it } }
            .build()
}
