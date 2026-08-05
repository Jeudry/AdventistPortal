package com.adventistportal.core.services

import com.adventistportal.core.domain.exceptions.InvalidTokenEx
import com.adventistportal.core.domain.types.UserId
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Date
import java.util.UUID
import kotlin.io.encoding.Base64

/**
 * Issues and verifies access tokens, signed with RSA.
 *
 * The signature used to be HMAC, which meant every process that could *check* a token
 * could also *mint* one — the gateway included, and the gateway is the thing exposed to
 * the internet. Only the user service holds the private key now; everyone else gets the
 * public half, and there is nothing to be done with it but verify.
 *
 * A service configured without a private key cannot sign, and says why rather than
 * failing obscurely at the point of use.
 */
@Service
class JwtService(
    @param:Value("\${jwt.public-key}") publicKeyBase64: String,
    @param:Value("\${jwt.private-key:}") privateKeyBase64: String,
    @param:Value("\${jwt.expiration-minutes:60}") expirationMinutes: Int,
) {
    private val publicKey: PublicKey = publicKeyBase64.toPublicKey()
    private val privateKey: PrivateKey? = privateKeyBase64.takeIf(String::isNotBlank)?.toPrivateKey()

    private val accessTokenValidityMs: Long = expirationMinutes * 60 * 1000L
    val refreshTokenValidityMs: Long = 30L * 24 * 60 * 60 * 1000

    fun generateAccessToken(userId: UserId): String = generateToken(userId, ACCESS, accessTokenValidityMs)

    fun generateRefreshToken(userId: UserId): String = generateToken(userId, REFRESH, refreshTokenValidityMs)

    fun validateAccessToken(token: String): Boolean = tokenTypeOf(token) == ACCESS

    fun validateRefreshToken(token: String): Boolean = tokenTypeOf(token) == REFRESH

    fun getUserIdFromToken(token: String): UserId {
        val claims = parseAllClaims(token) ?: throw InvalidTokenEx("The attached token is invalid.")
        return UUID.fromString(claims.subject)
    }

    private fun generateToken(userId: UserId, type: String, validityMs: Long): String {
        val signingKey = requireNotNull(privateKey) {
            "This service has no jwt.private-key and cannot issue tokens; only the user service should."
        }

        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim(TYPE_CLAIM, type)
            .issuedAt(now)
            .expiration(Date(now.time + validityMs))
            .signWith(signingKey, Jwts.SIG.RS256)
            .compact()
    }

    private fun tokenTypeOf(token: String): String? = parseAllClaims(token)?.get(TYPE_CLAIM) as? String

    private fun parseAllClaims(token: String): Claims? {
        val rawToken = token.removePrefix(BEARER_PREFIX)

        return try {
            Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        } catch (e: Exception) {
            null
        }
    }

    private fun String.toPublicKey(): PublicKey = KeyFactory.getInstance(RSA)
        .generatePublic(X509EncodedKeySpec(decodeKey()))

    private fun String.toPrivateKey(): PrivateKey = KeyFactory.getInstance(RSA)
        .generatePrivate(PKCS8EncodedKeySpec(decodeKey()))

    /** Accepts the key with or without PEM armour, so either form survives an environment variable. */
    private fun String.decodeKey(): ByteArray = Base64.Default.decode(replace(PEM_ARMOUR, ""))

    private companion object {
        const val ACCESS = "access"
        const val REFRESH = "refresh"
        const val TYPE_CLAIM = "type"
        const val BEARER_PREFIX = "Bearer "
        const val RSA = "RSA"
        val PEM_ARMOUR = Regex("-----(BEGIN|END)[A-Z ]*-----|\\s")
    }
}
