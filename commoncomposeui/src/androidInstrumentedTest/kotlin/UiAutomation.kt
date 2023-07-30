import androidx.test.uiautomator.UiDevice

interface UiAutomation {
    val device: UiDevice
    fun grantPermission()
    fun denyPermission()
}