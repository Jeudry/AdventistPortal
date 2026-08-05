package com.adventistportal.chat.infra.users

import com.adventistportal.chat.domain.models.ChatParticipant
import com.adventistportal.contracts.user.v1.GetUserRequest
import com.adventistportal.contracts.user.v1.UsersGrpc
import com.adventistportal.core.domain.types.UserId
import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Asks the user service about someone this service has never heard of.
 *
 * The participants here are a projection of `UserEvent.Created`. If that event never
 * arrived — chat rebuilt from an empty database, or the message dead-lettered after
 * failing — the projection has a hole in it and nothing will ever fill it: events are not
 * replayed, and creating a chat with that person fails forever.
 *
 * This is the way back, and the only synchronous call between services. It runs on a miss,
 * never on the happy path, so the coupling it introduces costs nothing while both sides
 * are healthy.
 */
@Component
class MissingParticipantRepair(
    private val users: UsersGrpc.UsersBlockingStub,
    @param:Value("\${adventistportal.users.timeout-seconds:3}") private val timeoutSeconds: Long,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Null means the user service says there is no such person — the projection was right
     * to be empty. A failed call throws, because "we could not ask" and "the answer is no"
     * must not look the same to the caller.
     */
    fun lookUp(userId: UserId): ChatParticipant? {
        logger.info("No local participant for $userId; asking the user service")

        val response = try {
            // The allowance is per call. A stalled user service must not become a stalled
            // chat service, holding the request thread until something else gives up.
            users.withDeadlineAfter(timeoutSeconds, TimeUnit.SECONDS)
                .getUser(GetUserRequest.newBuilder().setUserId(userId.toString()).build())
        } catch (failure: StatusRuntimeException) {
            logger.error("Could not reach the user service to look up $userId", failure)
            throw failure
        }

        if (!response.hasUser()) return null

        return response.user.let {
            ChatParticipant(
                userId = UserId.fromString(it.userId),
                username = it.username,
                email = it.email,
                profilePictureUrl = it.profilePictureUrl.takeIf(String::isNotBlank),
            )
        }
    }
}
