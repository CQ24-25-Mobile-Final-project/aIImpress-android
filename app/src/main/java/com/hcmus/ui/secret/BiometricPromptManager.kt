package com.hcmus.ui.secret

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(private val activity: ComponentActivity) {

    private val fragmentActivity: FragmentActivity
        get() = activity as? FragmentActivity
            ?: throw IllegalArgumentException("Activity must be a FragmentActivity")

    private val resultChannel = Channel<BiometricResult>(Channel.BUFFERED)
    val promptResults = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(title: String, description: String) {
        val manager = BiometricManager.from(activity)
        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL // API >= 30 support BIOMETRIC_STRONG and DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG


        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(true)

        // Add "Cancel" button for device with API < 30
        if (Build.VERSION.SDK_INT < 30) {
            promptInfo.setNegativeButtonText("Cancel")
        }


        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.FeatureNotSupported)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.AuthenticationCanceled)
                return
            }
            else -> Unit
        }

        val prompt = BiometricPrompt(
            fragmentActivity,
            fragmentActivity.mainExecutor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )

        //Notification authentication
        prompt.authenticate(promptInfo.build())
    }

    sealed interface BiometricResult {
        object HardwareUnavailable : BiometricResult
        object FeatureNotSupported : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        object AuthenticationFailed : BiometricResult
        object AuthenticationSuccess : BiometricResult
        object AuthenticationCanceled : BiometricResult
    }
}