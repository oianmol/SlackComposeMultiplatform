package dev.baseio.security

import android.app.KeyguardManager
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.protobuf.ByteString
import dev.baseio.slackdata.securepush.KMKeyAlgorithm
import dev.baseio.slackdata.securepush.kmSecureNotification
import io.grpc.ManagedChannel
import org.example.common.R
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.util.UUID

/**
 * Contains common helper functions used by the Android classes.
 */
object Utils {
    private const val CHANNEL_ID = "default_channel"
    private val PREF_NAME = String.format("%s_preferences", Utils::class::qualifiedName)
    private const val HOST_KEY = "host"
    private const val PORT_KEY = "port"
    private const val USER_ID_KEY = "user_id"
    private const val KEYSTORE_ANDROID = "AndroidKeyStore"

    /**
     * Initializes the Android security provider and the Capillary library.
     */
    fun initialize(context: Context) {
        updateAndroidSecurityProvider(context)
        try {
            Capillary.initialize()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }
    }

    private fun updateAndroidSecurityProvider(context: Context) {
        try {
            ProviderInstaller.installIfNeeded(context)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play services is out of date, disabled, etc.
            e.printStackTrace()
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance()
                .showErrorNotification(context, e.getConnectionStatusCode())
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates a non-recoverable error; the ProviderInstaller is not able
            // to install an up-to-date Provider.
            e.printStackTrace()
        }
    }

    /**
     * Creates a demo notification message and returns its serialized bytes.
     *
     * @param title the title of the notification.
     * @param keyAlgorithm the algorithm used to encrypt the notification.
     * @param isAuthKey whether the notification was encrypted using an authenticated key.
     * @return serialized notification bytes.
     */
    fun createSecureMessageBytes(
        messageTitle: String?, keyAlgorithm: KMKeyAlgorithm?, isAuthKey: Boolean
    ): ByteString {
        return kmSecureNotification {
            id = System.currentTimeMillis().toInt()
            title = messageTitle
            body = java.lang.String.format("Algorithm=%s, IsAuth=%s", keyAlgorithm, isAuthKey)
        }
    }

    /**
     * Shows the given notification message as an Android notification.
     */
    fun showNotification(context: Context, secureNotification: KMSecureNotification) {

    }

    /**
     * Creates a Capillary key manager for the specified key algorithm.
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    fun getKeyManager(context: Context, algorithm: KMKeyAlgorithm?): KeyManager {
        return when (algorithm) {
            KMKeyAlgorithm.RSA_ECDSA -> {
                context.resources.openRawResource(R.raw.sender_verification_key).use { senderVerificationKey ->
                    return RsaEcdsaKeyManager.getInstance(
                        context, RSA_ECDSA_KEYCHAIN_ID, senderVerificationKey
                    )
                }
                WebPushKeyManager.getInstance(context, AndroidConstants.WEB_PUSH_KEYCHAIN_ID)
            }

            KMKeyAlgorithm.WEB_PUSH -> WebPushKeyManager.getInstance(context, AndroidConstants.WEB_PUSH_KEYCHAIN_ID)
            else -> throw IllegalArgumentException("unsupported key algorithm")
        }
    }

    /**
     * Saves the specified gRPC channel host and port in [SharedPreferences].
     */
    fun addGrpcChannelParams(context: Context, host: String?, port: Int) {
        getSharedPreferences(context).edit().putString(HOST_KEY, host).putInt(PORT_KEY, port).apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val storageContext = getDeviceProtectedStorageContext(context)
        return storageContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getDeviceProtectedStorageContext(context: Context): Context {
        return context.createDeviceProtectedStorageContext()
    }

    /**
     * Removes gRPC channel host and port in [SharedPreferences].
     */
    fun clearGrpcChannelParams(context: Context) {
        getSharedPreferences(context).edit().remove(HOST_KEY).remove(PORT_KEY).apply()
    }

    /**
     * Returns the saved gRPC channel host.
     */
    fun getGrpcChannelHost(context: Context): String? {
        return getSharedPreferences(context).getString(HOST_KEY, null)
    }

    /**
     * Returns the saved gRPC channel port.
     */
    fun getGrpcChannelPort(context: Context): Int {
        return getSharedPreferences(context).getInt(PORT_KEY, 0)
    }

    /**
     * Creates a new gRPC channel to the host and port combination stored in
     * [SharedPreferences].
     */
    fun createGrpcChannel(context: Context): ManagedChannel {
        val sharedPreferences: SharedPreferences = getSharedPreferences(context)
        val host: String = sharedPreferences.getString(HOST_KEY, null)
            ?: throw IOException("missing host")
        val port: Int = sharedPreferences.getInt(PORT_KEY, -1)
        if (port == -1) {
            throw IOException("missing port")
        }
        context.resources.openRawResource(R.raw.tls)
            .use { certStream -> return TlsOkHttpChannelGenerator.generate(host, port, certStream) }
    }

    /**
     * Returns a demo user ID for the current app instance.
     */
    fun getUserId(context: Context): String {
        val sharedPreferences: SharedPreferences = getSharedPreferences(context)
        var userId: String? = sharedPreferences.getString(USER_ID_KEY, null)
        if (userId == null) {
            userId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
        }
        return userId
    }

    /**
     * Checks if the device supports generating authenticated Capillary keys or throws an exception if
     * it doesn't.
     */
    fun checkAuthModeIsAvailable(context: Context) {
        val isScreenLockEnabled = isScreenLockEnabled(context)
        if (isScreenLockEnabled && !isScreenLocked(context)) {
            return
        }
        if (!isScreenLockEnabled) {
            throw Exception(
                "the device is not secured with a PIN, pattern, or password"
            )
        }
        throw Exception("the device is locked")
    }

    /**
     * Checks if the device screen lock is enabled. Returns the status as a boolean.
     */
    private fun isScreenLockEnabled(context: Context): Boolean {
        val keyguardManager = (context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
        return keyguardManager.isDeviceSecure
    }

    /**
     * Checks if the device is locked. Returns the status as a boolean.
     */
    fun isScreenLocked(context: Context): Boolean {
        val keyguardManager = (context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager)
        return keyguardManager.isDeviceLocked
    }

    fun loadKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_ANDROID)
        try {
            keyStore.load(null)
        } catch (e: IOException) {
            throw GeneralSecurityException("unable to load keystore", e)
        }
        return keyStore
    }
}