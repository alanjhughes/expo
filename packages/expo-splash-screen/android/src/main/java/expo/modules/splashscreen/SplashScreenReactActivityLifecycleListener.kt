package expo.modules.splashscreen

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.facebook.react.ReactRootView
import expo.modules.core.interfaces.ReactActivityLifecycleListener
import expo.modules.splashscreen.singletons.SplashScreen

// this needs to stay for versioning to work
/* ktlint-disable no-unused-imports */
import expo.modules.splashscreen.SplashScreenImageResizeMode

// EXPO_VERSIONING_NEEDS_EXPOVIEW_R
/* ktlint-enable no-unused-imports */

class SplashScreenReactActivityLifecycleListener : ReactActivityLifecycleListener {
  override fun onCreate(activity: Activity, savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
      // To support backward compatible or SplashScreenImageResizeMode customization
      // that calling `SplashScreen.show()` in MainActivity,
      // we postpone the in-module call to the end of main loop.
      // If MainActivity.onCreate has `SplashScreen.show()`, it will override the call here.
      Handler(activity.mainLooper).post {
        SplashScreen.show(
          activity,
          getResizeMode(activity),
          ReactRootView::class.java,
          getStatusBarTranslucent(activity)
        )
      }
      return
    }

    installSplashScreen(activity)
  }

  private fun installSplashScreen(activity: Activity) {
    val splashScreen = activity.installSplashScreen()
    splashScreen.setKeepOnScreenCondition {
      SplashScreen.splashScreenVisible
    }

    splashScreen.setOnExitAnimationListener { splashScreenView ->
      val fadeOut = ObjectAnimator.ofFloat(
        splashScreenView.view,
        View.ALPHA,
        1f,
        0f
      ).apply {
        interpolator = AnticipateInterpolator()
        duration = 800L
        doOnEnd { splashScreenView.remove() }
      }
      fadeOut.start()
    }
  }

  private fun getResizeMode(context: Context): SplashScreenImageResizeMode =
    SplashScreenImageResizeMode.fromString(
      context.getString(R.string.expo_splash_screen_resize_mode).lowercase()
    )
      ?: SplashScreenImageResizeMode.CONTAIN

  private fun getStatusBarTranslucent(context: Context): Boolean =
    context.getString(R.string.expo_splash_screen_status_bar_translucent).toBoolean()
}
