
# Guide for developers

## Java version
The project is currently using **Java 17**. Please make sure to set up the correct version before trying to run or build the project.

The project is kept on the oldest still officially supported JAVA version to make sure the developers of all dependencies have time to release new versions of their libraries and to ensure maximum compatibility for the users of the JAR distributions.

## Building and running the project

### Using IDE
To run the project from IDE use [KarediAppLauncher.java](https://github.com/Nianna/Karedi/blob/master/src/main/java/com/github/nianna/karedi/KarediAppLauncher.java).

To ensure all features work properly make sure to add the following VM options:
```
--add-opens java.desktop/com.sun.media.sound=karedi --add-exports=javafx.base/com.sun.javafx.event=org.controlsfx.controls --add-exports=javafx.graphics/com.sun.javafx.scene=org.controlsfx.controls
```

### JAR
In order to create a fat jar with all dependencies, please run `mvn package` command.
This will create a JAR file in `target/dist` directory.

For example, you can run the built program like this:
```bash
mvn package  # Compile, build, and run tests
java -jar target/dist/Karedi-1.7.0.jar  # Replace "1.7.0" with current version
```

If building fails, double-check the java version:
```bash
java -version
```

Note: Since JavaFX libraries are platform-specific, without additional config only libraries for your current platform will be included in the jar.
To create a jar supporting multiple operating systems please go to the [multi-system fat jar section](#multi-system-fat-jar).

### System-specific installer

Installers are created using [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html) tool.

You can create the installer **only for the platform that you are currently on!**

#### Set-up
Make sure to install all dependencies required by jpackage. The list of required steps differs for each platform and is outside the scope of this readme.

#### Creating the installer
Once `jpackage` is properly set-up, you can run the command dedicated to your operating system:
* Windows: `mvn clean jpackage jpackage@win`
* Linux: `mvn clean jpackage jpackage@linux`
* macOS: `mvn clean jpackage jpackage@mac`

The installer will be created in the `target/dist` directory. After installation, you can just run the app - no additional steps are required.

## Releasing the project

### Multi-system fat jar
The jar created by `mvn package` is a fat jar with all dependencies. However, there is a catch - JavaFX libraries are platform-specific.
There is a separate version for Windows, Aarch64 macOS, x86_64 macOS, Aarch64 Linux and x86_64 Linux.
You can include multiple variants - one for each operating systems - in the fat jar, but you can't simultaneously support both types of architecture.

#### x86_64 multi-system jar
To create a jar supporting x86_64 Windows, Linux & macOS you have to **TEMPORARILY** add the following dependencies to the pom.xml file:
```
		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux</classifier>
		</dependency>
		

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac</classifier>
		</dependency>
		
		
		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>
```

and then create the JAR as usual using `mvn package`.

**NEVER COMMIT THESE CHANGES!**

#### Aarch64 multi-system jar
To create a jar supporting Windows, Aarch64 Linux & Aarch64 macOS you have to **TEMPORARILY**  add the following dependencies to the pom.xml file:
```
		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>mac-aarch64</classifier>
		</dependency>


		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux-aarch64</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>linux-aarch64</classifier>
		</dependency>
		
		
		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-base</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-controls</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-graphics</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>

		<dependency>
			<groupId>org.openjfx</groupId>
			<artifactId>javafx-fxml</artifactId>
			<version>${javafx.version}</version>
			<classifier>win</classifier>
		</dependency>
```

and then create the JAR as usual using `mvn package`.

**NEVER COMMIT THESE CHANGES!**


### System-specific installers
To create a single installer use steps described in [build & run section](#system-specific-installer). 

Since jpackage lets you create an installer only for the platform you are on, to create a set of installers for various operating systems and architectures, you have to repeat these steps on each of the systems.

Jpackage packages required modules of Java into the installer. Therefore, to avoid potentially violating any licences, it is advised to run these commands using some open-source implementation of the Java Platform e.g. OpenJDK.


## Adding new dependencies

This project is modular. Since all dependencies (including JRE) are packaged into the installer, without project Jigsaw the size of this application would be unnecessarily big.
Using modules lets reduce the application size while still keeping it self-sufficient.

The downside is that it significantly complicates the process of adding new non-modular dependencies.

### Double-check
Before adding any new dependency make sure it is absolutely necessary. Check licences and library size. Prefer actively supported and modular libraries over other alternatives.
If you are sure you have chosen the best necessary alternative, please proceed to the steps below.

### Adding a new modular dependency
If this new library is modular (contains module-info.java), you can just add it in the pom like usual. No additional steps are necessary.

### Adding a new non-modular dependency
If the new dependency is not modular, more steps are needed to make sure everything works properly in all distributions.

First, add the dependency as usual and make sure your code is working properly:
* from IDE (check if some additional `add-opens` directives are necessary for the new feature to work)
* from fat jar.
Test if on multiple operating systems and architectures if you can.

Please note that without additional config, installer creation will fail.

If you have tested the feature as described above, feel free to just create a pull request and I will add the required additional configuration for you.

However, if you want to try to fix installer creation yourself, here are some tips.

#### How to fix the installer

To keep everything modular, jpackage unfortunately requires all dependencies to explicitly contain `module-info.java` in the jars.
You can either contribute in the project that you want to add as dependency and ask the owner to release a new modular version of the library, or you can use a "hack" - direct Moditect maven plugin to create a `module-info.java` file and inject it into the library jar before it gets used by jpackage.

Let's first analyze some config of Moditect plugin for one dependency (taken from the `pom.xml` file):
```xml
<module>
    <artifact>
        <groupId>com.googlecode.soundlibs</groupId>
        <artifactId>mp3spi</artifactId>
        <version>${mp3spi.version}</version>
    </artifact>
    <moduleInfoSource>
        module mp3spi {
            requires java.desktop;
            requires java.logging;
            requires tritonus.share;
            requires jlayer;
    
            exports javazoom.spi.mpeg.sampled.convert;
            exports javazoom.spi.mpeg.sampled.file;
    
            provides javax.sound.sampled.spi.AudioFileReader
            with javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
            provides javax.sound.sampled.spi.FormatConversionProvider
            with javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider;
        }
    </moduleInfoSource>
</module>
```
In the `artifact` section you describe for which dependency you want to inject the module-info file and in `moduleInfoSource` you configure the actual contents of the file.

Please note that the module name:
```
module mp3spi {
```
must be the same as the one in Karedi's `module-info.java` requires directive:
```java
module karedi {
    // (...)
    requires mp3spi;
    // (...)
}
```

Beware that if you require some other transitive dependency e.g.
```
        requires tritonus.share;
```

You **will have to** add `module` config for that library as well (if it is already not modular of course)


To add config for new dependency start just by adding an empty module, for example
```xml 
<module>
    <artifact>
        <groupId>com.foo.bar</groupId>
        <artifactId>example</artifactId>
        <version>${example.version}</version>
    </artifact>
    <moduleInfoSource>
        module example {
        }
    </moduleInfoSource>
</module>
```

This will fix the jpackage errors - installer will be created and you will be able to install the app.
Obviously, with invalid module config for our example dependency, the app will either not start at all or it will start but the new feature which uses this library will not work.
Either way, regardless of the operating system, make sure to start the app from terminal and redirect stderr and stdout to some text files.
For example in Windows Powershell the command may look like this:
```bash
Start-Process -RedirectStandardOutput stdout.log -RedirectStandardError stderr.log .\Karedi.exe
```
Check the log files to see what declaration is missing, add it to the module configuration and repeat all steps - rebuild the project, create installer, install and re-run the app.
This iterative approach is tedious, but will result in the smallest module-info file required for the feature to work.


You can also try a different approach - download the sources of the library and try to create a module-info file there - in that project. Once it's complete, copy its contents to the pom.xml file here.
This may be easier, but the resulting module-info file will be bigger than necessary since it will include some required modules that may not be used by the classes we need, but by some other unused parts of the library.
Therefore, if using this approach, please check if all directives are necessary for the feature to work. If not, remove them.

## Troubleshooting

### Build fails
Please double-check the java version used to build the project.

### Application throws IllegalAccessError
If you are running the app from IDE accessing some features e.g. creating a new song, may result in IllegalAccessError, e.g.
```
Caused by: java.lang.IllegalAccessError: class org.controlsfx.control.textfield.AutoCompletionBinding (in module org.controlsfx.controls) cannot access class com.sun.javafx.event.EventHandlerManager (in module javafx.base) because module javafx.base does not export com.sun.javafx.event to module org.controlsfx.controls
```
To fix this, double-check your run configuration and make sure you have passed the necessary `add-opens` VM options as described in [Using IDE section](#using-ide)

### Jpackage fails due to FindException/ConfigException
Sometimes running the jpackage commands fails with exception similar to this:
```
Caused by: java.lang.module.FindException: Two versions of module javafx.fxml found in C:\Users\nianna\.m2\repository\org\openjfx\javafx-fxml\19.0.2.1 (javafx-fxml-19.0.2.1-linux.jar and javafx-fxml-19.0.2.1-linux-aarch64.jar)
```
This usually happens if you have previously released a multi-platform jar and therefore have multiple variants of JavaFX libraries.
The tool does not know which version to include.

First, please make sure that all temporary config from pom.xml for multi-platform support is cleaned up.

Then, please go to the directory specified in the error message and make sure to leave only the jars needed for your current platform.
It may be easier to just remove all JavaFX libraries from the local repository and refresh the project to let maven re-download only libraries needed by your platform.

This error may also happen if e.g. you have instructed your IDE to download some other related jars e.g. sources. 

In all cases, the error message will let you know where the problem lies.

## License

The project is licensed under [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.en.html).
