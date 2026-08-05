package com.adventistportal.chat.infra.users

import com.adventistportal.contracts.user.v1.UsersGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UsersChannelConfig {

    /** Plaintext: the call never leaves the internal network, where nothing else can listen. */
    @Bean(destroyMethod = "shutdownNow")
    fun usersChannel(@Value("\${adventistportal.users.grpc-target:localhost:9090}") target: String): ManagedChannel =
        ManagedChannelBuilder.forTarget(target).usePlaintext().build()

    /**
     * The stub without a deadline. The deadline is applied per call, in
     * [MissingParticipantRepair].
     *
     * `withDeadlineAfter` on a bean would compute one absolute instant when the bean is
     * built, not a fresh allowance per call — so every call after the first few seconds of
     * uptime starts already expired, and the error says the deadline passed minutes ago.
     */
    @Bean
    fun usersStub(channel: ManagedChannel): UsersGrpc.UsersBlockingStub = UsersGrpc.newBlockingStub(channel)
}
