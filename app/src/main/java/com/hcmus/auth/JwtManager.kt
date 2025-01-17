package com.hcmus.auth

import io.fusionauth.jwt.Signer
import io.fusionauth.jwt.Verifier
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.hmac.HMACSigner
import io.fusionauth.jwt.hmac.HMACVerifier
import java.time.ZoneOffset
import java.time.ZonedDateTime

class JwtManager {
    val signer: Signer = HMACSigner.newSHA256Signer("too many secrets")
    val verifier: Verifier = HMACVerifier.newVerifier("too many secrets");

    fun sign(): String {
        val jwt = JWT().setIssuer("www.cukhoaimon.com")
            .setSubject("deptrai")
            .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
            .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(60))

        return JWT.getEncoder().encode(jwt, signer)
    }

    fun verify(token: String): Boolean {
        return JWT.getDecoder().decode(token, verifier).isExpired == false
    }






}