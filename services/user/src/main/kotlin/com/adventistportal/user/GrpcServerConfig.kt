package com.adventistportal.user

import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * The gRPC server, on its own port.
 *
 * Not published anywhere: it is reachable only from inside the network, like the HTTP port
 * is, and for the same reason — it answers questions about users with no token attached,
 * because the only callers are other services.
 */
@Configuration
class GrpcServerConfig {

    @Bean(destroyMethod = "shutdownNow")
    fun grpcServer(
        services: List<BindableService>,
        @Value("\${grpc.server.port:9090}") port: Int,
    ): Server = ServerBuilder.forPort(port)
        .apply { services.forEach(::addService) }
        .build()
        .start()
}
