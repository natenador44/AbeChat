# AbeChat Development/Contribution

## Development Environment

### Install Java 21
#### Debian-basked Linux (e.g. Ubuntu)
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
./gradlew bootJar
```
Then run this to start the server (again from the AbeChatServer root directory)
```shell
java -jar build/libs/AbeChatServer-0.0.1-SNAPSHOT.jar
```
The version of that jar may change in the future.

**Note: This approach requires Java 21 be set as your default Java runtime, or specified via the JAVA_HOME environment variable (I think, I haven't tested this out yet)**

#### Intellij IDEA
Find the `AbeChatServerApplication` class and press the run button intellij idea provides for you.

#### Docker Setup/Running, Linux
Install using below commands for CLI only, GUI also availible for Linux.
```shell
# Uninstall any conflicting packages
for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do sudo apt-get remove $pkg; done

# Setup docker repo, we want to use their sources
# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \ 
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# Install required docker packages
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Test with hello world, may need to start docker daemon manually
sudo service docker start
sudo docker run hello-world
```
https://docs.docker.com/engine/install/ubuntu/#install-from-a-package

Run post install steps, this sets up your user under the docker group to allow non-sudo access to containers.
```shell
sudo groupadd docker
sudo usermod -aG docker $USER
# Log out and back in for changes to take effect
```
https://docs.docker.com/engine/install/linux-postinstall/
