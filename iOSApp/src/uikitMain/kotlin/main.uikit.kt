/*
 * Copyright 2020-2022 JetBrains s.r.o. and respective authors and developers.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE.txt file.
 */

// Use `xcodegen` first, then `open ./ComposeMinesweeper.xcodeproj` and then Run button in XCode.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.cinterop.*
import platform.UIKit.*
import platform.Foundation.*
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import dev.baseio.slackclone.*

fun main() {
  val args = emptyArray<String>()
  memScoped {
    val argc = args.size + 1
    val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
    autoreleasepool {
      UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
    }
  }
}

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
  companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

  private val lifecycle = LifecycleRegistry()
  val skKeyValueData = SKKeyValueData(this)
  val root by lazy {
    RootComponent(
      context = DefaultComponentContext(lifecycle = lifecycle),
      skKeyValueData
    )
  }

  @ObjCObjectBase.OverrideInit
  constructor() : super()

  private var _window: UIWindow? = null
  override fun window() = _window
  override fun setWindow(window: UIWindow?) {
    _window = window
  }

  override fun application(
    application: UIApplication,
    supportedInterfaceOrientationsForWindow: UIWindow?
  ): UIInterfaceOrientationMask {
    return UIInterfaceOrientationMaskAll
  }

  override fun application(application: UIApplication, didFinishLaunchingWithOptions: Map<Any?, *>?): Boolean {
    window = UIWindow(frame = UIScreen.mainScreen.bounds)
    window!!.rootViewController = Application("SlackComposeiOS") {

      val rememberedComposeWindow by remember(window!!) {
        val windowInfo = window!!.frame.useContents {
          WindowInfo(this.size.width.dp, this.size.height.dp)
        }
        mutableStateOf(windowInfo)
      }

      val driver = DriverFactory().createDriver(SlackDB.Schema)
      CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
      ) {
        SlackCloneTheme(isDarkTheme = true) {
          Column {
            Box(Modifier.height(48.dp).background(SlackCloneColorProvider.colors.appBarColor))
            App(sqlDriver = driver, skKeyValueData = skKeyValueData, rootComponent = {
              root
            })
          }
        }

      }
    }
    window!!.makeKeyAndVisible()
    return true
  }

  override fun applicationDidBecomeActive(application: UIApplication) {
    lifecycle.resume()
  }

  override fun applicationWillResignActive(application: UIApplication) {
    lifecycle.stop()
  }

  override fun applicationWillTerminate(application: UIApplication) {
    lifecycle.destroy()
  }
}