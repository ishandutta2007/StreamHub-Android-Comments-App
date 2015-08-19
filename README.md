StreamHub Android Comments Application
=============================
Livefyre Community Comments replaces your default comments with real-time conversations. Our social integration features make it easy to capture all the conversations going on about your posts across Twitter and Facebook, and pull your friends into the conversation.

Make Android apps powered by Livefyre StreamHub

Read the docs: [http://livefyre.github.com/StreamHub-Android-SDK/](http://livefyre.github.com/StreamHub-Android-SDK/)

---
Installation
=============================
Android Comment App Build Steps:

1. Clone this repo to get the project
2. Open Android Studio, File > Open, and open the comments app (cloned in step 1)
  * Optionally you can change the config params for the endpoints under StreamHub-Android-Comments-App/streamHubAndroidSDK/src/main/java/livefyre/streamhub/Config.java
3. Ensure that you have the <a href="https://github.com/Livefyre/StreamHub-Android-SDK.git">SDK</a> loaded into your environment
  * You might have to explictily "File" => "Import Module" and point to the StreamHub SDK location.
4. Click on the Build Gradle button or just "run" if you are using Android Studio as it will manage gradle explicitly with UI elements.
  * Clicking the Run button will open the app in the simulator or device if it is connected.

Customizing the SDK
=============================
In some cases, the following might be required when customizing the SDK:

1. git clone https://github.com/Livefyre/StreamHub-Android-SDK.git
2. In your example app go to "File" => Project Structure > select '+' in opened window > Select 'Import Existing Project' in 'More Modules' Section in opened window > Browse to the SDK location cloned in the step above.
3. In **build.gradle** file add following before running the app:

```
dependencies {
  compile files('libs/android-async-http.jar')
  compile files('libs/picasso-2.3.4.jar')
  compile project(':filePickerSDK')
  compile project(':streamHubAndroidSDK')
  compile 'com.squareup:otto:1.3.6'
}
```

---
Dependencies
=============================
This Livefyre StreamHub Comments example App uses the following dependencies:
* [StreamHub-Android-SDK](https://github.com/Livefyre/StreamHub-Android-SDK/)

* [Filepicker](https://github.com/Ink/filepicker-android)
 * Filepicker SDK is used to upload image/files to the host.
* [Asynchronous Http Client](https://github.com/loopj/android-async-http)
 * Android-async-http is an asynchronous, callback-based Http client for Android built on top of Apache's HttpClient libraries.
* [Picasso](https://github.com/square/picasso)
 * Picasso is an powerful image downloading and caching library for Android
* [Picasso](https://github.com/square/otto)
 * Otto is an event bus designed to decouple different parts of your application while still allowing them to communicate efficiently.

* In build.gradle the preferred version for targetSdkVersion is 20, but 21  will also work if using SDK Manager,  Android 5.0 (API 21) is  downloaded and installed

### Developers
Clone the project, run the tests, and notice a few undocumented classes. Kindly treat the project as alpha code.

## License

Copyright (c) 2015 Livefyre

Licensed under the MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
