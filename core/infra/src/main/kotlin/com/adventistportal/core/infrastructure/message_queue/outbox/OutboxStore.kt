package com.adventistportal.core.infrastructure.message_queue.outbox

import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.UUID

/**
 * The outbox table, reached through JDBC rather than JPA.
 *
 * It shares the caller's transaction — Spring hands out the same connection the entity
 * manager is using — which is the entire point: the row and the domain change commit
 * together or neither does.
 *
 * JDBC and not an entity because each service keeps its outbox in its own schema, and an
 * entity would have to name one schema at compile time.
 */
@Repository
class OutboxStore(
    private val db: JdbcClient,
    private val properties: OutboxProperties,
) {
    private val table = "${properties.schema}.outbox"

    fun append(record: OutboxRecord) {
        db.sql(
            "insert into $table (id, exchange, routing_key, proto_type, payload, trace_parent) " +
                "values (?, ?, ?, ?, ?, ?)",
        )
            .params(
                listOf(
                    record.id,
                    record.exchange,
                    record.routingKey,
                    record.protoType,
                    record.payload,
                    record.traceParent,
                ),
            )
            .update()
    }

    /**
     * `skip locked` so that two instances of the same service drain different rows
     * instead of colliding on the same batch.
     */
    fun claimUnsent(batchSize: Int): List<OutboxRecord> = db
        .sql(
            """
            select id, exchange, routing_key, proto_type, payload, trace_parent from $table
            where sent_at is null
            order by created_at
            limit $batchSize
            for update skip locked
            """,
        )
        .query(::toRecord)
        .list()

    fun markSent(ids: Collection<UUID>) {
        if (ids.isEmpty()) return
        db.sql("update $table set sent_at = now() where id in (:ids)")
            .param("ids", ids)
            .update()
    }

    fun recordFailure(id: UUID, error: String) {
        db.sql("update $table set attempts = attempts + 1, last_error = ? where id = ?")
            .params(error.take(MAX_ERROR_LENGTH), id)
            .update()
    }

    private fun toRecord(rs: ResultSet, @Suppress("UNUSED_PARAMETER") rowNum: Int) = OutboxRecord(
        id = rs.getObject("id", UUID::class.java),
        exchange = rs.getString("exchange"),
        routingKey = rs.getString("routing_key"),
        protoType = rs.getString("proto_type"),
        payload = rs.getBytes("payload"),
        traceParent = rs.getString("trace_parent"),
    )

    private companion object {
        const val MAX_ERROR_LENGTH = 2000
    }
}
