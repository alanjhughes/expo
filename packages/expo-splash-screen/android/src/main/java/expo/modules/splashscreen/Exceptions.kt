package expo.modules.splashscreen

import expo.modules.kotlin.exception.CodedException

class NoContentViewException : CodedException("ContentView is not yet available. Call 'SplashScreen.show(...)' once 'setContentView()' is called.")

class SplashScreenException(message: String) : CodedException(message)
