Media streaming with ExoPlayer
===
> The code in this repository accompanies the [Media streaming with ExoPlayer codelab](https://codelabs.developers.google.com/codelabs/exoplayer-intro). If you are looking to get started with [ExoPlayer](https://exoplayer.dev) the codelab is a great place to start.

## Import the project
- Start Android Studio.
- Choose File > New > Import Project*.*
- Select the root `build.gradle` file.

You can switch to the starting state of any step in this codelab by changing exoplayer-codelab-00 to explayer-codelab-N, where N is the step number. After doing so, do File -> Sync Project With Gradle Files.

#### app/build.gradle
```bash
dependencies {
   implementation project(":exoplayer-codelab-00")
}
```
