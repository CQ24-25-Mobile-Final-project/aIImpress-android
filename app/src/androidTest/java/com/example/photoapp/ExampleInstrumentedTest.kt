package com.example.photoapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.hmac.HMACSigner
import io.fusionauth.jwt.hmac.HMACVerifier

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.photoapp", appContext.packageName)
    }

    @Test()
    fun `aduvop`() {
        val signer = HMACSigner.newSHA256Signer("too many secrets")

        val jwt = JWT().setIssuer("www.cukhoaimon.com")
            .setSubject("deptrai")
            .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))
            .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(60));

        val encodedJWT = JWT.getEncoder().encode(jwt, signer)

        print(encodedJWT)

        val verifier = HMACVerifier.newVerifier("too many secrets");

        val decodedJwt = JWT.getDecoder().decode(encodedJWT, verifier);
        assertEquals(decodedJwt.subject, "www.cukhoaimon.com");
    }
}