### Ruby Pit

**Ruby pit** is a simple text editor with Ruby syntax highlight.

#### Build and install

###### Requirements

1. The current implementation of IDE works on x64 Linux (it was tested on **Ubuntu 18.04**) or **Windows10 x64**.
1. Also you must have **openjdk v11** installed.
 
 
###### How to build the Ruby Pit

```
git clone https://github.com/cdeler/ruby_pit.git
cd ruby_pit
./gradlew :fatJar
```
After that you will be able to find runnable jar file in `build/libs/` directory. You can run it by
```
java -jar build/libs/rubyide-bundle-1.0-SNAPSHOT.jar
```

###### How to add new target platform
1. Build the [rubypit_native](https://github.com/cdeler/rubypit_native)
1. Then put the result artifact according to [this](https://github.com/scijava/native-lib-loader#package-native-libraries) scheme into the resource directory.

