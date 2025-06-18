# AbeChat Development/Contribution

## Development Environment

### Install Java 21
#### Linux
Install with `apt`.
```shell
sudo apt install openjdk-21-jdk openjdk-21-dbg openjdk-21-source
```

If you want your system's default Java version to be 21, you can run this to set that up (if it isn't already).
```shell
sudo update-alternatives --config java
```
This is not required if you plan on running the server from an IDE. The IDE should be able to run using the correct
Java version once you set that up.

#### Windows
_shrug_

### IDE Setup

#### Intellij IDEA
1. Open up the Project Structure (File->Project Structure).
2. Select "SDKs" on the left hand side under Platform Settings
3. Add a new SDK (The '+' button)
4. Intellij IDEA should automatically see the installed Java 21. Select one of those if so, otherwise search for it manually. 
   - If you use linux, Java 21 should have been installed under `/usr/lib/jvm`
5. Under Project Settings, select the Project option
6. Make sure SDK and Language Level are set to 21
7. Press OK and exit the dialog. You should be set up now.

### Running the Server

#### Command Line
Run this to build the server from the AbeChatServer root directory.
```shell
./gradle bootJar
```
Then run this to start the server (again from the AbeChatServer root directory)
```shell
java -jar build/libs/AbeChatServer-0.0.1-SNAPSHOT.jar
```
The version of that jar may change in the future.

**Note: This approach requires Java 21 be set as your default Java runtime, or specified via the JAVA_HOME environment variable (I think, I haven't tested this out yet)**

#### Intellij IDEA
Find the `AbeChatServerApplication` class and press the run button intellij idea provides for you.