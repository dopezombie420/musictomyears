# Music to my Ears!

This mod ports the 1.7+ music system to legacy versions of Minecraft! It is written for Ornithe on versions 1.1-1.5.2 with 1.6.4 support coming soon.

### Configuration
Can be found in **minecraft/config/musictomyears.properties**
```
overrideMusic
boolean: can be set to true or false.

survivalDelay
array: defines the minimum delay and the random additional delay.

creativeDelay
array: defines the minimum delay and the random additional delay.

netherDelay
array: defines the minimum delay and the random additional delay.

endDelay
array: defines the minimum delay and the random additional delay.

menuDelay
array: defines the minimum delay and the random additional delay.
```

### Building

To build this mod, use this command:
`./gradlew.bat -Pmc="1.VERSION.HERE" build`

To generate a project for the Eclipse IDE, use this command:
```
./gradlew.bat -Pmc="1.VERSION.HERE" genSources
./gradlew.bat -Pmc="1.VERSION.HERE" eclipse
```

### Copying
You are welcome to port this mod with given credit. If you are interested in including in a mod/modpack, please get permission from me first.

