package uitests.base

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector

class UiAutomationDelegateImpl : UiAutomation {

    override val device by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }

    override fun grantPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermission = device.findObject(
                UiSelector().text(
                    when {
                        Build.VERSION.SDK_INT == 23 -> "Allow"
                        Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                        Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                        else -> "While using the app"
                    }
                )
            )
            if (allowPermission.exists()) {
                allowPermission.click()
            }
        }
    }

    override fun denyPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val denyPermission = device.findObject(
                UiSelector().text(
                    when (Build.VERSION.SDK_INT) {
                        in 24..28 -> "DENY"
                        else -> "Deny"
                    }
                )
            )
            if (denyPermission.exists()) {
                denyPermission.click()
            }
        }
    }

}