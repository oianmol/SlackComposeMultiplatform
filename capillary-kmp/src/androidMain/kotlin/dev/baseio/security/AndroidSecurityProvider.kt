package dev.baseio.security

import android.content.Context
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.security.GeneralSecurityException

object AndroidSecurityProvider {
    const val KEYSTORE_ANDROID = "AndroidKeyStore"

    fun initialize(context: Context) {
        updateAndroidSecurityProvider(context)
    }

    private fun updateAndroidSecurityProvider(context: Context) {
        try {
            ProviderInstaller.installIfNeeded(context)
        } catch (e: GooglePlayServicesRepairableException) {
            // Indicates that Google Play services is out of date, disabled, etc.
            e.printStackTrace()
            // Prompt the user to install/update/enable Google Play services.
            GoogleApiAvailability.getInstance()
                .showErrorNotification(context, e.connectionStatusCode)
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Indicates a non-recoverable error; the ProviderInstaller is not able
            // to install an up-to-date Provider.
            e.printStackTrace()
        }
    }
}
