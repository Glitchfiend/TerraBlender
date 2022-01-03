<p align="center"><img src="https://i.imgur.com/8VBgnKN.png"></p>

<p align="center"><img src="https://i.imgur.com/CYxKg5M.png"></p>

<p align="center">https://discord.gg/GyyzU6T</p>

**TerraBlender** is a **library mod** for adding biomes in a simple and compatible manner with Minecraft's new biome/terrain system.

-----------------

### Using TerraBlender

To configure your ``build.gradle`` to use **TerraBlender** you should:

1. Ensure you have the Forge Maven in your `buildscript -> repositories` block:

```groovy
buildscript {
    repositories {
        maven { url = 'https://maven.minecraftforge.net/' }
    }
    dependencies {
        ...
    }
}
```

2. Add **TerraBlender** to your dependencies block, ensuring you replace ``<Minecraft version>`` and ``<Mod version>`` as appropriate:

```groovy
dependencies {
    minecraft 'net.minecraftforge:forge:' <...>
    implementation 'com.github.glitchfiend:TerraBlender:<Minecraft version>-<Mod version>:deobf'
}
```

-----------------

 [<img src="https://licensebuttons.net/l/by-nc-nd/4.0/88x31.png">](http://creativecommons.org/licenses/by-nc-nd/4.0/deed.en_US)

TerraBlender is licensed under a [Creative Commons Attribution-NonCommercial-NoDerivs 4.0 Unported License](http://creativecommons.org/licenses/by-nc-nd/4.0/deed.en_US).