package expo.modules.splashscreen

import android.os.Build
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.splashscreen.singletons.SplashScreen

class SplashScreenModule : Module() {
  private val currentActivity
    get() = appContext.currentActivity ?: throw Exceptions.MissingActivity()

  override fun definition() = ModuleDefinition {
    Name("ExpoSplashScreen")

    AsyncFunction("preventAutoHideAsync") { promise: Promise ->
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        SplashScreen.preventAutoHide(
          currentActivity,
          { hasEffect -> promise.resolve(hasEffect) },
          { m -> promise.reject(SplashScreenException(m)) }
        )
      } else {
        SplashScreen.splashScreenVisible = true
        promise.resolve(true)
      }
    }

    AsyncFunction("hideAsync") { promise: Promise ->
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        SplashScreen.hide(
          currentActivity,
          { hasEffect -> promise.resolve(hasEffect) },
          { m -> promise.reject(SplashScreenException(m)) }
        )
      } else {
        SplashScreen.splashScreenVisible = false
        promise.resolve(null)
      }
    }
  }
}
