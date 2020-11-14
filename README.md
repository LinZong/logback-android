# logback-android [![CircleCI branch](https://img.shields.io/circleci/project/github/tony19/logback-android/master.svg)](https://circleci.com/gh/tony19/logback-android) [![Codacy Badge](https://api.codacy.com/project/badge/grade/c1d818d1911440e3b6d685c20a425856)](https://www.codacy.com/app/tony19/logback-android)
<sup>v2.0.0</sup>

<a href="https://opencollective.com/logback-android/donate" target="_blank">
  <img src="https://opencollective.com/logback-android/donate/button@2x.png?color=blue" width=250 />
</a>

Overview
--------
[`logback-android`][2] brings the power of [`logback`][1] to Android. This library provides a highly configurable logging framework for Android apps, supporting multiple log destinations simultaneously:

 * files
 * SQLite databases
 * logcat
 * sockets
 * syslog
 * email

Runs on Android 2.3 (SDK 9) or higher. See [Wiki][4] for documentation.

*For `v1.x`, see the [`1.x` branch](https://github.com/tony19/logback-android/tree/1.x).*

Quick Start
-----------
1. Create a new "Basic Activity" app in [Android Studio][3].

2. In `app/build.gradle`, add the following dependencies:

    ```groovy
    dependencies {
      compile 'org.slf4j:slf4j-api:1.7.25'
      compile 'com.github.tony19:logback-android:2.0.0'
    }
    ```

3. Create `app/src/main/assets/logback.xml` containing:

    ```xml
    <configuration>
      <appender name="logcat" class="ch.qos.logback.classic.android.LogcatAppender">
        <tagEncoder>
          <pattern>%logger{12}</pattern>
        </tagEncoder>
        <encoder>
          <pattern>[%-20thread] %msg</pattern>
        </encoder>
      </appender>

      <root level="DEBUG">
        <appender-ref ref="logcat" />
      </root>
    </configuration>
    ```

4. In `MainActivity.java`, add the following imports:

    ```java
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    ```

5. ...and modify `onOptionsItemSelected()` to log "hello world":

    ```java
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger log = LoggerFactory.getLogger(MainActivity.class);
        log.info("hello world");
        // ...
    }
    ```

6. Build and start the app.

7. Open logcat for your device (via the _Android Monitor_ tab in Android Studio).

8. Click the app menu, and select the menu-option. You should see "hello world" in logcat.

9. To use conditional statements, create `logback.xml` like below:

    ```xml
    <configuration>
        <property name="ANDROID_DIR" value="${DATA_DIR}/logs"/>
        <property name="PC_DIR" value="${user.home}/${project.name}/logs"/>
    
        <if condition='"${log.debug}" === "false" || "${log.debug}" === "log.debug_IS_UNDEFINED"'>
            <then>
                <statusListener class="ch.qos.logback.core.status.NopStatusListener" />
            </then>
        </if>
    
        <if condition='"${log.platform}" === "ANDROID"'>
            <then>
                <appender name="console" class="ch.qos.logback.classic.android.LogcatAppender">
                    <tagEncoder>
                        <pattern>%logger{12}</pattern>
                    </tagEncoder>
                    <encoder>
                        <pattern>[%thread] %msg</pattern>
                    </encoder>
                </appender>
            </then>
            <else>
                <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
                    <tagEncoder>
                        <pattern>%logger{12}</pattern>
                    </tagEncoder>
                    <encoder>
                        <pattern>[%date{yyyy-M-d HH:mm:ss.SSS}] [%level] [%thread] %msg%n</pattern>
                    </encoder>
                </appender>
            </else>
        </if>
    
        <appender name="file" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>${${log.platform}_DIR}/trace.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                <fileNamePattern>trace-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>50MB</maxFileSize>
                <maxHistory>7</maxHistory>
                <totalSizeCap>350MB</totalSizeCap>
            </rollingPolicy>
            <tagEncoder>
                <pattern>%logger{12}</pattern>
            </tagEncoder>
            <encoder>
                <pattern>[%date{yyyy-M-d HH:mm:ss.SSS}] [%level] [%thread] %msg%n</pattern>
            </encoder>
        </appender>
    
        <root level="INFO">
            <appender-ref ref="console"/>
            <appender-ref ref="file"/>
        </root>
    </configuration>
    ```


Download
--------
_Gradle_ **release**

```groovy
dependencies {
  implementation 'org.slf4j:slf4j-api:1.7.25'
  implementation 'com.github.tony19:logback-android:2.0.0'
  implementation 'io.apisense:rhino-android:1.1.1'
}
```

_Gradle_ **snapshot (unstable)**

```groovy
repositories {
  maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

dependencies {
  implementation 'org.slf4j:slf4j-api:1.7.25'
  implementation 'com.github.tony19:logback-android:2.0.1-SNAPSHOT'
  implementation 'io.apisense:rhino-android:1.1.1'
}
```

[1]: http://logback.qos.ch
[2]: http://tony19.github.com/logback-android
[3]: http://developer.android.com/sdk/index.html
[4]: https://github.com/tony19/logback-android/wiki

Practice: Include the latest local build to your Android project.
-----

1. Use these commands to create the AAR:

```shell
    git clone git://github.com/tony19/logback-android.git
    cd logback-android
    scripts/makejar.sh
```
​	Then the file is output to: `./build/logback-android-2.0.0-debug.aar`

2. Copy `./build/logback-android-2.0.0-debug.aar` to the `libs` folder under your Android project.

3. Ensure that the `build.gradle` of Android contains `*.aar` type like: `implementation fileTree(dir: 'libs', include: ['*.jar','*.aar'])`.

4. Add two dependencies:

   ```groovy
   dependencies {
       // ...
       implementation 'org.slf4j:slf4j-api:1.7.25'
       implementation 'io.apisense:rhino-android:1.1.1'
       // ...
   }
   ```

5. Add `logback.xml` to `src/main/assets`.

6. That is it. Enjoy!

