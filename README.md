<h1 align="center">Trans App Mobile</h1>

<p align="center"><a href="https://www.imostate.gov.ng/"><img alt="Website" src="https://img.shields.io/badge/Website-Imo%20Govt-yellowgreen"/></a> </p>

<p align="center">
  <a href="https://www.android.com/"><img alt="Platform" src="https://img.shields.io/badge/Platform-Android-white"/></a>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-yellow.svg?style=flat"/></a>
  <a href="https://github.com/imofintech/transapp-mobile/actions"><img alt="Build Status" src="https://github.com/imofintech/transapp-mobile/workflows/Build/badge.svg"/></a> 
</p>

## Configurations
- Minimum SDK level 21
- Compile SDK version 32
- Version code 1
- Version name "1.0"

## Tech stack & Third-party libraries
- [Kotlin](https://kotlinlang.org/), [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous operations and background processes.
- Near Field Communication Tech (NDEF Standard)
- [ESC/POS Thermal Printer Library](https://github.com/DantSu/ESCPOS-ThermalPrinter-Android)
- Jetpack
  - Lifecycle - Observe Android lifecycles and handle UI states upon the lifecycle changes.
  - ViewModel - Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  - ViewBinding - Generates binding classes for XML layout files.
  - Room - Constructs Database by providing an abstraction layer over SQLite to allow fluent database access.
  - DataStore - Data storage solution for key-value pairs or typed objects with protocol buffers.
  - Hilt - Manage dependency injection.
- Architecture
  - MVVM Architecture (Model - View - ViewModel)
  - Repository Pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - Construct the REST APIs.
- [Moshi](https://github.com/square/moshi/) - A modern JSON library for Kotlin and Java.
- [Timber](https://github.com/JakeWharton/timber) - A logger with a small, extensible API.
- [Material-Components](https://github.com/material-components/material-components-android) - Material design components for building ripple animation, and CardView.
- Custom Views / Animations
  - [Lottie Animations](https://github.com/airbnb/lottie-android) - Apply animations.
  - [MotionToast](https://github.com/Spikeysanju/MotionToast) - A simple and beautiful way to show animated toast messages.
  
## App Architecture
This app is based on the MVVM architecture and the Repository pattern.

<p align="center">
<a href="https://developer.android.com/topic/architecture/"><img alt="Webpage" src="https://koenig-media.raywenderlich.com/uploads/2020/05/final-architecture-650x488.png"/></a> 
</p>

* Side Note: Instead of emitting LiveData, we have chosen to emit StateFlow instead which similar to LiveData can be observed for any changes by views. This allows maintaining a consistent flow of data stream while leveraging on the power of Kotlin Flow to transform the data stream.

## License
```xml

The Office of the Commissinor for Finance and Coordinating Economy reserves exclusive right to the use and 
ownership of this project.
```
